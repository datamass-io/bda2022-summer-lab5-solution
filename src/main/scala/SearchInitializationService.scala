import com.azure.core.credential.AzureKeyCredential
import com.azure.search.documents.indexes.models._
import com.azure.search.documents.indexes._
import com.typesafe.scalalogging.LazyLogging

import java.time.Duration
import scala.jdk.CollectionConverters.SeqHasAsJava


class SearchInitializationService(searchEndpoint: String, searchApiKey: String, blobConnectionString: String) extends LazyLogging {

  def getSearchIndexClient: SearchIndexClient = {
    new SearchIndexClientBuilder().endpoint(searchEndpoint).credential(new AzureKeyCredential(searchApiKey)).buildClient
  }

  def getSearchIndexerClient: SearchIndexerClient = {
    new SearchIndexerClientBuilder().endpoint(searchEndpoint).credential(new AzureKeyCredential(searchApiKey)).buildClient
  }

  def createIndex(indexName: String, keyField: String, fields: List[String]): Unit = {
    val searchFieldList: List[SearchField] = new SearchField(keyField, SearchFieldDataType.STRING).setKey(true) ::
      fields.map((f: String) =>
        f match {
          case "show_number" => new SearchField(f, SearchFieldDataType.INT32)
          case _ => new SearchField(f, SearchFieldDataType.STRING)
        })
    val newIndex: SearchIndex = new SearchIndex(indexName, searchFieldList.asJava)
    getSearchIndexClient.createIndex(newIndex)
    logger.info(String.format("Created index %s.", newIndex))
  }

  def createDataSource(dataSourceName: String, blobContainerName: String): Unit = {
    val dataSource: String = createBlobDataSource(getSearchIndexerClient, dataSourceName, blobContainerName)
    logger.info(String.format("Created datasource %s.", dataSource))
  }

  def createIndexer(indexerName: String, dataSourceName: String, indexName: String): Unit = {
    // Create indexing configuration
    val indexingParametersConfiguration: IndexingParametersConfiguration = new IndexingParametersConfiguration
    // we set JSON_ARRAY since the JSON contains the array of documents
    indexingParametersConfiguration.setParsingMode(BlobIndexerParsingMode.JSON_ARRAY)
    // Create indexer parameters
    val indexingParameters: IndexingParameters = new IndexingParameters().setBatchSize(50).setMaxFailedItems(10).setMaxFailedItemsPerBatch(10).setIndexingParametersConfiguration(indexingParametersConfiguration)
    // Create schedule
    val indexingSchedule: IndexingSchedule = new IndexingSchedule(Duration.ofHours(12))
    // Create the indexer
    val indexer: SearchIndexer = new SearchIndexer(indexerName, dataSourceName, indexName).setParameters(indexingParameters).setSchedule(indexingSchedule)
    logger.info(String.format("Creating Indexer: %s", indexer.getName))
    val createdIndexer: SearchIndexer = getSearchIndexerClient.createOrUpdateIndexer(indexer)
    logger.info(String.format("Created indexer name: %s, ETag: %s", createdIndexer.getName, createdIndexer.getETag))
  }

  private def createBlobDataSource(client: SearchIndexerClient, dataSourceName: String, containerName: String): String = {
    createDataSourceHelper(client, SearchIndexerDataSourceType.AZURE_BLOB, dataSourceName, blobConnectionString, new SearchIndexerDataContainer(containerName), null)
  }

  private def createDataSourceHelper(client: SearchIndexerClient, sourceType: SearchIndexerDataSourceType, dataSourceName: String, connectionString: String, container: SearchIndexerDataContainer, dataChangeDetectionPolicy: DataChangeDetectionPolicy): String = {
    val dataSource: SearchIndexerDataSourceConnection = createSampleDatasource(dataSourceName, sourceType, connectionString, container, dataChangeDetectionPolicy)
    try client.createOrUpdateDataSourceConnection(dataSource)
    catch {
      case ex: Exception =>
        System.err.println(ex.toString)
    }
    dataSource.getName
  }

  private def createSampleDatasource(dataSourceName: String, sourceType: SearchIndexerDataSourceType, connectionString: String, container: SearchIndexerDataContainer, dataChangeDetectionPolicy: DataChangeDetectionPolicy): SearchIndexerDataSourceConnection = {
    new SearchIndexerDataSourceConnection(dataSourceName, sourceType, connectionString, container).setDataChangeDetectionPolicy(dataChangeDetectionPolicy)
  }

}

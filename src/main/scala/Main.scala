/* ENV VARIABLES
 * export BLOB_STORAGE_CONNECTION_STRING="blobstorageconnectionstring"
 * export AZURE_COGNITIVE_SEARCH_ENDPOINT="https://<yoursearchservicename>.search.windows.net"
 * export AZURE_COGNITIVE_SEARCH_API_KEY="<yoursearchapikey>"
 */

import com.azure.core.credential.AzureKeyCredential
import com.azure.core.util.{Configuration, Context}
import com.azure.search.documents.{SearchClientBuilder, SearchDocument}
import com.azure.search.documents.models.{QueryType, SearchMode, SearchOptions}
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.typesafe.scalalogging.LazyLogging

import java.io.File

object Main extends LazyLogging {
  private val BLOB_STORAGE_CONNECTION_STRING = Configuration.getGlobalConfiguration.get("BLOB_STORAGE_CONNECTION_STRING")
  // AZURE_COGNITIVE_SEARCH_ENDPOINT https://<name>.search.windows.net
  private val ENDPOINT = Configuration.getGlobalConfiguration.get("AZURE_COGNITIVE_SEARCH_ENDPOINT")
  // AZURE_COGNITIVE_SEARCH_API_KEY <KEY>
  private val ADMIN_KEY = Configuration.getGlobalConfiguration.get("AZURE_COGNITIVE_SEARCH_API_KEY")
  private val CONTAINER_NAME = "search"
  private val INDEX_NAME = "questions-index"
  private val DATA_SOURCE_NAME = "questions-datasource"
  private val INDEXER_NAME = "questions-indexer"

  def main(args: Array[String]): Unit = {
    logger.info("Hello Azure Cognitive Search!")
    logger.info("Uploading question file.")
    // TODO (part 1)
    //  using StorageDataUploader upload ./data/question file to the container
    val questions = new File("./data/questions.json")
    val storageDataUploader = new StorageDataUploader(BLOB_STORAGE_CONNECTION_STRING, CONTAINER_NAME)
    storageDataUploader.uploadQuestionFile(questions)
    logger.info("DONE: Question file is in the storage.")
    // END TODO (part 1)
/*
    // TODO (part 2 - comment this part after it served its purpose)
    //  using SearchInitializationService do the following 1, 2 and 3
    val searchInitializationService = new SearchInitializationService(ENDPOINT, ADMIN_KEY, BLOB_STORAGE_CONNECTION_STRING)
    //  1. Create an index "questions-index" for fields, id (KEY), category, question, answer, round and show number
    logger.info("Creating the index...")
    searchInitializationService.createIndex(INDEX_NAME, "id", List("category", "question", "answer", "round", "show_number"))
    //  2. Create datasource from the questions.json uploaded to the storage
    logger.info("Creating the datasource...")
    searchInitializationService.createDataSource(DATA_SOURCE_NAME, CONTAINER_NAME)
    //  3. Create indexer
    searchInitializationService.createIndexer(INDEXER_NAME, DATA_SOURCE_NAME, INDEX_NAME)
    // END TODO (part 2)
    // TODO it may take up to 30 min to parse and index 216930 questions
    //  but you can test your queries on incomplete data
    //  you can check the indexer progress in search service overview / Indexers
    // TODO (part 3 - search time, comment out part 2 - we don't want to recreate index, datasource or update the indexer, uncomment part 3)
*/

    logger.info("Questions...")
    // Jackson object mapper
    val objectMapper = new ObjectMapper()
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    val searchClient = new SearchClientBuilder()
      .endpoint(ENDPOINT)
      .credential(new AzureKeyCredential(ADMIN_KEY))
      .indexName(INDEX_NAME)
      .buildClient()

    val searchOptions = new SearchOptions()
      .setIncludeTotalCount(true)
      .setSearchFields("id, category, question, answer, round")
      .setFilter("show_number le 300")
      .setSearchMode(SearchMode.ANY)
      .setQueryType(QueryType.FULL)
      .setTop(20)

    val results = searchClient
      .search("frence~", searchOptions, Context.NONE)

    val count = results.getTotalCount
    logger.info(s"Result count: $count")

    results.forEach(r => {
      val doc = r.getDocument(classOf[SearchDocument])
      // typed version
      val question: Question = objectMapper.convertValue(doc, classOf[Question])
      logger.info(s"Score ${r.getScore} -> document: ${question.toString}")
    })

    // THE END
  }
}

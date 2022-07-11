import com.azure.storage.blob._
import com.typesafe.scalalogging.LazyLogging
import java.io.File

class StorageDataUploader() extends LazyLogging {

  var containerClientOption: Option[BlobContainerClient] = None

  def this(connectionString: String, containerName: String) {
    this()
    val blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient
    var containerClientTemp: BlobContainerClient = blobServiceClient.getBlobContainerClient(containerName)
    if (!containerClientTemp.exists) {
      logger.info("There is no container: \"" + containerName + "\". Creating...")
      containerClientTemp = blobServiceClient.createBlobContainer(containerName)
    }
    else logger.info("There is already container: \"" + containerName + "\". Skip this step.")
    containerClientOption = Some(containerClientTemp)
  }

  def uploadQuestionFile(file: File): Unit = {
    val blobClient = containerClientOption.get.getBlobClient(file.getName)
    if (!blobClient.exists) {
      logger.info("There is no file: \"" + file + "\". Uploading...")
      blobClient.uploadFromFile(file.getPath)
    }
    else logger.info("There is already file: \"" + file + "\". Skip this step.")
  }
}

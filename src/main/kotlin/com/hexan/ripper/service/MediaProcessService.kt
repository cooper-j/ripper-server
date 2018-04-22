package com.hexan.ripper.service

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectRequest
import com.hexan.ripper.RipperApplication
import com.hexan.ripper.model.Song
import com.hexan.ripper.repo.SongRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.Charset


@Service
class MediaProcessService {
    private val logger = LoggerFactory.getLogger(RipperApplication::class.java)

    @Autowired
    lateinit var taskExecutor: TaskExecutor

    @Autowired
    lateinit var applicationContext: ApplicationContext

    fun executeAsynchronously(song: Song) {
        logger.debug("executeAsynchronously")
        taskExecutor.execute {
            logger.debug("taskExecutor execute")
            val myThread = applicationContext.getBean(MediaProcessThread::class.java)
            myThread.song = song
            taskExecutor.execute(myThread)
        }
    }
}

@Configuration
class MediaProcessThreadConfig {
    @Bean
    fun threadPoolTaskExecutor(): TaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 4
        executor.maxPoolSize = 4
        executor.setThreadNamePrefix("mediaprocess_task_executor_thread")
        executor.initialize()
        return executor
    }
}

@Component
@Scope("prototype")
class MediaProcessThread : Runnable {
    private val logger = LoggerFactory.getLogger(RipperApplication::class.java)

    lateinit var song: Song

    @Autowired
    lateinit var songRepository: SongRepository

    override fun run() {
        logger.debug("MediaProcessThread started")
        val mediaFile = File("/tmp/mediaprocess/" + System.currentTimeMillis() + ".tmp")
        mediaFile.parentFile.mkdirs()
        mediaFile.createNewFile()
        logger.debug("Created file: " + mediaFile.absoluteFile)
        logger.debug("Getting media file: " + song.mediaUrl)
        ProcessBuilder("wget", song.mediaUrl, "-O", mediaFile.absolutePath).redirectErrorStream(true).start()

        logger.debug("Start ffmpeg")
        val processDuration = ProcessBuilder("ffmpeg", "-i", mediaFile.absolutePath, "-c:a", "aac", "-b:a", "80k", "" + System.currentTimeMillis() + ".m4a")
                .redirectErrorStream(true).start()
        val strBuild = StringBuilder()
        BufferedReader(InputStreamReader(processDuration.inputStream, Charset.defaultCharset())).use { processOutputReader ->
            var line: String? = processOutputReader.readLine()
            while (line != null) {
                strBuild.append(line + System.lineSeparator())
                line = processOutputReader.readLine()
            }
            processDuration.waitFor()
        }
        val outputJson = strBuild.toString().trim { it <= ' ' }
        //need to upload back into aws
        logger.debug("Start uploadFileToAWS")
        uploadFileToAWS(song.mediaUrl, mediaFile)

        song.processing = false
        songRepository.save(song)
    }

    private fun uploadFileToAWS(mediaUrl: String, mediaFile: File) {

        val s3 = AmazonS3Client()
        val usWest2 = Region.getRegion(Regions.US_WEST_2)
        s3.setRegion(usWest2)
        val bucketName = "storage-hexan"
        val key = mediaUrl.substring(mediaUrl.indexOf(bucketName) + bucketName.length + 1, mediaUrl.lastIndexOf(".")) + ".m4a"
        logger.debug("AWS key: " + key)
        try {

            logger.debug("Uploading a new object to S3 from a file\n")
            s3.putObject(PutObjectRequest(bucketName, key, mediaFile))

            song.mediaUrl = "https://s3.eu-west-3.amazonaws.com/$bucketName/$key"
            songRepository.save(song)

            /*System.out.println("Downloading an object")
            val s3object = s3.getObject(GetObjectRequest(bucketName, key))
            s3object.
            System.out.println("Content-Type: " + s3object.objectMetadata.contentType)*/

        } catch (ase: AmazonServiceException) {
            logger.debug("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.")
            logger.debug("Error Message:    " + ase.message)
            logger.debug("HTTP Status Code: " + ase.statusCode)
            logger.debug("AWS Error Code:   " + ase.errorCode)
            logger.debug("Error Type:       " + ase.errorType)
            logger.debug("Request ID:       " + ase.requestId)
        } catch (ace: AmazonClientException) {
            logger.debug("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.")
            logger.debug("Error Message: " + ace.message)
        }
    }
}
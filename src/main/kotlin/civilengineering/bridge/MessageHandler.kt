package civilengineering.bridge

import civilengineering.CivilEngineering
import civilengineering.Config
import java.io.DataOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentLinkedQueue


object MessageHandler {

    private fun createThread(): CancellableConnectionFollowThread {
        return CancellableConnectionFollowThread(
                {
                    CivilEngineering.logger.info("Connecting to bridge server @ " + Config.connectURL)
                    val httpConn = URL(Config.connectURL + "/api/stream").openConnection() as HttpURLConnection
                    if (Config.authToken.isNotBlank())
                        httpConn.setRequestProperty("Authorization", "Bearer ${Config.authToken}")
                    httpConn
                },
                {
                    rcvQueue.add(
                            ApiMessage.decode(it)
                    )
                    CivilEngineering.logger.trace("received: " + it)
                }
        )
    }

    private var cancellableThread: CancellableConnectionFollowThread = createThread()

    private var xmitQueue = ConcurrentLinkedQueue<ApiMessage>()

    var rcvQueue = ConcurrentLinkedQueue<ApiMessage>()

    fun transmit(msg: ApiMessage) {
        CivilEngineering.logger.info("transmitting " + msg)
        transmitMessage(msg)
        //TODO: create thread with Runnable(sendstuff).execute()
    }

    fun stop() {
        cancellableThread.abort()
        CivilEngineering.logger.info("bridge closed ")
    }

    fun start(): Boolean {
        if (cancellableThread.isInterrupted) {
            CivilEngineering.logger.info("rebuilding bridge")
            cancellableThread = createThread()
        }
        if (!cancellableThread.isAlive) {
            cancellableThread.start()
            return true
        }
        return false
    }

    @Throws(IOException::class)
    private fun transmitMessage(message: ApiMessage) {
        //open a connection
        val url = URL(Config.connectURL + "/api/message")
        val urlConnection = url.openConnection()
        val connection = urlConnection as HttpURLConnection

        //configure the connection
        connection.allowUserInteraction = false
        connection.instanceFollowRedirects = true
        connection.setRequestProperty("Content-Type", "application/json")
        connection.requestMethod = "POST"
        if (Config.authToken.isNotEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer " + Config.authToken)
        }

        //encode the ApiMessage for sending
        val json = message.encode()

        //send the message
        connection.doOutput = true
        val post = DataOutputStream(connection.outputStream)
        post.writeBytes(json)
        post.flush()
        post.close()

        if (connection.responseCode != 200) {
            CivilEngineering.logger.error("Server returned " + connection.responseCode)
        }
    }
}


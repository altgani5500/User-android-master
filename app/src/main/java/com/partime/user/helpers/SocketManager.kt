package com.partime.user.helpers

import android.content.Context
import android.os.Handler
import android.os.Looper
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import okhttp3.OkHttpClient
import java.security.cert.X509Certificate
import javax.net.ssl.*


/**
 * Created by Techugo on 9/12/2017.
 */

class SocketManager
private constructor(context: Context) {

    var socketId = ""

    val isConnected: Boolean get() = socket.connected()

    /*
    Method to Initialize and Add Listener on Socket
    */
    fun initialize(socketListener: SocketListener) {
        try {

            /****************SSL***/
            val myHostnameVerifier = object : HostnameVerifier {
                override fun verify(hostname: String, session: SSLSession): Boolean {
                    return true
                }
            }
            val mySSLContext = SSLContext.getInstance("TLS")
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }


                override fun checkClientTrusted(
                    chain: Array<X509Certificate>,
                    authType: String
                ) {
                }

                override fun checkServerTrusted(
                    chain: Array<X509Certificate>,
                    authType: String
                ) {
                }
            })

            mySSLContext.init(null, trustAllCerts, java.security.SecureRandom())
            val okHttpClient = OkHttpClient.Builder()
                .hostnameVerifier(myHostnameVerifier)

                .sslSocketFactory(mySSLContext.socketFactory, object : X509TrustManager {

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }


                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {

                    }

                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {

                    }
                })
                .build()


            val options = IO.Options()
            options.webSocketFactory = okHttpClient
            //options.secure = true;
            options.transports = arrayOf(WebSocket.NAME)
            /*End of ssl**********************************************************/

            socket = IO.socket(socketUrl,options)

            socket.on(Socket.EVENT_CONNECT) {

                Handler(Looper.getMainLooper()).post {
                    socketListener.onConnected()
                    if (socket.connected()) {
                        socketId = socket.id()
                    }
                }
            }.on(Socket.EVENT_DISCONNECT) { args ->
                Handler(Looper.getMainLooper()).post {
                    if (args != null && args.isNotEmpty()) {

                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /*
    Method to connect Socket
     */
    fun connect() {
        if (!socket.connected())
            socket.connect()
    }

    /*
    Method to send message via Socket on a Key
     */
    fun sendMsg(key: String, vararg args: Any) {
        if (socket.connected()) {
            socket.emit(key, *args)
        }
    }

    /*
    Add Listener to Socket
     */
    fun addListener(key: String, socketMessageListener: SocketMessageListener) {
        socket.on(key) { args ->
            Handler(Looper.getMainLooper()).post {
                if (args != null && args.isNotEmpty()) {
                    socketMessageListener.onMessage(*args)
                }
            }
        }
    }

    /*
    Disconnect Socket
     */
    fun disConnect() {
        if (socket.connected())
            socket.disconnect()
    }

    /*
    Interface to Handle Connect and Disconnect events of Socket
     */
    interface SocketListener {
        fun onConnected()
        fun onDisConnected()

    }

    /*
   Interface to Handle Message event of Socket
    */
    interface SocketMessageListener {
        fun onMessage(vararg args: Any)
    }

    companion object {
        lateinit var socket: Socket
        var socketManager: SocketManager? = null

var myport =":8888"

         internal var socketUrl = "https://www.partime.org"+myport



        //internal var socketUrl = "https://www.partime.org:5454"
        //internal var socketUrl = "http://192.168.1.51:9292"
        //Hi.. all please use new socket url https://app.sterkla.com:9292


        /**
         * Method to get the instance of socket class
         *
         * @param context
         * @return
         */


        fun getInstance(context: Context): SocketManager {
            if (socketManager == null) {
                socketManager = SocketManager(context)
            }


            return socketManager as SocketManager
        }
    }

}
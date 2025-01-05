package tn.esprit.projet_.ui.screens
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URI

object SocketManager {
    private var socket: Socket? = null

    init {
        socket = IO.socket(URI.create("http://your-server-url.com")) // Replace with your server URL
        socket?.connect()
    }

    fun sendMessage(message: String, roomId: String) {
        socket?.emit("sendMessage", roomId, message)
    }

    fun receiveMessage(callback: (String) -> Unit) {
        socket?.on("receiveMessage") { args ->
            args[0]?.let {
                callback(it as String)
            }
        }
    }
}

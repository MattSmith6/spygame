package com.example.spygame.packet

import com.example.spygame.auth.PlayerEncryptionKey
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class ServerConnectionHandler {

    private val socket: Socket = getServerConnection()
    private val encryptionKey: PlayerEncryptionKey = PlayerEncryptionKey()

    private fun getServerConnection(): Socket {
        // TODO: Establish server connection with server credentials once setup
        return Socket()
    }

    fun isConnectionOpened(): Boolean {
        return !socket.isClosed && encryptionKey.isInitialized()
    }

    fun sendPacket(packet: AbstractPacket): Boolean {
        if (socket.isClosed) {
            return false
        }

        if (!encryptionKey.isInitialized() && packet !is PlayerHandshakePacket) {
            return false
        }

        packet.sendPacket(encryptionKey, getBufferedReader(), getBufferedWriter())
        return true
    }

    fun closeConnection() {
        socket.close()
    }

    private fun getBufferedReader(): BufferedReader {
        return BufferedReader(InputStreamReader(socket.getInputStream()))
    }

    private fun getBufferedWriter(): BufferedWriter {
        return BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
    }

}
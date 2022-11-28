package com.example.spygame.packet

import com.example.spygame.auth.PlayerEncryptionKey
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class ServerConnectionHandler {

    private val hostName: String = "137.184.180.66";
    private val port: Int = 6532;

    private var socket: Socket? = null
    private val encryptionKey: PlayerEncryptionKey = PlayerEncryptionKey()

    fun createServerConnection(): Boolean {
        socket = Socket(hostName, port)

        if (!isConnectionOpened()) {
            socket = null
            return false
        }

        return true
    }

    fun isConnectionOpened(): Boolean {
        return socket?.isClosed == true && encryptionKey.isInitialized()
    }

    fun sendPacket(packet: AbstractPacket): Boolean {
        if (socket?.isClosed == true) {
            return false
        }

        if (!encryptionKey.isInitialized() && packet !is PlayerHandshakePacket) {
            return false
        }

        packet.sendPacket(encryptionKey, getBufferedReader(), getBufferedWriter())
        return true
    }

    fun closeConnection() {
        socket?.close()
    }

    private fun getBufferedReader(): BufferedReader {
        return BufferedReader(InputStreamReader(socket?.getInputStream()))
    }

    private fun getBufferedWriter(): BufferedWriter {
        return BufferedWriter(OutputStreamWriter(socket?.getOutputStream()))
    }

}
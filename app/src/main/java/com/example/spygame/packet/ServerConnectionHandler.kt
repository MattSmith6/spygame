package com.example.spygame.packet

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.spygame.auth.PlayerEncryptionKey
import com.example.spygame.util.ThreadCreator
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket

class ServerConnectionHandler {

    private val LOGGER_NAME: String = "CONNECTION";

    private val hostName: String = "137.184.180.66";
    private val port: Int = 6532;

    private var socket: Socket? = null
    private var bufferedReader: BufferedReader? = null
    private var printWriter: PrintWriter? = null

    private val encryptionKey: PlayerEncryptionKey = PlayerEncryptionKey()

    fun createServerConnection(callback: Runnable) {
        ThreadCreator.createThreadWithCallback( {
            socket = Socket(hostName, port)
            bufferedReader = getBufferedReader()
            printWriter = getPrintWriter()
        }, callback)
    }

    fun isConnectionOpened(): Boolean {
        return socket?.isClosed == false
    }

    fun sendPacket(packet: AbstractPacket, callback: Runnable): Boolean {
        Log.i(LOGGER_NAME, "In send packet method");

        if (!isConnectionOpened()) {
            Log.i(LOGGER_NAME, "Returned early from send packet, connection closed");
            return false
        }

        if (!encryptionKey.isInitialized() && packet !is PlayerHandshakePacket) {
            Log.i(LOGGER_NAME, "Returned early from send packet, not initialized and not handshake packet");
            return false
        }

        Log.i(LOGGER_NAME, "Sending packet with id ${packet.getPacketId()}...");
        ThreadCreator.createThreadWithCallback({
            packet.sendPacket(encryptionKey, getBufferedReader(), getPrintWriter())
        }, callback)

        return true
    }

    fun closeConnection() {
        bufferedReader?.close()
        printWriter?.close()
        socket?.close()
    }

    private fun getBufferedReader(): BufferedReader {
        return BufferedReader(InputStreamReader(socket?.getInputStream()))
    }

    private fun getPrintWriter(): PrintWriter {
        return PrintWriter(OutputStreamWriter(socket?.getOutputStream()), true)
    }

}
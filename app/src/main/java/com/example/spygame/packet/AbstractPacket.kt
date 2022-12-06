package com.example.spygame.packet

import com.example.spygame.auth.PlayerEncryptionKey

import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.PrintWriter

// Use Android studio to convert Java code from server side to Kotlin, remove reference to PacketManager
abstract class AbstractPacket(private val packetId: Int) {

    open fun getPacketId(): Int {
        return packetId
    }

    fun sendPacket(playerEncryptionKey: PlayerEncryptionKey,
                   bufferedReader: BufferedReader, printWriter: PrintWriter): JSONObject? {
        // Send unencrypted packet id for server to read
        printWriter.println(getPacketId())

        // Process the rest of the packet according to protocol
        return process(playerEncryptionKey, bufferedReader, printWriter)
    }

    protected abstract fun process(
        playerEncryptionKey: PlayerEncryptionKey,
        bufferedReader: BufferedReader, printWriter: PrintWriter
    ): JSONObject?

    @Throws(IOException::class)
    protected fun writeJSONObjectToOutput(
        playerEncryptionKey: PlayerEncryptionKey, jsonObject: JSONObject,
        printWriter: PrintWriter
    ) {
        val encryptedObject = playerEncryptionKey.encryptJSONObject(jsonObject)
        printWriter.println(encryptedObject)
    }

    @Throws(IOException::class)
    protected fun readJSONObjectFromInput(
        playerEncryptionKey: PlayerEncryptionKey,
        bufferedReader: BufferedReader
    ): JSONObject? {
        val readObject: String = bufferedReader.readLine()
        return playerEncryptionKey.decryptJSONObject(readObject)
    }

}
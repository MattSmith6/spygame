package com.example.spygame.packet

import com.example.spygame.auth.PlayerEncryptionKey

import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException

// Use Android studio to convert Java code from server side to Kotlin, remove reference to PacketManager
abstract class AbstractPacket(private val packetId: Int) {

    open fun getPacketId(): Int {
        return packetId
    }

    fun sendPacket(playerEncryptionKey: PlayerEncryptionKey,
                   bufferedReader: BufferedReader, bufferedWriter: BufferedWriter) {
        // Send unencrypted packet id for server to read
        bufferedWriter.write(getPacketId())

        // Process the rest of the packet according to protocol
        process(playerEncryptionKey, bufferedReader, bufferedWriter)
    }

    protected abstract fun process(
        playerEncryptionKey: PlayerEncryptionKey,
        bufferedReader: BufferedReader, bufferedWriter: BufferedWriter
    )

    @Throws(IOException::class)
    protected fun writeJSONObjectToOutput(
        playerEncryptionKey: PlayerEncryptionKey, jsonObject: JSONObject,
        bufferedWriter: BufferedWriter
    ) {
        val encryptedObject = playerEncryptionKey.encryptJSONObject(jsonObject)
        bufferedWriter.write(encryptedObject)
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
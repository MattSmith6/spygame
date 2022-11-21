package com.example.spygame.packet

import com.example.spygame.auth.PlayerAuthenticationHandshake
import com.example.spygame.auth.PlayerEncryptionKey
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.BufferedWriter

class PlayerHandshakePacket(private val username: String,
                            private val password: String,
                            private val encryptionKey: PlayerEncryptionKey)
    : AbstractPacket(0) {

    private var errorMessage: String? = null

    fun getErrorMessage(): String? {
        return errorMessage
    }

    override fun process(
        playerEncryptionKey: PlayerEncryptionKey,
        bufferedReader: BufferedReader,
        bufferedWriter: BufferedWriter
    ) {
        val handshake = PlayerAuthenticationHandshake(username, password)

        // Send player hello
        val playerHelloObject = handshake.getPlayerHelloObject()
        writeJSONObjectToOutput(bufferedWriter, playerHelloObject)

        // Receive server hello object
        val serverHelloObject = readJSONObjectFromInput(bufferedReader)

        if (shouldAbortHandshake(serverHelloObject)) {
            return
        }

        // Generate player key exchange object and send to server
        val playerKeyExchangeObject = handshake.getKeyExchangeObject(serverHelloObject)
        writeJSONObjectToOutput(bufferedWriter, playerKeyExchangeObject)

        // Receive server proof
        val serverProof = readJSONObjectFromInput(bufferedReader)

        if (shouldAbortHandshake(serverProof)) {
            return
        }

        // Check validity of server proof
        if (!handshake.isServerProofValid(serverProof)) {
            errorMessage = "Invalid server proof"
            return
        }

        // Our proof was valid and we now have the encryption key
        encryptionKey.initialize(handshake.getSharedSecret())
    }

    private fun shouldAbortHandshake(jsonObject: JSONObject): Boolean {
        if (!jsonObject.has("error")) {
            return false
        }

        errorMessage = jsonObject.getString("error")
        return true
    }

    private fun readJSONObjectFromInput(bufferedReader: BufferedReader): JSONObject {
        val jsonTokener = JSONTokener(bufferedReader.readLine())
        return JSONObject(jsonTokener)
    }

    private fun writeJSONObjectToOutput(bufferedWriter: BufferedWriter, jsonObject: JSONObject) {
        bufferedWriter.write(jsonObject.toString())
    }

}
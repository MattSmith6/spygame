package com.example.spygame.packet

import android.util.Log
import com.example.spygame.auth.PlayerAuthenticationHandshake
import com.example.spygame.auth.PlayerEncryptionKey
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.PrintWriter

class PlayerHandshakePacket(private val username: String,
                            private val password: String,
                            private val encryptionKey: PlayerEncryptionKey)
    : AbstractPacket(0) {

    private val LOGGER_NAME: String = "HANDSHAKE PACKET";

    override fun process(
        playerEncryptionKey: PlayerEncryptionKey,
        bufferedReader: BufferedReader,
        printWriter: PrintWriter
    ): JSONObject? {
        val handshake = PlayerAuthenticationHandshake(username, password)

        Log.i(LOGGER_NAME, "Created PlayerAuthenticationHandshake")

        // Send player hello
        val playerHelloObject = handshake.getPlayerHelloObject()
        writeJSONObjectToOutput(printWriter, playerHelloObject)

        Log.i(LOGGER_NAME, "Wrote player hello to output")

        // Receive server hello object
        val serverHelloObject = readJSONObjectFromInput(bufferedReader)

        Log.i(LOGGER_NAME, "Read player hello from input")

        if (shouldAbortHandshake(serverHelloObject)) {
            Log.i(LOGGER_NAME, "Bad server hello, return early, ${getErrorMessage(serverHelloObject)}");
            return serverHelloObject
        }

        // Generate player key exchange object and send to server
        val playerKeyExchangeObject = handshake.getKeyExchangeObject(serverHelloObject)
        writeJSONObjectToOutput(printWriter, playerKeyExchangeObject)

        Log.i(LOGGER_NAME, "Write player key exchange to output")

        // Receive server proof
        val serverProof = readJSONObjectFromInput(bufferedReader)

        Log.i(LOGGER_NAME, "Read server proof from input")

        if (shouldAbortHandshake(serverProof)) {
            Log.i(LOGGER_NAME, "Bad server proof, return early, ${getErrorMessage(serverProof)}");
            return serverProof
        }

        // Check validity of server proof
        if (!handshake.isServerProofValid(serverProof)) {
            Log.i(LOGGER_NAME, "Invalid server proof")
            return getErrorJSONObject("Invalid server proof")
        }

        // Our proof was valid and we now have the encryption key
        encryptionKey.initialize(handshake.getSharedSecret())
        Log.i(LOGGER_NAME, "Initialized secret successfully")

        return getSuccessJSONObject()
    }

    private fun getErrorJSONObject(error: String): JSONObject {
        val errorJSONObject = getStatusJSONObject("ERROR")
        errorJSONObject.put("error", error)

        return errorJSONObject
    }

    private fun getSuccessJSONObject(): JSONObject {
        return getStatusJSONObject("SUCCESS")
    }

    private fun getStatusJSONObject(status: String): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("status", status)

        return jsonObject
    }

    private fun getErrorMessage(jsonObject: JSONObject): String {
        return jsonObject.getString("error")
    }

    private fun shouldAbortHandshake(jsonObject: JSONObject): Boolean {
        if (!jsonObject.has("error")) {
            return false
        }

        return true
    }

    private fun readJSONObjectFromInput(bufferedReader: BufferedReader): JSONObject {
        val readLine = bufferedReader.readLine()
        Log.i(LOGGER_NAME, readLine)
        val jsonTokener = JSONTokener(readLine)
        return JSONObject(jsonTokener)
    }

    private fun writeJSONObjectToOutput(printWriter: PrintWriter, jsonObject: JSONObject) {
        printWriter.println(jsonObject.toString())
    }

}
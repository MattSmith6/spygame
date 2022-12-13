package com.github.spygameserver.packet

import com.example.spygame.auth.PlayerEncryptionKey
import com.example.spygame.packet.AbstractPacket
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.PrintWriter

class CreateGamePacket(private val isPublic : Int, private val gameType : Int, private val maxPlayers : Int, private val gameName : String) : AbstractPacket(PACKET_ID) {

    override fun process(
        playerEncryptionKey: PlayerEncryptionKey,
        bufferedReader: BufferedReader,
        printWriter: PrintWriter
    ): JSONObject? {

        var firstReadObject: JSONObject? = null

        try {

            val objectToSend = JSONObject()

            objectToSend.put("is_public", isPublic)
            objectToSend.put("game_type", gameType)
            objectToSend.put("max_players", maxPlayers)
            objectToSend.put("game_name", gameName)

            writeJSONObjectToOutput(playerEncryptionKey, objectToSend, printWriter)

            firstReadObject =
                readJSONObjectFromInput(playerEncryptionKey, bufferedReader)

        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        return firstReadObject
    }

    companion object {
        private const val PACKET_ID = 16
    }
}
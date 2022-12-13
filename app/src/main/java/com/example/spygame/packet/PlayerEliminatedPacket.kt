package com.github.spygameserver.packet

import com.example.spygame.auth.PlayerEncryptionKey
import com.example.spygame.packet.AbstractPacket
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.PrintWriter

class PlayerEliminatedPacket(var eliminatorID : Int) : AbstractPacket(PACKET_ID) {

    override fun process(
        playerEncryptionKey: PlayerEncryptionKey,
        bufferedReader: BufferedReader,
        printWriter: PrintWriter
    ): JSONObject? {

        var firstReadObject: JSONObject? = null

        try {

            val objectToSend = JSONObject()

            objectToSend.put("eliminator_id", eliminatorID);

            writeJSONObjectToOutput(playerEncryptionKey, objectToSend, printWriter)

            firstReadObject =
                readJSONObjectFromInput(playerEncryptionKey, bufferedReader)

        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        return firstReadObject
    }

    companion object {
        private const val PACKET_ID = 14
    }
}
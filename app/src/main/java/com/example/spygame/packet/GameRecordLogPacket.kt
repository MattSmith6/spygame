package com.github.spygameserver.packet;

import com.github.spygameserver.auth.PlayerEncryptionKey;
import com.github.spygameserver.packet.AbstractPacket;
import com.github.spygameserver.packet.PacketManager;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

class GameRecordLogPacket(private val username: String,
                          private val password: String,
                          private val encryptionKey: PlayerEncryptionKey)
    : AbstractPacket(0)  {

    private PACKET_ID: int = 31; //pick the number for the package
    //variable info;
    var invitationCode: String;
    var currentPlayer: int;
    var playerID: int;



        
 
    override fun process(
        playerEncryptionKey: PlayerEncryptionKey,
        bufferedReader: BufferedReader,
        printWriter: PrintWriter
    ): JSONObject {
        try {
            // Read object from the reader, can read using #getInt, #getString, etc.
            JSONObject firstReadObject = readJSONObjectFromInput(playerEncryptionKey, bufferedReader);
            //firstReadObject.getInt/String("field name", info);
            this.invitationCode = firstReadObject.getString("invite_code");
            this.currentPlayer = firstReadObject.getInt("current_player");
            this.playerID = playerEncryptionKey.getPlayerId();

            // Set object properties using #put
            JSONObject objectToSend = new JSONObject();
            //firstReadObject.put("field name", info);
            objectToSend.put("invitationCode", this.invitationCode);
            objectToSend.put("current_player", this.currentPlayer);
            objectToSend.put("playerID", this.playerID);

            // Write the JSON object to the player's app
            writeJSONObjectToOutput(playerEncryptionKey, objectToSend, bufferedWriter);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
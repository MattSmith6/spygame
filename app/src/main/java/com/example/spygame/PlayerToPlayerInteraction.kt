package com.example.spygame

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

class PlayerToPlayerInteraction : AppCompatActivity() {
    private var SERVICE_ID: String = "com.example.spygame"
    private var context = this
    private lateinit var toEndpointId: String
    private var isPlayerNearby: Boolean = false

    @Preview
    @Composable
    fun InterfaceScreen() {
        var enabled by remember { mutableStateOf(isPlayerNearby) }
        var playerName: String = "default_name" //The name of the nearest player
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (enabled) {
                Text(
                    text = "Player $playerName is within range!", //This can also just say "A player is within range"
                    fontSize = 20.sp
                )
                Image(
                    painter = painterResource(id = R.drawable.player_near_icon),
                    contentDescription = "Nobody Nearby",
                    modifier = Modifier
                        .size(500.dp)
                )
                //ELIMINATE button appears only if a player is nearby
                Button(
                    onClick = { attack()/*Eliminates Player $playerName from game and rewards a point*/ },
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .padding(bottom = 10.dp, top = 10.dp)
                ) {
                    Text(
                        text = "ELIMINATE",
                        fontSize = 50.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text(text = "No players nearby. Keep searching.", fontSize = 20.sp)
                Image(
                    painter = painterResource(id = R.drawable.nobody_near_icon),
                    contentDescription = "Nobody Nearby",
                    modifier = Modifier
                        .size(500.dp)
                )
            }
        }
    }

    @Composable
    fun InterfaceImage(text: String) {
        
    }
    
    @Composable
    private fun EliminationButton(isEnabled: Boolean) {
        Button(
            onClick = { attack()/*Eliminates Player $playerName from game and rewards a point*/ },
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp),
            enabled = isEnabled
        ) {
            Text(
                text = "ELIMINATE",
                fontSize = 50.sp,
                textAlign = TextAlign.Center
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startAdvertising()
        startDiscovery()
    }

    /////////////////////////////////////////// Advertiser ////////////////////////////////////////////////////////////////

    private fun startAdvertising() {
        val advertisingOptions: AdvertisingOptions =
            AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
        Nearby.getConnectionsClient(this)
            .startAdvertising(
                getLocalUserName(), SERVICE_ID, connectionLifeCycleCallback, advertisingOptions
            )
            .addOnSuccessListener { _ ->
                Toast.makeText(applicationContext, "Advertising", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { _ ->
                Toast.makeText(applicationContext, "Could Not Advertise", Toast.LENGTH_SHORT).show()
            };
    }

    private fun getLocalUserName(): String {
        return "rachel"
    }

    /////////////////////////////////////// Subscriber ///////////////////////////////////////////////////////////////////

    private fun startDiscovery() {
        val discoveryOptions: DiscoveryOptions =
            DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
        Nearby.getConnectionsClient(this)
            .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener { _ -> /*discovering()*/ }
            .addOnFailureListener { _ ->
//                Toast.makeText(applicationContext, "Could Not Discover", Toast.LENGTH_SHORT).show()
            }
    }


    private var endpointDiscoveryCallback: EndpointDiscoveryCallback = object :
        EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            toEndpointId = endpointId
            // An endpoint was found. We request a connection to it.
            Nearby.getConnectionsClient(context)
                .requestConnection(getLocalUserName(), endpointId, connectionLifeCycleCallback)
                .addOnSuccessListener {
                    run {
//                        endpointFound()
                        isPlayerNearby = true
                        Toast.makeText(applicationContext, endpointId, Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { _ ->
//                    Toast.makeText(applicationContext, "Could Not Connect", Toast.LENGTH_SHORT).show()
                }
        }

        override fun onEndpointLost(endpointId: String) {
            Toast.makeText(applicationContext, "Endpoint Lost", Toast.LENGTH_SHORT).show()
        }
    }


    ////////////////////////////////////////// Connection Lifecycle Callback
    private var connectionLifeCycleCallback: ConnectionLifecycleCallback = object :
        ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            // Automatically accept the connection on both sides
            Nearby.getConnectionsClient(context)
                .acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
//                    connectionMade()
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    // Aww
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    // Uh oh
                }
                else -> {
                    // IDK
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            // Show disconnection something or other
//            endpointLost()
        }
    }

    ///////////////////////////////////////// Create/Receive payloads ///////////////////////////////////////////////////

    // Create and send payload
    private fun attack() {
        var bytesPayload: Payload = Payload.fromBytes(byteArrayOf(0x1))
        Nearby.getConnectionsClient(this).sendPayload(toEndpointId, bytesPayload)
    }


    var payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) run {
                var receivedBytes: ByteArray? = payload.asBytes();
                Toast.makeText(applicationContext, "OUCH", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            if (update.status == PayloadTransferUpdate.Status.SUCCESS) {
                // Do something later maybe
            }
        }
    }
    
}
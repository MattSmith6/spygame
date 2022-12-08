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
import androidx.compose.material.*
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

    /////////////////////////////////////////// Game Interface ////////////////////////////////////////////////////////////////

    @Preview
    @Composable
    fun InterfaceScreen() {
        var enabled = remember { mutableStateOf(isPlayerNearby) }
        var playerName: String = "default_name" //The name of the nearest player
//        checkIsPlayerNearby { enabled.value = it } //Needs to be tested, but it should update when an actual player is near
        Scaffold(
            floatingActionButton = {
                SwitchPlayerNearbyBooleanButton(updateBoolean = {
                    enabled.value = it
                })
            },
            floatingActionButtonPosition = FabPosition.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (enabled.value) {
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
                    //ELIMINATE button should be enabled
                    EliminationButton(isEnabled = enabled.value)
                } else {
                    Text(text = "No players nearby. Keep searching.", fontSize = 20.sp)
                    Image(
                        painter = painterResource(id = R.drawable.nobody_near_icon),
                        contentDescription = "Nobody Nearby",
                        modifier = Modifier
                            .size(500.dp)
                    )
                    //ELIMINATE button should be disabled
                    EliminationButton(isEnabled = enabled.value)
                }
            }
        }
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

    /*Once this function is uncommented in InterfaceScreen(), isPlayerNearby will update the screen
    based on whether an actual player is nearby
     */
    private fun checkIsPlayerNearby(isPlayerNearbyValue: (Boolean) -> Unit){
        isPlayerNearbyValue(isPlayerNearby)
    }

//    private fun endpointFound(updateBoolean: (Boolean) -> Unit) {
//        updateBoolean(isPlayerNearby)
//    }
//
//    private fun endpointLost(updateBoolean: (Boolean) -> Unit) {
//        updateBoolean(isPlayerNearby)
//    }


    /*The two functions below are for testing purposes. Screen is now updating, still need to
     test with onEndpointFound() and onDisconnected(), but these should be fine
     */
    //A button to switch the value of isPlayerNearby from true to false and vice-versa
    @Composable
    fun SwitchPlayerNearbyBooleanButton(updateBoolean: (Boolean) -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ExtendedFloatingActionButton(
                text = { Text(text = "TEST BOOLEAN SWITCH") },
                onClick = { updateBoolean(switchPlayerNearby()) }
            )
        }
    }

    //Switches the value of isPlayerNearby from true to false and vice-versa
    private fun switchPlayerNearby() : Boolean {
        isPlayerNearby = !isPlayerNearby
        return isPlayerNearby
    }

///////////////////////////////////////////  ////////////////////////////////////////////////////////////////

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
            isPlayerNearby = false
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
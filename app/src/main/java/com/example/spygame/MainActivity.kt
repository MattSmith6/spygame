package com.example.spygame

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.twotone.List
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spygame.ui.theme.SpyGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpyGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    //LoginScreen(applicationContext)
                }
            }
        }
    }
}

//@Composable
//fun LoginScreen(context: Context) {
//    var username by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(20.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.sgtemplogo),
//            contentDescription = "Spy Game Logo",
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 10.dp)
//        )
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        Text(
//            text = "LOGIN",
//            fontFamily = FontFamily.Monospace,
//            textAlign = TextAlign.Center,
//            fontSize = 30.sp,
//            fontWeight = FontWeight.Bold,
//            color = Color.LightGray,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 20.dp)
//        )
//
//        OutlinedTextField(
//            value = username,
//            onValueChange = { username = it },
//            label = { Text(text = "Enter Username") },
//            leadingIcon = {
//                Icon(Icons.Default.Person, contentDescription = "username")
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 10.dp, top = 10.dp)
//        )
//
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text(text = "Enter Password") },
//            leadingIcon = {
//                Icon(Icons.Default.Info, contentDescription = "password")
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 10.dp, top = 10.dp),
//            visualTransformation = PasswordVisualTransformation(),
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
//        )
//
//        OutlinedButton(
//            onClick = { logged(username, password, context) },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 10.dp, top = 10.dp)
//        ) {
//            Text(
//                text = "Login",
//                textAlign = TextAlign.Center
//            )
//        }
//
//        Row(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(20.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = "Don't have an account?",
//                textAlign = TextAlign.Center
//            )
//
//            TextButton(onClick = { /*TODO*/ }) {
//                Text(
//                    text = "REGISTER"
//                )
//            }
//
//        }
//    }
//}
//
//fun logged(username: String, password: String, context: Context) {
//    //Test data for now
//    if (username == "TroyMalaki" && password == "1234") {
//        Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show()
//    } else {
//        Toast.makeText(context, "Username or Password is Invalid!", Toast.LENGTH_SHORT).show()
//    }
//
//}

//@Composable
//fun RegisterScreen() {
//    var email by remember { mutableStateOf("") }
//    var username by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(20.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.sgtemplogo),
//            contentDescription = "Spy Game Logo",
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 10.dp)
//        )
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        Text(
//            text = "SIGN UP",
//            fontFamily = FontFamily.Monospace,
//            textAlign = TextAlign.Center,
//            fontSize = 30.sp,
//            fontWeight = FontWeight.Bold,
//            color = Color.LightGray,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 20.dp)
//        )
//
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text(text = "Enter CSUN E-Mail") },
//            leadingIcon = {
//                Icon(Icons.Default.MailOutline, contentDescription = "email")
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 10.dp, top = 10.dp)
//        )
//
//        OutlinedTextField(
//            value = username,
//            onValueChange = { username = it },
//            label = { Text(text = "Enter Username") },
//            leadingIcon = {
//                Icon(Icons.Default.Person, contentDescription = "username")
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 10.dp, top = 10.dp)
//        )
//
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text(text = "Enter Password") },
//            leadingIcon = {
//                Icon(Icons.Default.Info, contentDescription = "password")
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 10.dp, top = 10.dp),
//            visualTransformation = PasswordVisualTransformation(),
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
//        )
//
//        OutlinedButton(
//            onClick = { /*TO DO */ },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 10.dp, top = 10.dp)
//        ) {
//            Text(
//                text = "Create Account",
//                textAlign = TextAlign.Center
//            )
//        }
//
//        Row(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(20.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = "Already have an account?",
//                textAlign = TextAlign.Center
//            )
//
//            TextButton(onClick = { /*TODO*/ }) {
//                Text(
//                    text = "LOG IN"
//                )
//            }
//
//        }
//    }
//}

//@Preview
//@Composable
//fun MainScreen() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(20.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        Spacer(modifier = Modifier.height(100.dp))
//
//        Button(
//            onClick = { /*TODO*/ },
//            modifier = Modifier
//                .height(125.dp)
//                .fillMaxWidth()
//                .padding(bottom = 10.dp, top = 10.dp)
//        ) {
//            Text(
//                text = "JOIN",
//                fontSize = 70.sp,
//                textAlign = TextAlign.Center
//            )
//        }
//
//        Button(
//            onClick = { /*TODO*/ },
//            modifier = Modifier
//                .height(125.dp)
//                .fillMaxWidth()
//                .padding(bottom = 10.dp, top = 10.dp)
//        ) {
//            Text(
//                text = "HOST",
//                fontSize = 70.sp,
//                textAlign = TextAlign.Center
//            )
//        }
//        Row(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(20.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center
//        ) {
//            IconButton(
//                onClick = { /*TODO*/ }
//            ) {
//                Icon(Icons.Outlined.List, contentDescription = "leaderboard", modifier = Modifier.size(50.dp))
//            }
//
//            Spacer(modifier = Modifier.width(50.dp))
//
//            IconButton(
//                onClick = { /*TODO*/ }
//            ) {
//                Icon(Icons.Outlined.Settings, contentDescription = "settings", modifier = Modifier.size(50.dp))
//            }
//        }
//    }
//}
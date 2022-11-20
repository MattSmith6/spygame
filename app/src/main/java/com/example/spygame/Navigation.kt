package com.example.spygame

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
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
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.LoginScreen.route) {
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController)
        }
        composable(
            route = Screen.MenuScreen.route,
            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                    defaultValue = "default_user"
                }
            )
        ) {

        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.sg_logo),
            contentDescription = "Spy Game Logo",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "LOGIN",
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = "Enter Username") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "username")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Enter Password") },
            leadingIcon = {
                Icon(Icons.Default.Info, contentDescription = "password")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        OutlinedButton(
            onClick = { /*TO DO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp)
        ) {
            Text(
                text = "Login",
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Don't have an account?",
                textAlign = TextAlign.Center
            )

            TextButton(onClick = { navController.navigate(Screen.RegistrationScreen.route) }) {
                Text(
                    text = "REGISTER"
                )
            }

        }
    }
}

@Composable
fun RegisterScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.sg_logo),
            contentDescription = "Spy Game Logo",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "SIGN UP",
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Enter CSUN E-Mail") },
            leadingIcon = {
                Icon(Icons.Default.MailOutline, contentDescription = "email")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = "Enter Username") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "username")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Enter Password") },
            leadingIcon = {
                Icon(Icons.Default.Info, contentDescription = "password")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        OutlinedButton(
            onClick = { /*TO DO */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp)
        ) {
            Text(
                text = "Create Account",
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Already have an account?",
                textAlign = TextAlign.Center
            )

            TextButton(onClick = { navController.navigate(Screen.LoginScreen.route) }) {
                Text(
                    text = "LOG IN"
                )
            }
        }
    }
}

@Preview
@Composable
fun MenuScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(100.dp))

        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .height(125.dp)
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp)
        ) {
            Text(
                text = "JOIN",
                fontSize = 70.sp,
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .height(125.dp)
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp)
        ) {
            Text(
                text = "HOST",
                fontSize = 70.sp,
                textAlign = TextAlign.Center
            )
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { /*TODO*/ }
            ) {
                Icon(Icons.Outlined.List, contentDescription = "leaderboard", modifier = Modifier.size(50.dp))
            }

            Spacer(modifier = Modifier.width(50.dp))

            IconButton(
                onClick = { /*TODO*/ }
            ) {
                Icon(Icons.Outlined.Settings, contentDescription = "settings", modifier = Modifier.size(50.dp))
            }
        }
    }
}
@Preview
@Composable
fun InterfaceScreen() {
    var isPlayerNearby: Boolean = true //Returns true if a player is within the range of 15 feet
    var playerName: String = "default_name" //The name of the nearest player
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isPlayerNearby) {
            Text(text = "Player $playerName is within range!", fontSize = 20.sp) //This can also just say "A player is within range"
            Image(
                painter = painterResource(id = R.drawable.player_near_icon),
                contentDescription = "Nobody Nearby",
                modifier = Modifier
                    .size(500.dp)
            )
            //ELIMINATE button appears only if a player is nearby
            Button(
                onClick = { /*TO DO */ }, //Eliminates Player $playerName from game and rewards a point/s
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
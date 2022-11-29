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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
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
import com.example.spygame.auth.PlayerEncryptionKey
import com.example.spygame.auth.website.CheckUsernameExistsRequest
import com.example.spygame.auth.website.RegisterAccountRequest
import com.example.spygame.packet.PlayerHandshakePacket
import com.example.spygame.packet.ServerConnectionHandler
import java.util.regex.Pattern

@Preview
@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.LoginScreen.route) {
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Screen.RegistrationScreen.route) {
            RegisterScreen(navController = navController)
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
            MenuScreen()
        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val serverConnectionHandler by remember { mutableStateOf(ServerConnectionHandler()) }
    val playerEncryptionKey by remember { mutableStateOf(PlayerEncryptionKey()) }

    var badLoginCredentialsOpacity by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.sgtemplogo),
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
            onValueChange = {
                username = it
                badLoginCredentialsOpacity = 0f
                            },
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
            onValueChange = {
                password = it
                badLoginCredentialsOpacity = 0f
                            },
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

        Text(
            text = "Incorrect login credentials.",
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Left,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            color = Color.Red,
            fontStyle = FontStyle.Italic,
            modifier = Modifier
                .alpha(badLoginCredentialsOpacity)
                .padding(top = 3.dp, bottom = 3.dp)
        )

        OutlinedButton(
            onClick = {
                serverConnectionHandler.createServerConnection()

                if (serverConnectionHandler.isConnectionOpened()) {
                    val handshakePacket = PlayerHandshakePacket(username, password, playerEncryptionKey)
                    serverConnectionHandler.sendPacket(handshakePacket)

                    if (playerEncryptionKey.isInitialized()) {
                        navController.navigate(Screen.MenuScreen.route)
                    } else {
                        serverConnectionHandler.closeConnection()
                        badLoginCredentialsOpacity = 1f
                    }
                }
                      },
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

    var emailErrorOpacity by remember { mutableStateOf(0f) }
    var emailErrorMessage by remember { mutableStateOf("") }

    var usernameErrorOpacity by remember { mutableStateOf(0f) }
    var usernameErrorMessage by remember { mutableStateOf("") }

    var passwordErrorOpacity by remember { mutableStateOf(0f) }
    var passwordErrorMessage by remember { mutableStateOf("") }

    val pattern: Pattern = Pattern.compile("^[a-zA-Z0-9]+\$")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.sgtemplogo),
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
            onValueChange = {
                email = it
                emailErrorOpacity = 0f
                            },
            label = { Text(text = "Enter CSUN E-Mail") },
            leadingIcon = {
                Icon(Icons.Default.MailOutline, contentDescription = "email")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )

        Text(
            text = emailErrorMessage,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Left,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            color = Color.Red,
            fontStyle = FontStyle.Italic,
            modifier = Modifier
                .alpha(emailErrorOpacity)
                .padding(top = 1.dp, bottom = 10.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                usernameErrorOpacity = 0f
                            },
            label = { Text(text = "Enter Username") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "username")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )

        Text(
            text = usernameErrorMessage,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Left,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            color = Color.Red,
            fontStyle = FontStyle.Italic,
            modifier = Modifier
                .alpha(usernameErrorOpacity)
                .padding(top = 1.dp, bottom = 10.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordErrorOpacity = 0f
                            },
            label = { Text(text = "Enter Password") },
            leadingIcon = {
                Icon(Icons.Default.Info, contentDescription = "password")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Text(
            text = passwordErrorMessage,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Left,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            color = Color.Red,
            fontStyle = FontStyle.Italic,
            modifier = Modifier
                .alpha(passwordErrorOpacity)
                .padding(top = 1.dp, bottom = 10.dp)
        )

        OutlinedButton(
            onClick = {
                var validEmail = false
                if (email.contains("@")) {
                    validEmail = email.split("@")[1].endsWith("csun.edu")
                }

                if (!validEmail) {
                    emailErrorMessage = "This email is not a valid CSUN email."
                    emailErrorOpacity = 1f
                }

                var validUsername = true
                if (username.length < 5 || username.length > 16) {
                    usernameErrorMessage = "Usernames must be between 5 and 16 characters."
                    usernameErrorOpacity = 1f
                    validUsername = false
                } else if (!pattern.matcher(username).matches()) {
                    usernameErrorMessage = "Usernames cannot contain any special characters."
                    usernameErrorOpacity = 1f
                    validUsername = false
                } else {
                    val checkUsernameExistsRequest = CheckUsernameExistsRequest(username)
                    checkUsernameExistsRequest.createHttpRequest(checkUsernameExistsRequest.getJSONObjectConsumer {
                            jsonObject ->
                        // If the object has the exists property (it should) and the username does not exist,
                        // we should set the error message when signing up
                        if (jsonObject?.has("exists") == true && !jsonObject.getBoolean("exists")) {
                            usernameErrorMessage = "This username already exists, pick another."
                            usernameErrorOpacity = 1f
                            validUsername = false
                        }
                    })
                }

                var validPassword = true
                if (password.length < 8) {
                    passwordErrorMessage = "Passwords must be at least 8 characters long."
                    passwordErrorOpacity = 1f
                    validPassword = false
                }

                if (validEmail && validUsername && validPassword) {
                    val registerRequest = RegisterAccountRequest(email, username, password)
                    registerRequest.createHttpRequest(registerRequest.getJSONObjectConsumer {
                        jsonObject ->
                        if (jsonObject == null || jsonObject.has("error")) {
                            passwordErrorMessage = jsonObject?.getString("error").orEmpty()
                            passwordErrorOpacity = 1f
                        } else {
                            // TODO: Alert about email verification first
                            navController.navigate(Screen.LoginScreen.route)
                        }
                    })
                }
            },
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
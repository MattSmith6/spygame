package com.example.spygame

import android.os.StrictMode
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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

var isHost: Boolean = false

@Preview
@Composable
fun Navigation() {
    StrictMode.enableDefaults();
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.LoginScreen.route) {
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Screen.RegisterScreen.route) {
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
        //Here I attempted the implementation of passing the username,
        //but I'm still learning more on how to do it, so its commented out right now
        /*********************************
        composable(
        route = Screen.MenuScreen.route,
        arguments = listOf(
        navArgument("username") {
        type = NavType.StringType
        defaultValue = "default_user"
        }
        )
        ) { entry ->
        MenuScreen(username = entry.arguments?.getString("username"))
        }
         *********************************/
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

        LogoHeader(pageName = "LOG IN")
        LoginFields(
            username = username,
            password = password,
            onUsernameChange = { username = it },
            onPasswordChange = { password = it },
            onForgotPassClick = { /*Should open forgot password prompt*/ }
        )
        LoginRegisterFooter(
            mainButtonTxt = "LOG IN",
            onMainButtonClick = { /*Checks account details, if wrong then prompts to try again, if not then goes to MenuScreen()*/ },
            switchScreenTxt = "Don't have an account?",
            onSwitchScreenClick = { navController.navigate(Screen.RegisterScreen.route) },
            switchScreenTxtButton = "REGISTER"
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
                badLoginCredentialsOpacity = 1f

                // Create the server connection with callback to check connection and initialization
                serverConnectionHandler.createServerConnection {

                    Log.i("NAVIGATION", "Created server connection, in callback")

                    if (serverConnectionHandler.isConnectionOpened()) {

                        Log.i("NAVIGATION", "Connection opened successfully")

                        val handshakePacket = PlayerHandshakePacket(username, password, playerEncryptionKey)

                        // Send packet with callback with check on initialization
                        serverConnectionHandler.sendPacket(handshakePacket) {

                            Log.i("NAVIGATION", "Sent packet, in callback")

                            if (playerEncryptionKey.isInitialized()) {
                                navController.navigate(Screen.MenuScreen.route)
                            } else {
                                serverConnectionHandler.closeConnection()
                                badLoginCredentialsOpacity = 1f
                            }

                        }
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

            TextButton(onClick = { navController.navigate(Screen.RegisterScreen.route) }) {
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
        LogoHeader(pageName = "SIGN UP")
        RegisterFields(
            email = email,
            username = username,
            password = password,
            onEmailChange = { email = it },
            onUsernameChange = { username = it },
            onPasswordChange = { password = it }
        )
        LoginRegisterFooter(
            mainButtonTxt = "CREATE ACCOUNT",
            onMainButtonClick = { /*Checks if csun.edu and checks if account details don't exist then creates account*/ },
            switchScreenTxt = "Already have an account?",
            onSwitchScreenClick = { navController.navigate(Screen.LoginScreen.route) },
            switchScreenTxtButton = "LOG IN"
        )
    }
}

//        OutlinedTextField(
//            value = email,
//            onValueChange = {
//                email = it
//                emailErrorOpacity = 0f
//                            },
//            label = { Text(text = "Enter CSUN E-Mail") },
//            leadingIcon = {
//                Icon(Icons.Default.MailOutline, contentDescription = "email")
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 10.dp)
//        )
//
//        Text(
//            text = emailErrorMessage,
//            fontFamily = FontFamily.Monospace,
//            textAlign = TextAlign.Left,
//            fontSize = 12.sp,
//            fontWeight = FontWeight.Light,
//            color = Color.Red,
//            fontStyle = FontStyle.Italic,
//            modifier = Modifier
//                .alpha(emailErrorOpacity)
//                .padding(top = 1.dp, bottom = 10.dp)
//        )
//
//        OutlinedTextField(
//            value = username,
//            onValueChange = {
//                username = it
//                usernameErrorOpacity = 0f
//                            },
//            label = { Text(text = "Enter Username") },
//            leadingIcon = {
//                Icon(Icons.Default.Person, contentDescription = "username")
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 10.dp)
//        )
//
//        Text(
//            text = usernameErrorMessage,
//            fontFamily = FontFamily.Monospace,
//            textAlign = TextAlign.Left,
//            fontSize = 12.sp,
//            fontWeight = FontWeight.Light,
//            color = Color.Red,
//            fontStyle = FontStyle.Italic,
//            modifier = Modifier
//                .alpha(usernameErrorOpacity)
//                .padding(top = 1.dp, bottom = 10.dp)
//        )
//
//        OutlinedTextField(
//            value = password,
//            onValueChange = {
//                password = it
//                passwordErrorOpacity = 0f
//                            },
//            label = { Text(text = "Enter Password") },
//            leadingIcon = {
//                Icon(Icons.Default.Info, contentDescription = "password")
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 10.dp),
//            visualTransformation = PasswordVisualTransformation(),
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
//        )
//
//        Text(
//            text = passwordErrorMessage,
//            fontFamily = FontFamily.Monospace,
//            textAlign = TextAlign.Left,
//            fontSize = 12.sp,
//            fontWeight = FontWeight.Light,
//            color = Color.Red,
//            fontStyle = FontStyle.Italic,
//            modifier = Modifier
//                .alpha(passwordErrorOpacity)
//                .padding(top = 1.dp, bottom = 10.dp)
//        )
//
//        OutlinedButton(
//            onClick = {
//                var validEmail = false
//                if (email.contains("@")) {
//                    validEmail = email.split("@")[1].endsWith("csun.edu")
//                }
//
//                if (!validEmail) {
//                    emailErrorMessage = "This email is not a valid CSUN email."
//                    emailErrorOpacity = 1f
//                }
//
//                var validUsername = true
//                if (username.length < 5 || username.length > 16) {
//                    usernameErrorMessage = "Usernames must be between 5 and 16 characters."
//                    usernameErrorOpacity = 1f
//                    validUsername = false
//                } else if (!pattern.matcher(username).matches()) {
//                    usernameErrorMessage = "Usernames cannot contain any special characters."
//                    usernameErrorOpacity = 1f
//                    validUsername = false
//                } else {
//                    CheckUsernameExistsRequest(username).createHttpRequest {
//                            jsonObject ->
//                        // If the object has the exists property (it should) and the username does not exist,
//                        // we should set the error message when signing up
//                        if (jsonObject?.has("exists") == true && !jsonObject.getBoolean("exists")) {
//                            usernameErrorMessage = "This username already exists, pick another."
//                            usernameErrorOpacity = 1f
//                            validUsername = false
//                        }
//                    }
//                }
//
//                var validPassword = true
//                if (password.length < 8) {
//                    passwordErrorMessage = "Passwords must be at least 8 characters long."
//                    passwordErrorOpacity = 1f
//                    validPassword = false
//                }
//
//                if (validEmail && validUsername && validPassword) {
//                    RegisterAccountRequest(email, username, password).createHttpRequest {
//                        jsonObject ->
//                        if (jsonObject == null || jsonObject.has("error")) {
//                            passwordErrorMessage = jsonObject?.getString("error").orEmpty()
//                            passwordErrorOpacity = 1f
//                        } else {
//                            // TODO: Alert about email verification first
//                            Log.i("REGISTER", jsonObject.toString())
//                            navController.navigate(Screen.LoginScreen.route)
//                        }
//                    }
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 10.dp, top = 10.dp)
//        ) {
//            Text(
//                text = "Create Account",
//                textAlign = TextAlign.Center
//            )
//        }


@Composable
fun ForgotPasswordPrompt() {
    var email by remember { mutableStateOf("") }
    Card(elevation = 10.dp, modifier = Modifier.padding(20.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Enter your account email. Link will be sent to reset password.")
            PasswordPromptField(
                email = email,
                onEmailChange = { email = it },
                onSubmitClick = {/*Checks email, if it exists then sends a reset pw link*/ })
        }
    }
}

@Composable
fun ForgotPasswordScreen() {
    var newPassword1 by remember { mutableStateOf("") }
    var newPassword2 by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LogoHeader(pageName = "PASSWORD RESET")

        ForgotPasswordField(
            firstPassword = newPassword1,
            secondPassword = newPassword2,
            onFirstPasswordChange = {newPassword1 = it},
            onSecondPasswordChange = {newPassword2 = it},
            onResetClick = {/*both passwords should be the same then set password to newPassword1*/}
        )
    }

}

//@Preview
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

        MenuButton( //Lets the player join an existing game
            text = "JOIN",
            onMenuButtonClick = {/*Prompts for join code*/ }
        )

        MenuButton( //Lets the player create their own game and invite
            text = "HOST",
            onMenuButtonClick = {/*Switches to CreateGameScreen*/ }
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { /*Switches to LeaderboardScreen*/ }
            ) {
                Icon(
                    Icons.Outlined.List,
                    contentDescription = "leaderboard",
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.width(50.dp))

            IconButton(
                onClick = { /*Switches to SettingsScreen*/ }
            ) {
                Icon(
                    Icons.Outlined.Settings,
                    contentDescription = "settings",
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}

//@Preview
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
                onClick = { /*Eliminates Player $playerName from game and rewards a point*/ },
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
fun LobbyScreen(joinCode: String) {
    isHost = true
    /*These will need to be changed. I just placed this data for*/
    /*testing purposes. The goal of these variables is to display*/
    /*the joinCode on the top of the page so it can be shared and*/
    /*it checks if the player isHost so it can display a button to*/
    /*start the game*/
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Game Lobby",
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            //color = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp)
        )
        Text(
            text = "Join Code: $joinCode", fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center, modifier = Modifier.padding(top = 10.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp)
                .background(color = MaterialTheme.colors.onBackground)
        ) {
            StartGameButton(onStartButtonClick = { /*Checks to see if the minimum amount of players
            are in the lobby then starts game and switches to InterfaceScreen for all players*/
            }
            )
        }
    }
}

@Preview
@Composable
fun PreviewLobbyScreen() {
    LobbyScreen(joinCode = "ABCD")

}

@Composable
fun LogoHeader(pageName: String) {
    Image(
        painter = painterResource(id = R.drawable.sg_logo),
        contentDescription = "Spy Game Logo",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = pageName,
        fontFamily = FontFamily.Monospace,
        textAlign = TextAlign.Center,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        color = Color.LightGray,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    )
}

@Composable
fun LoginRegisterFooter(
    mainButtonTxt: String,
    onMainButtonClick: () -> Unit,
    switchScreenTxt: String,
    onSwitchScreenClick: () -> Unit,
    switchScreenTxtButton: String
) {
    Column {
        OutlinedButton(
            onClick = onMainButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp)
        ) {
            Text(
                text = mainButtonTxt,
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
                text = switchScreenTxt,
                textAlign = TextAlign.Center
            )

            TextButton(onClick = onSwitchScreenClick) { //Switches to LoginScreen
                Text(
                    text = switchScreenTxtButton
                )
            }
        }
    }
}

@Composable
fun ColumnScope.LoginFields(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onForgotPassClick: () -> Unit
) {
    TxtField(
        value = username,
        label = "Username",
        placeholder = "Enter Username",
        onValueChange = onUsernameChange,
        leadingIcon = {
            Icon(Icons.Default.Person, contentDescription = "username")
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        )
    )
    TxtField(
        value = password,
        label = "Password",
        placeholder = "Enter Password",
        onValueChange = onPasswordChange,
        visualTransformation = PasswordVisualTransformation(),
        leadingIcon = {
            Icon(Icons.Default.Info, contentDescription = "password")
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Go
        )
    )

    TextButton(
        onClick = onForgotPassClick,
        modifier = Modifier.align(Alignment.End)
    ) {
        Text(text = "Forgot Password?", fontSize = 12.sp)
    }
}

@Composable
fun ColumnScope.PasswordPromptField(
    email: String,
    onEmailChange: (String) -> Unit,
    onSubmitClick: () -> Unit
) {
    TxtField(
        value = email,
        label = "E-Mail",
        placeholder = "Enter E-Mail",
        onValueChange = onEmailChange,
        leadingIcon = {
            Icon(Icons.Default.Email, contentDescription = "email")
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        )
    )

    TextButton(
        onClick = onSubmitClick,
        modifier = Modifier.align(Alignment.End)
    ) {
        Text(text = "Forgot Password?", fontSize = 12.sp)
    }

}

@Composable
fun RegisterFields(
    email: String,
    username: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    TxtField(
        value = email,
        label = "E-Mail",
        placeholder = "Enter E-Mail",
        onValueChange = onEmailChange,
        leadingIcon = {
            Icon(Icons.Default.Email, contentDescription = "email")
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        )
    )
    TxtField(
        value = username,
        label = "Username",
        placeholder = "Enter Username",
        onValueChange = onUsernameChange,
        leadingIcon = {
            Icon(Icons.Default.Person, contentDescription = "username")
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        )
    )
    TxtField(
        value = password,
        label = "Password",
        placeholder = "Enter Password",
        onValueChange = onPasswordChange,
        visualTransformation = PasswordVisualTransformation(),
        leadingIcon = {
            Icon(Icons.Default.Info, contentDescription = "password")
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Go
        )
    )

}

@Composable
fun ForgotPasswordField(
    firstPassword: String,
    secondPassword: String,
    onFirstPasswordChange: (String) -> Unit,
    onSecondPasswordChange: (String) -> Unit,
    onResetClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) { TxtField(
        value = firstPassword,
        label = "New Password",
        placeholder = "Enter New Password",
        onValueChange = onFirstPasswordChange,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        )
    )
        TxtField(
            value = secondPassword,
            label = "Re-enter Password",
            placeholder = "Re-enter New Password",
            onValueChange = onSecondPasswordChange,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Go
            )
        )

        OutlinedButton(
            onClick = { /*TODO*/ }, modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp)
        ) {
            Text(
                text = "Reset Password",
                textAlign = TextAlign.Center
            )
        }

    }
}

@Composable
fun TxtField(
    value: String,
    label: String,
    placeholder: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        leadingIcon = leadingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp, top = 10.dp)
    )
}

@Composable
fun MenuButton(text: String, onMenuButtonClick: () -> Unit) {
    Button(
        onClick = onMenuButtonClick,
        modifier = Modifier
            .height(125.dp)
            .fillMaxWidth()
            .padding(bottom = 10.dp, top = 10.dp)
    ) {
        Text(
            text = text,
            fontSize = 70.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StartGameButton(onStartButtonClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExtendedFloatingActionButton(
            text = { Text(text = "START GAME") },
            onClick = onStartButtonClick
        )
    }
}
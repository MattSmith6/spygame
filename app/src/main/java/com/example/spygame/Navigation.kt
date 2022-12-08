package com.example.spygame

import android.os.StrictMode
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.colorResource
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
import com.example.spygame.auth.website.ResetPasswordRequest
import com.example.spygame.packet.PlayerHandshakePacket
import com.example.spygame.packet.ServerConnectionHandler
import java.util.regex.Pattern

private var isHost: Boolean = false /*Purpose of this is so only a player who is host can see and
                                    use the StartGameButton() in the LobbyScreen(), might need to
                                    change implementation of it for correct functionality.*/

@Preview
@Composable
fun Navigation() {
    StrictMode.enableDefaults();
    val navController = rememberNavController()
    val serverConnectionHandler by remember { mutableStateOf(ServerConnectionHandler()) }
    val playerEncryptionKey by remember { mutableStateOf(PlayerEncryptionKey()) }
    NavHost(navController = navController, startDestination = Screen.LoginScreen.route) {
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController,
                serverConnectionHandler = serverConnectionHandler,
                playerEncryptionKey = playerEncryptionKey)
        }
        composable(route = Screen.RegisterScreen.route) {
            RegisterScreen(navController = navController)
        }
        composable(route = Screen.LobbyScreen.route) {
            /*Join code and max players don't NEED to be used here but are here just to test if the
            check if the screen behaves properly*/
            LobbyScreen(joinCode = "ABCD", navController = navController,
                serverConnectionHandler = serverConnectionHandler,
                playerEncryptionKey = playerEncryptionKey)
        }
        composable(route = Screen.MenuScreen.route) {
            MenuScreen(navController = navController,
                serverConnectionHandler = serverConnectionHandler,
                playerEncryptionKey = playerEncryptionKey)
        }
        composable(route = Screen.InterfaceScreen.route) {
            PlayerToPlayerInteraction().InterfaceScreen()
        }
    }
}

@Composable
fun LoginScreen(navController: NavController,
                serverConnectionHandler: ServerConnectionHandler,
                playerEncryptionKey: PlayerEncryptionKey
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }

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
            errorMessage = errorMessage,
            onUsernameChange = {
                username = it
                errorMessage = ""
            },
            onPasswordChange = {
                password = it
                errorMessage = ""
            },
            onForgotPassClick = { navController.navigate(Screen.ForgotPasswordScreen.route) }
        )

        LoginRegisterFooter(
            mainButtonTxt = "LOG IN",
            onMainButtonClick = {
                // Create the server connection with callback to check connection and initialization
                serverConnectionHandler.createServerConnection {

                    Log.i("NAVIGATION", "Created server connection, in callback")

                    if (serverConnectionHandler.isConnectionOpened()) {

                        Log.i("NAVIGATION", "Connection opened successfully")

                        val handshakePacket =
                            PlayerHandshakePacket(username, password, playerEncryptionKey)

                        // Send packet with callback with check on initialization
                        serverConnectionHandler.sendPacket(handshakePacket) { jsonObject ->

                            Log.i("NAVIGATION", "Sent packet, in callback")

                            if (jsonObject.has("error")) {
                                val trueErrorMessage = jsonObject.getString("error")
                                serverConnectionHandler.closeConnection()

                                if (trueErrorMessage.equals("bad_record_mac")) {
                                    errorMessage = "Invalid login credentials"
                                } else {
                                    errorMessage = trueErrorMessage
                                }
                            } else {
                                navController.navigate(Screen.MenuScreen.route)
                            }
                        }
                    }

                }
            },
            switchScreenTxt = "Don't have an account?",
            onSwitchScreenClick = { navController.navigate(Screen.RegisterScreen.route) },
            switchScreenTxtButton = "REGISTER"
        )
    }
}

@Composable
fun RegisterScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailErrorMessage by remember { mutableStateOf("") }
    var usernameErrorMessage by remember { mutableStateOf("") }
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
            emailErrorMessage = emailErrorMessage,
            username = username,
            usernameErrorMessage = usernameErrorMessage,
            password = password,
            passwordErrorMessage = passwordErrorMessage,
            onEmailChange = {
                email = it
                emailErrorMessage = ""
            },
            onUsernameChange = {
                username = it
                usernameErrorMessage = ""
            },
            onPasswordChange = {
                password = it
                passwordErrorMessage = ""
            }
        )
        LoginRegisterFooter(
            mainButtonTxt = "CREATE ACCOUNT",
            onMainButtonClick = {
                var validEmail = false
                if (email.contains("@")) {
                    validEmail = email.split("@")[1].endsWith("csun.edu")
                }

                if (!validEmail) {
                    emailErrorMessage = "This email is not a valid CSUN email."
                }

                var validUsername = true
                if (username.length < 5 || username.length > 16) {
                    usernameErrorMessage = "Usernames must be between 5 and 16 characters."
                    validUsername = false
                } else if (!pattern.matcher(username).matches()) {
                    usernameErrorMessage = "Usernames cannot contain any special characters."
                    validUsername = false
                } else {
                    CheckUsernameExistsRequest(username).createHttpRequest { jsonObject ->
                        // If the object has the exists property (it should) and the username does not exist,
                        // we should set the error message when signing up
                        if (jsonObject?.has("exists") == true && !jsonObject.getBoolean("exists")) {
                            usernameErrorMessage = "This username already exists, pick another."
                            validUsername = false
                        }
                    }
                }

                var validPassword = true
                if (password.length < 8) {
                    passwordErrorMessage = "Passwords must be at least 8 characters long."
                    validPassword = false
                }

                if (validEmail && validUsername && validPassword) {
                    RegisterAccountRequest(
                        email,
                        username,
                        password
                    ).createHttpRequest { jsonObject ->
                        if (jsonObject == null || jsonObject.has("error")) {
                            passwordErrorMessage = jsonObject?.getString("error").orEmpty()
                        } else {
                            // TODO: Alert about email verification first
                            Log.i("REGISTER", jsonObject.toString())
                            navController.navigate(Screen.LoginScreen.route)
                        }
                    }
                }
            },
            switchScreenTxt = "Already have an account?",
            onSwitchScreenClick = { navController.navigate(Screen.LoginScreen.route) },
            switchScreenTxtButton = "LOG IN"
        )
    }
}

@Composable
fun ForgotPasswordPrompt(navController: NavController) {
    var email by remember { mutableStateOf("") }
    val errorMessage by remember { mutableStateOf("") }

    Card(elevation = 10.dp, modifier = Modifier.padding(20.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Enter your account email. Link will be sent to reset password.")
            Text(
                text = errorMessage,

                )

            PasswordPromptField(
                email = email,
                onEmailChange = { email = it },
                onSubmitClick = {
                    ResetPasswordRequest(email).createHttpRequest { jsonObject ->
                        if (jsonObject?.has("error") == true) {

                        } else {
                            // TODO: ALERT USER TO CHECK EMAIL
                            navController.navigate(Screen.LoginScreen.route);
                        }
                    }
                }
            )
        }
    }
}


@Composable
fun MenuScreen(navController: NavController,
               serverConnectionHandler: ServerConnectionHandler,
               playerEncryptionKey: PlayerEncryptionKey
) {
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
            onMenuButtonClick = { navController.navigate(Screen.LobbyScreen.route)/*Prompts for join code*/ }
        )

        MenuButton( //Lets the player create their own game and invite
            text = "HOST",
            onMenuButtonClick = { navController.navigate(Screen.LobbyScreen.route)/*Switches to LobbyScreen*/ }
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

/*Similar problem to InterfaceScreen(), the LobbyScreen() should update the PlayerList() with every
player that joins.
 */
@Composable
fun LobbyScreen(joinCode: String, navController: NavController,
                serverConnectionHandler: ServerConnectionHandler,
                playerEncryptionKey: PlayerEncryptionKey
) {
    isHost = true
    /*These will need to be changed. I just placed this data for*/
    /*testing purposes. The goal of these variables is to display*/
    /*the joinCode on the top of the page so it can be shared and*/
    /*it checks if the player isHost so it can display a button to*/
    /*start the game*/
    Scaffold(
        floatingActionButton = {
            StartGameButton(onStartButtonClick = {
                navController.navigate(Screen.InterfaceScreen.route)
                /*Checks to see if the minimum amount of players
                are in the lobby then starts game and switches to InterfaceScreen for all players*/
            })
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
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
            Spacer(modifier = Modifier.fillMaxWidth().height(20.dp))
            Text(
                text = "Join Code: $joinCode", fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center, modifier = Modifier.padding(top = 10.dp)
            )
            PlayerList()

        }
    }
}

//A list of players that are currently in the lobby. Still needs work to display player name
@Preview
@Composable
fun PlayerList() {
    LazyColumn() {
        itemsIndexed(
            //Place a list of players here
            listOf(
                "Troy",
                "Matt",
                "Mario",
                "Dov",
                "Rocket",
                "Moose",
                "Rex",
                "Olive",
                "Princess",
                "Winnie",
                "Chloe"
            )
        ) { index, playerName ->

            val backgroundColor =
                if (index % 2 == 0) {
                    colorResource(id = R.color.purple_200)
                } else {
                    colorResource(id = R.color.purple_500)
                }

            Text(
                text = playerName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .padding(14.dp)
                    .border(1.dp, Color.DarkGray, RoundedCornerShape(4.dp))
                    .background(backgroundColor)
            )
        }
    }
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
    errorMessage: String,
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

    Text(
        text = errorMessage,
        fontFamily = FontFamily.Monospace,
        textAlign = TextAlign.Left,
        fontSize = 12.sp,
        fontWeight = FontWeight.Light,
        color = Color.Red,
        fontStyle = FontStyle.Italic,
        modifier = Modifier
            .padding(top = 1.dp, bottom = 3.dp)
    )
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

    OutlinedButton(
        onClick = { onSubmitClick }, modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp, top = 10.dp)
    ) {
        Text(
            text = "Reset Password",
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RegisterFields(
    email: String,
    emailErrorMessage: String,
    username: String,
    usernameErrorMessage: String,
    password: String,
    passwordErrorMessage: String,
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

    Text(
        text = emailErrorMessage,
        fontFamily = FontFamily.Monospace,
        textAlign = TextAlign.Left,
        fontSize = 12.sp,
        fontWeight = FontWeight.Light,
        color = Color.Red,
        fontStyle = FontStyle.Italic,
        modifier = Modifier
            .padding(top = 1.dp, bottom = 3.dp)
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

    Text(
        text = usernameErrorMessage,
        fontFamily = FontFamily.Monospace,
        textAlign = TextAlign.Left,
        fontSize = 12.sp,
        fontWeight = FontWeight.Light,
        color = Color.Red,
        fontStyle = FontStyle.Italic,
        modifier = Modifier
            .padding(top = 1.dp, bottom = 3.dp)
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

    Text(
        text = passwordErrorMessage,
        fontFamily = FontFamily.Monospace,
        textAlign = TextAlign.Left,
        fontSize = 12.sp,
        fontWeight = FontWeight.Light,
        color = Color.Red,
        fontStyle = FontStyle.Italic,
        modifier = Modifier
            .padding(top = 1.dp, bottom = 3.dp)
    )

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
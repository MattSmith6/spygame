package com.example.spygame

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
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

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.LoginScreen.route) {
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Screen.RegisterScreen.route) {
            RegisterScreen(navController = navController)
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
            onForgotPassClick = {/*Switches to forgot password screen */ }
        )
        LoginRegisterFooter(
            mainButtonTxt = "LOG IN",
            onMainButtonClick = { /*Checks account details, if wrong then prompts to try again, if not then goes to MenuScreen()*/ },
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
            onMenuButtonClick =  {/*Prompts for join code*/}
        )

        MenuButton( //Lets the player create their own game and invite
            text = "HOST",
            onMenuButtonClick =  {/*Switches to CreateGameScreen*/}
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

@Preview
@Composable
fun LobbyScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 15.dp)
                .background(color = Color.DarkGray)
        )
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
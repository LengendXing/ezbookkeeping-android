package com.ezbookkeeping.android.ui.screen.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ezbookkeeping.android.ui.navigation.Routes

@Composable
fun LoginScreenPlaceholder(navController: NavController? = null) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("EZ Bookkeeping", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { navController?.navigate(Routes.HOME) }, modifier = Modifier.fillMaxWidth()) {
                Text("Login")
            }
        }
    }
}

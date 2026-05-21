package com.ezbookkeeping.android.ui.screen.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ezbookkeeping.android.R
import com.ezbookkeeping.android.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = { TopAppBar(title = { Text("Forgot Password") }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Enter your email address and we'll send you a reset link.", style = MaterialTheme.typography.bodyMedium)
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Button(onClick = { isLoading = true; result = "Reset link sent to $email"; isLoading = false }, modifier = Modifier.fillMaxWidth(), enabled = email.isNotBlank() && !isLoading) {
                if (isLoading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary) else Text("Send Reset Link")
            }
            result?.let {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Text(it, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { navController.navigate(Routes.LOGIN) }, modifier = Modifier.fillMaxWidth()) { Text("Back to Login") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(navController: NavController) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = { TopAppBar(title = { Text("Reset Password") }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = password, onValueChange = { password = it; error = null }, label = { Text("New Password") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it; error = null }, label = { Text("Confirm Password") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            Button(onClick = {
                if (password != confirmPassword) { error = "Passwords do not match"; return@Button }
                if (password.length < 6) { error = "Password must be at least 6 characters"; return@Button }
                isLoading = true
                navController.navigate(Routes.LOGIN)
            }, modifier = Modifier.fillMaxWidth(), enabled = password.isNotBlank() && !isLoading) {
                if (isLoading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary) else Text("Reset Password")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyEmailScreen(navController: NavController) {
    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = { TopAppBar(title = { Text("Verify Email") }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(24.dp))
            Text("A verification code has been sent to your email.", style = MaterialTheme.typography.bodyMedium)
            OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Verification Code") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Button(onClick = { isLoading = true; result = "Email verified successfully"; isLoading = false }, modifier = Modifier.fillMaxWidth(), enabled = code.isNotBlank() && !isLoading) {
                if (isLoading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary) else Text("Verify")
            }
            result?.let {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Text(it, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            TextButton(onClick = { /* resend */ }) { Text("Resend Code") }
        }
    }
}

@Composable
fun OAuth2CallbackScreen(navController: NavController) {
    LaunchedEffect(Unit) { navController.navigate(Routes.HOME) { popUpTo(0) { inclusive = true } } }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
}

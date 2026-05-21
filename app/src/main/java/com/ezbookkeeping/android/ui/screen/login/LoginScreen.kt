package com.ezbookkeeping.android.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R
import com.ezbookkeeping.android.ui.navigation.Routes

@Composable
fun LoginScreen(navController: NavController) {
    val vm: LoginViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo + Title (matches original)
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            if (state.tips != null) {
                Spacer(Modifier.height(8.dp))
                Text(state.tips!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(24.dp))

            // Standalone mode quick start
            if (vm.isStandaloneMode()) {
                Button(
                    onClick = { vm.loginStandalone() },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(stringResource(R.string.quick_start))
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text(stringResource(R.string.or_connect_server), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
            }

            // Username + Password fields (matches original layout)
            OutlinedTextField(
                value = state.username,
                onValueChange = vm::onUsernameChange,
                label = { Text(stringResource(R.string.username)) },
                placeholder = { Text(stringResource(R.string.username_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !state.isLoading
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.password,
                onValueChange = vm::onPasswordChange,
                label = { Text(stringResource(R.string.password)) },
                placeholder = { Text(stringResource(R.string.password_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = !state.isLoading
            )
            Spacer(Modifier.height(8.dp))

            // Forget password + Switch desktop (matches original links row)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { /* TODO forget password */ }) { Text(stringResource(R.string.forget_password), style = MaterialTheme.typography.bodySmall) }
            }

            Spacer(Modifier.height(8.dp))

            // Log In button (matches original)
            Button(
                onClick = { vm.login() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = !state.isLoading && !vm.isInputEmpty()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(stringResource(R.string.log_in))
                }
            }

            // Don't have an account? Create an account (matches original)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.no_account), style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = { navController.navigate(Routes.SIGNUP) }, contentPadding = PaddingValues(0.dp)) {
                    Text(stringResource(R.string.create_account), style = MaterialTheme.typography.bodySmall)
                }
            }

            // 2FA sheet trigger (if needed)
            if (state.show2faSheet) {
                AlertDialog(
                    onDismissRequest = { vm.dismiss2fa() },
                    title = { Text(stringResource(R.string.two_factor_auth)) },
                    text = {
                        Column {
                            if (state.twoFaType == "passcode") {
                                OutlinedTextField(value = state.twoFaCode, onValueChange = vm::onTwoFaCodeChange, placeholder = { Text(stringResource(R.string.passcode)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), singleLine = true)
                            } else {
                                OutlinedTextField(value = state.twoFaCode, onValueChange = vm::onTwoFaCodeChange, placeholder = { Text(stringResource(R.string.backup_code)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                            }
                        }
                    },
                    confirmButton = { Button(onClick = { vm.verify2fa() }) { Text(stringResource(R.string.verify)) } },
                    dismissButton = { TextButton(onClick = { vm.switch2faType() }) { Text(if (state.twoFaType == "passcode") stringResource(R.string.use_backup_code) else stringResource(R.string.use_passcode)) } }
                )
            }

            if (state.error != null) {
                Spacer(Modifier.height(8.dp))
                Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // Powered by (matches original bottom)
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Powered by ezBookkeeping ${state.version}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

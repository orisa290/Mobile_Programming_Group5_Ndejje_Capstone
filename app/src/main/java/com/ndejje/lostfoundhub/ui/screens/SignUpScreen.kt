package com.ndejje.lostfoundhub.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ndejje.lostfoundhub.R
import com.ndejje.lostfoundhub.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // ALL string resources moved outside the button callback
    val nameRequiredText = stringResource(R.string.name_required)
    val emailText = stringResource(R.string.email)
    val passwordText = stringResource(R.string.password)
    val passwordMismatchText = stringResource(R.string.password_mismatch)
    val passwordTooShortText = stringResource(R.string.password_too_short)
    val signupErrorText = stringResource(R.string.signup_error)

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.create_account), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text(stringResource(R.string.full_name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(stringResource(R.string.confirm_password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                when {
                    displayName.isBlank() -> errorMessage = nameRequiredText
                    email.isBlank() -> errorMessage = emailText
                    password.isBlank() -> errorMessage = passwordText
                    password != confirmPassword -> errorMessage = passwordMismatchText
                    password.length < 6 -> errorMessage = passwordTooShortText
                    else -> {
                        isLoading = true
                        authViewModel.signUp(email, password, displayName) { success, error ->
                            isLoading = false
                            if (success) {
                                onSignUpSuccess()
                            } else {
                                errorMessage = error ?: signupErrorText
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(Modifier.size(20.dp))
            } else {
                Text(stringResource(R.string.sign_up))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text(stringResource(R.string.already_have_account))
        }
    }
}

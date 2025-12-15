package com.loiev.geonot.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loiev.geonot.R // Переконайтеся, що ви додали ic_google_logo.xml в res/drawable
import com.loiev.geonot.ui.theme.GeonotTheme

@Composable
fun LoginScreen(
    onGoogleSignInClicked: () -> Unit,
    onEmailSignInClicked: (String, String) -> Unit,
    onEmailSignUpClicked: (String, String, String) -> Unit,
    errorMessage: String?
) {
    var isRegisterMode by remember { mutableStateOf(true) }

    var login by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("GeoNot", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = if (isRegisterMode) "Create Account" else "Log In",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (isRegisterMode) {
                OutlinedTextField(
                    value = login,
                    onValueChange = { login = it },
                    label = { Text("Login") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (errorMessage != null) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (isRegisterMode) {
                        onEmailSignUpClicked(login, email, password)
                    } else {
                        onEmailSignInClicked(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(if (isRegisterMode) "Register" else "Log In")
            }

            TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
                Text(if (isRegisterMode) "Already have an account? Log In" else "Don't have an account? Sign Up")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("- or -", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))

            GoogleSignInButton(onClick = onGoogleSignInClicked)
        }
    }
}

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_google_logo),
        contentDescription = "Sign in with Google",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    GeonotTheme {
        LoginScreen(
            onGoogleSignInClicked = {},
            onEmailSignInClicked = { _, _ -> },
            onEmailSignUpClicked = { _, _, _ -> },
            errorMessage = null
        )
    }
}
package com.loiev.geonot.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loiev.geonot.ui.theme.GeonotTheme

@Composable
fun LoginScreen(
    onGoogleSignInClicked: () -> Unit // Лямбда, яка буде викликатися при кліку
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Тут можна додати ваш логотип
            Text("GeoNot", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(48.dp))

            Text("Log in / Create Account", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(32.dp))

            Text("- or Sign in with -", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка входу через Google
            Button(
                onClick = onGoogleSignInClicked,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                // Тут можна додати іконку Google
                Text("Sign in with Google")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    GeonotTheme {
        LoginScreen(onGoogleSignInClicked = {})
    }
}
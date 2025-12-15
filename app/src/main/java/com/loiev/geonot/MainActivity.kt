package com.loiev.geonot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.loiev.geonot.ui.MainScreen
import com.loiev.geonot.ui.screens.LoginScreen
import com.loiev.geonot.ui.theme.GeonotTheme
import com.loiev.geonot.ui.viewmodels.AuthState
import com.loiev.geonot.ui.viewmodels.AuthViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GeonotTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val authState by authViewModel.authState.collectAsState()

                    val googleSignInLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult(),
                        onResult = { result ->
                            result.data?.let { intent ->
                                authViewModel.signInWithGoogle(intent)
                            }
                        }
                    )

                    when (val state = authState) {
                        is AuthState.Authenticated -> {
                            MainScreen()
                        }
                        is AuthState.Unauthenticated, is AuthState.Error -> {
                            LoginScreen(
                                onGoogleSignInClicked = {
                                    authViewModel.clearError()
                                    googleSignInLauncher.launch(authViewModel.getGoogleSignInIntent())
                                },
                                onEmailSignInClicked = { email, password ->
                                    authViewModel.signInWithEmail(email, password)
                                },
                                onEmailSignUpClicked = { login, email, password ->
                                    authViewModel.signUpWithEmail(login, email, password)
                                },
                                errorMessage = if (state is AuthState.Error) state.message else null
                            )
                        }
                        is AuthState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}
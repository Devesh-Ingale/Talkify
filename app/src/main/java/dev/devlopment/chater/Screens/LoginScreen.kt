
package dev.devlopment.chater.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseNetworkException
import dev.devlopment.chater.R
import dev.devlopment.chater.Repository.Result
import dev.devlopment.chater.ViewModels.AuthViewModel
import dev.devlopment.chater.ui.theme.Black
import dev.devlopment.chater.ui.theme.BlueGray
import dev.devlopment.chater.ui.theme.focusedTextFieldText
import dev.devlopment.chater.ui.theme.textFieldContainer
import dev.devlopment.chater.ui.theme.unfocusedTextFieldText


@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val result by authViewModel.authResult.observeAsState()
    val result2 by authViewModel.forgotPasswordResult.observeAsState()
    var errorMessage by remember { mutableStateOf<String?>(null) }

//    fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        FirebaseAuth.getInstance().signInWithCredential(credential)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    // Sign in success
//                    onLoginSuccess()
//                } else {
//                    errorMessage = task.exception?.message
//                }
//            }
//    }
//
//    val googleSignInLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == ComponentActivity.RESULT_OK) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//            try {
//                val account = task.getResult(ApiException::class.java)!!
//                firebaseAuthWithGoogle(account.idToken!!)
//            } catch (e: ApiException) {
//                errorMessage = "Google sign-in failed: ${e.localizedMessage}"
//            }
//        }
//    }

    LaunchedEffect(result) {
        result?.let { authResult ->
            when (authResult) {
                is Result.Success -> {
                    onLoginSuccess()
                }
                is Result.Error -> {
                    errorMessage = when (authResult.exception) {
                        is FirebaseNetworkException -> "Network error: Please check your internet connection and try again."
                        else -> authResult.exception.message
                    }
                }
            }
        }
    }

    LaunchedEffect(result2) {
        result2?.let { result ->
            when (result) {
                is Result.Success -> {
                    // Handle success, possibly show a snackbar or toast
                    // with a message that the password reset email has been sent
                }
                is Result.Error -> {
                    errorMessage = result.exception.message
                }
            }
        }
    }

    Surface {
        Column(modifier = Modifier.fillMaxSize()) {
            val uiColor: Color = if (isSystemInDarkTheme()) Color.White else Color.Black
            val background: Painter = if (isSystemInDarkTheme()) {
                painterResource(id = R.drawable.shapedark)
            } else {
                painterResource(id = R.drawable.shapelight)
            }

            Box(
                contentAlignment = Alignment.TopCenter
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = 0.46f),
                    painter = background,
                    contentDescription = "Shape",
                    contentScale = ContentScale.FillBounds
                )
                Row(
                    modifier = Modifier.padding(top = 80.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(42.dp),
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = stringResource(id = R.string.app_name),
                        tint = uiColor
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Column {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.headlineMedium,
                            color = uiColor
                        )
                        Text(
                            text = stringResource(id = R.string.headline),
                            style = MaterialTheme.typography.titleMedium,
                            color = uiColor
                        )
                    }
                }
                Text(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .align(alignment = Alignment.BottomCenter),
                    text = "Login",
                    style = MaterialTheme.typography.headlineLarge,
                    color = uiColor
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp)
            ) {
                TextField(modifier = Modifier.fillMaxWidth(),
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(
                            text = "Email",
                            style = MaterialTheme.typography.labelMedium,
                            color = uiColor
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.unfocusedTextFieldText,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.focusedTextFieldText,
                        unfocusedContainerColor = MaterialTheme.colorScheme.textFieldContainer,
                        focusedContainerColor = MaterialTheme.colorScheme.textFieldContainer
                    )
                )
                Spacer(modifier = Modifier.height(15.dp))

                TextField(modifier = Modifier.fillMaxWidth(),
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(
                            text = "Password",
                            style = MaterialTheme.typography.labelMedium,
                            color = uiColor
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.unfocusedTextFieldText,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.focusedTextFieldText,
                        unfocusedContainerColor = MaterialTheme.colorScheme.textFieldContainer,
                        focusedContainerColor = MaterialTheme.colorScheme.textFieldContainer
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { authViewModel.sendPasswordResetEmail(email) }) {
                            Text(
                                text = "Forgot?",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                color = uiColor
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    onClick = { authViewModel.login(email, password) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) BlueGray else Black,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(size = 4.dp)
                ) {
                    Text(
                        text = "Log in",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Text(
//                            text = "Or continue with",
//                            style = MaterialTheme.typography.labelMedium.copy(
//                                color = Color(0xFF64748B)
//                            )
//                        )
//
//                        Spacer(modifier = Modifier.height(20.dp))
//
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            val context = LocalContext.current
//                            Row(
//                                modifier = Modifier
//                                    .clip(RoundedCornerShape(4.dp))
//                                    .socialMedia()
//                                    .clickable {
//                                        val gso = GoogleSignInOptions
//                                            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                                            .requestIdToken(context.getString(R.string.default_web_client_id))
//                                            .requestEmail()
//                                            .build()
//                                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
//                                        val signInIntent = googleSignInClient.signInIntent
//                                        googleSignInLauncher.launch(signInIntent)
//                                    }
//                                    .height(40.dp)
//                                    .weight(1f),
//                                horizontalArrangement = Arrangement.Center,
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Image(
//                                    painter = painterResource(id = R.drawable.google),
//                                    contentDescription = "Google",
//                                    modifier = Modifier.size(16.dp)
//                                )
//                                Spacer(modifier = Modifier.width(5.dp))
//                                Text(
//                                    text = "Google",
//                                    style = MaterialTheme.typography.labelMedium.copy(
//                                        color = Color(0xFF64748B)
//                                    )
//                                )
//                            }
//                        }
//                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxHeight(fraction = 0.8f)
                            .fillMaxWidth()
                            .clickable { onNavigateToSignUp() },
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = uiColor.copy(alpha = 0.5f)
                                    )
                                ) { append("Don’t have an account? ") }

                                withStyle(
                                    style = SpanStyle(
                                        color = if (isSystemInDarkTheme()) BlueGray else Black,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                ) { append("Sign up") }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

    @SuppressLint("ModifierFactoryUnreferencedReceiver")
    fun Modifier.socialMedia(): Modifier = composed {
        if (isSystemInDarkTheme()) {
            background(Color.Transparent).border(
                width = 1.dp,
                color = BlueGray,
                shape = RoundedCornerShape(4.dp)
            )
        } else {
            background(Color(0xFFF4FDFE)).border(
                width = 1.dp,
                color = BlueGray,
                shape = RoundedCornerShape(4.dp)
            )
        }
    }



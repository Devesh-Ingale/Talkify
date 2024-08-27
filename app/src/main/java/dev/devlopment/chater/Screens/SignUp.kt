package dev.devlopment.chater.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import dev.devlopment.chater.R
import dev.devlopment.chater.Repository.Result
import dev.devlopment.chater.ViewModels.AuthViewModel
import dev.devlopment.chater.ui.theme.Black
import dev.devlopment.chater.ui.theme.BlueGray
import dev.devlopment.chater.ui.theme.Roboto
import dev.devlopment.chater.ui.theme.focusedTextFieldText
import dev.devlopment.chater.ui.theme.textFieldContainer
import dev.devlopment.chater.ui.theme.unfocusedTextFieldText


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    val result by authViewModel.authResult.observeAsState() // Observe auth result
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Function to handle the result after sign up
    fun handleAuthResult(authResult: Result<Boolean>?) {
        authResult?.let {
            when (it) {
                is Result.Success -> {
                    onSignUpSuccess() // Navigate to the next screen or show success message
                }
                is Result.Error -> {
                    errorMessage = it.exception.message // Display error message
                }
            }
        }
    }


    // Call the function to handle the result after sign up
    LaunchedEffect(result) {
        handleAuthResult(result)
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
                )
                {
                    Spacer(modifier = Modifier.width(15.dp))
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            modifier = Modifier.size(42.dp),
                            painter = painterResource(id = R.drawable.meetx),
                            contentDescription = stringResource(id = R.string.app_name),
                            tint = uiColor
                        )

                        Text(
                            text = "Welcome to " + stringResource(id = R.string.app_name),
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
                    text = "Sign up",
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
                TextField(
                    modifier = Modifier.fillMaxWidth(),
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

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(
                            text = "password",
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
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(15.dp))

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = {
                        Text(
                            text = "Roll No",
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

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = {
                        Text(
                            text = "Name",
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
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    onClick = {
                        authViewModel.signUp(email, password, firstName, lastName)
                        authViewModel.login(email, password)
                        handleAuthResult(result) // Handle result immediately
                        email = ""
                        password = ""
                        firstName = ""
                        lastName = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) BlueGray else Black,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(size = 4.dp)
                ) {
                    Text(
                        text = "Sign Up",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight(fraction = 0.8f)
                        .fillMaxWidth()
                        .clickable { onNavigateToLogin() },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFF94A3B8),
                                    fontSize = 14.sp,
                                    fontFamily = Roboto,
                                    fontWeight = FontWeight.Normal
                                )
                            ) {
                                append("Already have an account?")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = uiColor,
                                    fontSize = 14.sp,
                                    fontFamily = Roboto,
                                    fontWeight = FontWeight.Medium
                                )
                            ) {
                                append(" ")
                                append("Sign in.")
                            }
                        }
                    )
                }
            }
        }
    }
}

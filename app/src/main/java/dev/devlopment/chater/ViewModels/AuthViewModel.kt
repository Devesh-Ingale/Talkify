package dev.devlopment.chater.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dev.devlopment.chater.MainAndUtils.Injection
import dev.devlopment.chater.MainAndUtils.SharedPreferencesManager
import dev.devlopment.chater.Repository.Result
import dev.devlopment.chater.Repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val userRepository: UserRepository = UserRepository(
        FirebaseAuth.getInstance(),
        Injection.instance()
    )

    private val _currentUserEmail = MutableLiveData<String?>()
    val currentUserEmail: MutableLiveData<String?> get() = _currentUserEmail

    init {
        SharedPreferencesManager.initialize()
        val isFirstLaunchOrLoggedOut = !SharedPreferencesManager.getBoolean("isLoggedIn", false)
        if (!isFirstLaunchOrLoggedOut) {
            attemptAutoLogin()
        }
    }

    private val _authResult = MutableLiveData<Result<Boolean>>()
    val authResult: LiveData<Result<Boolean>> get() = _authResult

    private val _loggedIn = MutableLiveData<Boolean>().apply { value = false }
    val loggedIn: LiveData<Boolean> get() = _loggedIn

    private fun attemptAutoLogin() {
        val storedEmail = SharedPreferencesManager.getString("email", "")
        val storedPassword = SharedPreferencesManager.getString("password", "")

        if (storedEmail.isNotEmpty() && storedPassword.isNotEmpty()) {
            login(storedEmail, storedPassword)
        } else {
            _loggedIn.value = false
        }
    }

    fun signUp(email: String, password: String, firstName: String, lastName: String) {
        viewModelScope.launch {
            val result = userRepository.signUp(email, password, firstName, lastName)
            _authResult.value = result
            if (result is Result.Success) {
                _loggedIn.value = true
                _currentUserEmail.value = email
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = userRepository.login(email, password)
            if (result != null) {
                _authResult.value = result
                if (result is Result.Success) {
                    SharedPreferencesManager.saveString("email", email)
                    SharedPreferencesManager.saveString("password", password)
                    SharedPreferencesManager.saveBoolean("isLoggedIn", true)
                    _loggedIn.value = true
                    _currentUserEmail.value = email
                }
            } else {
                _authResult.value = Result.Error(Exception("Login failed"))
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            val result = userRepository.sendPasswordResetEmail(email)
            _forgotPasswordResult.postValue(result)
        }
    }

    fun logout() {
        SharedPreferencesManager.clearAll()
        SharedPreferencesManager.saveBoolean("isLoggedIn", false)
        _loggedIn.value = false
        _currentUserEmail.value = null
    }

    private val _forgotPasswordResult: MutableLiveData<Result<Unit>> = MutableLiveData()
    val forgotPasswordResult: LiveData<Result<Unit>> get() = _forgotPasswordResult
}

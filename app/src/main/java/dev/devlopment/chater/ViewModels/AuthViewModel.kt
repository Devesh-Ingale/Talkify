package dev.devlopment.chater.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dev.devlopment.chater.MainAndUtils.Injection
import dev.devlopment.chater.Repository.Result
import dev.devlopment.chater.Repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val userRepository: UserRepository
    init {
        userRepository = UserRepository(
            FirebaseAuth.getInstance(),
            Injection.instance()
        )
    }

    private val _authResult = MutableLiveData<Result<Boolean>>()
    val authResult: LiveData<Result<Boolean>> get() = _authResult
    fun signUp(email: String, password: String, firstName: String, lastName: String) {
        viewModelScope.launch {
            _authResult.value = userRepository.signUp(email, password, firstName, lastName)
        }
    }
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = userRepository.login(email, password)
        }
    }

    private val _forgotPasswordResult: MutableLiveData<Result<Unit>> = MutableLiveData()
    val forgotPasswordResult: LiveData<Result<Unit>> = _forgotPasswordResult
    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            val result = userRepository.sendPasswordResetEmail(email)
            _forgotPasswordResult.postValue(result)
        }
    }


}



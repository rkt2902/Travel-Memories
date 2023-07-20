package com.ipca.travelmemories.views.splash_screen

import androidx.lifecycle.*
import com.ipca.travelmemories.repositories.AuthRepository

class SplashScreenViewModel : ViewModel() {
    private var authRepository = AuthRepository()

    fun isLoggedFromFirebase(callback: (Result<Boolean>) -> Unit) {
        val currentUser = authRepository.getUserID()

        if (currentUser != null) {
            callback.invoke(Result.success(true))
        } else {
            callback.invoke(Result.failure(Throwable("Não existe utilizador logado.")))
        }
    }
}
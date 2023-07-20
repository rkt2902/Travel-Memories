package com.ipca.travelmemories.views.auth

import androidx.lifecycle.ViewModel
import com.ipca.travelmemories.repositories.AuthRepository

class AuthViewModel : ViewModel() {
    private var authRepository = AuthRepository()

    fun loginUserFromFirebase(
        email: String,
        password: String,
        callback: (Result<Boolean>) -> Unit
    ) {
        if (email == "" || password == "") {
            callback(Result.failure(Throwable("Campos vazios.")))
            return
        }

        // autenticar utilizador
        authRepository.signIn(email, password)
            .addOnSuccessListener {
                callback(Result.success(true))
            }
            .addOnFailureListener {
                callback(Result.failure(Throwable("Erro ao fazer login na aplicação.")))
            }
    }
}
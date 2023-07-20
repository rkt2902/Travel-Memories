package com.ipca.travelmemories.views.sign_up

import androidx.lifecycle.*
import com.ipca.travelmemories.models.UserModel
import com.ipca.travelmemories.repositories.AuthRepository
import com.ipca.travelmemories.repositories.UserRepository

class SignUpViewModel : ViewModel() {
    private var userRepository = UserRepository()
    private var authRepository = AuthRepository()

    private fun setUser(name: String): UserModel {
        return UserModel(name, "")
    }

    fun registerUserToFirebase(
        name: String,
        password: String,
        email: String,
        callback: (Result<Boolean>) -> Unit
    ) {
        if (name == "" || email == "" || password == "") {
            callback.invoke(Result.failure(Throwable("Campos vazios.")))
            return
        }

        // adicionar utilizador na autenticação do firebase
        authRepository.signUp(email, password)
            .addOnSuccessListener {
                val user = setUser(name)

                // adicionar utilizador na base de dados
                userRepository.create(authRepository.getUserID()!!, user.convertToHashMap())
                    .addOnSuccessListener {
                        callback.invoke(Result.success(true))
                    }
                    .addOnFailureListener {
                        callback.invoke(Result.failure(Throwable("Erro ao registar utilizador.")))
                    }
            }
            .addOnFailureListener {
                callback.invoke(Result.failure(Throwable("Erro ao registar utilizador.")))
            }
    }
}
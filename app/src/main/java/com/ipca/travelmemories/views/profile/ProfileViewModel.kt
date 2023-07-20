package com.ipca.travelmemories.views.profile

import androidx.lifecycle.*
import com.ipca.travelmemories.models.UserModel
import com.ipca.travelmemories.repositories.AuthRepository
import com.ipca.travelmemories.repositories.UserRepository

class ProfileViewModel : ViewModel() {
    private var userLiveData: MutableLiveData<Result<UserModel>> = MutableLiveData()

    private var userRepository = UserRepository()
    private var authRepository = AuthRepository()

    fun getUserFromFirebase(): LiveData<Result<UserModel>> {
        val user = authRepository.getUser()

        user?.let {
            val userID = it.uid
            val email = it.email

            userRepository.selectOne(userID)
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val user = UserModel.convertToUserModel(userID, email!!, document.data!!)
                        userLiveData.value = Result.success(user)
                    } else {
                        userLiveData.value =
                            Result.failure(Throwable("Erro ao obter o utilizador autenticado."))
                    }
                }
                .addOnFailureListener {
                    userLiveData.value =
                        Result.failure(Throwable("Erro ao obter o utilizador autenticado."))
                }
        }

        return userLiveData
    }

    fun editUserDataFromFirebase(
        name: String,
        country: String?,
        callback: (Result<Boolean>) -> Unit
    ) {
        if (name == "") {
            callback.invoke(Result.failure(Throwable("O nome de utilizador é obrigatório.")))
            return
        }

        val userID = authRepository.getUserID()!!
        val user = UserModel(name, country)

        userRepository.update(userID, user.convertToHashMap())
            .addOnSuccessListener {
                callback.invoke(Result.success(true))
            }
            .addOnFailureListener {
                callback.invoke(Result.failure(Throwable("Erro ao atualizar dados do utilizador.")))
            }
    }

    fun editUserEmailFromFirebase(email: String, callback: (Result<Boolean>) -> Unit) {
        if (email == "") {
            callback.invoke(Result.failure(Throwable("O email é obrigatório.")))
            return
        }

        authRepository.updateEmail(email)
            .addOnSuccessListener {
                callback.invoke(Result.success(true))
            }
            .addOnFailureListener {
                callback.invoke(Result.failure(Throwable("Erro ao atualizar email do utilizador.")))
            }
    }

    fun editUserPasswordFromFirebase(newPassword: String, callback: (Result<Boolean>) -> Unit) {
        if (newPassword.length < 6) {
            callback.invoke(Result.failure(Throwable("A palavra-passe deve ter no mínimo 6 caracteres.")))
            return
        }

        authRepository.updatePassword(newPassword)
            .addOnSuccessListener {
                callback.invoke(Result.success(true))
            }
            .addOnFailureListener {
                callback.invoke(Result.failure(Throwable("Erro ao atualizar a palavra-passe do utilizador.")))
            }
    }

    fun removeUserFromFirebase(callback: (Result<Boolean>) -> Unit) {
        val userID = authRepository.getUserID()!!

        authRepository.delete()
            .addOnSuccessListener {
                userRepository.delete(userID)
                    .addOnSuccessListener {
                        callback.invoke(Result.success(true))
                    }
                    .addOnFailureListener {
                        callback.invoke(Result.failure(Throwable("Erro ao apagar conta do utilizador.")))
                    }
            }
            .addOnFailureListener {
                callback.invoke(Result.failure(Throwable("Erro ao apagar conta do utilizador.")))
            }
    }

    fun signOutFromFirebase() {
        authRepository.signOut()
    }
}
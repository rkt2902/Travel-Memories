package com.ipca.travelmemories.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

// para lidar com utilizadores na autenticação do firebase
class AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    fun signUp(email: String, password: String): Task<AuthResult> {
        val result = auth.createUserWithEmailAndPassword(email, password)
        return result
    }

    fun signIn(email: String, password: String): Task<AuthResult> {
        val result = auth.signInWithEmailAndPassword(email, password)
        return result
    }

    fun signOut() {
        auth.signOut()
    }

    fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun getUserID(): String? {
        return auth.currentUser?.uid
    }

    fun updateEmail(email: String): Task<Void> {
        val user = getUser()
        return user!!.updateEmail(email)
    }

    fun updatePassword(newPassword: String): Task<Void> {
        val user = getUser()
        return user!!.updatePassword(newPassword)
    }

    fun delete(): Task<Void> {
        val user = getUser()
        return user!!.delete()
    }
}
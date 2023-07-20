package com.ipca.travelmemories.views.photo_create

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.ipca.travelmemories.models.PhotoModel
import com.ipca.travelmemories.repositories.AuthRepository
import com.ipca.travelmemories.repositories.PhotoRepository
import com.ipca.travelmemories.utils.ParserUtil
import java.io.File
import java.io.IOException
import java.util.*

class PhotoCreateViewModel : ViewModel() {
    private var photoRepository = PhotoRepository()
    private var authRepository = AuthRepository()

    lateinit var pathInDevice: String
    private lateinit var filename: String
    private lateinit var fullPath: String

    private fun setFullPath(userID: String, tripID: String): String {
        return "/${userID}/${tripID}/photos/${filename}.jpg"
    }

    private fun getFullPath(): String {
        return fullPath
    }

    private fun setPhoto(id: String, path: String, description: String): PhotoModel {
        return PhotoModel(id, path, description)
    }

    fun addPhotoToDatabase(
        tripID: String,
        description: String,
        callback: (Result<Boolean>) -> Unit
    ) {
        // criar novo documento no firebase onde será guardada a nova foto
        val userID = authRepository.getUserID()!!
        val documentReference = photoRepository.setDocumentBeforeCreate(userID, tripID)

        // criar foto
        fullPath = setFullPath(userID, tripID)
        val photo = setPhoto(tripID, fullPath, description)

        // adicionar à base de dados
        photoRepository.create(documentReference, photo.convertToHashMap())
            .addOnSuccessListener {
                callback.invoke(Result.success(true))
            }
            .addOnFailureListener {
                callback.invoke(Result.failure(Throwable("Erro ao adicionar foto.")))
            }
    }

    fun uploadFileToFirebase(callback: (String?) -> Unit) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val file = Uri.fromFile(File(pathInDevice))

        val ref = storageRef.child(getFullPath())
        val uploadTask = ref.putFile(file)

        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback.invoke(ref.path)
            } else {
                callback.invoke(null)
            }
        }
    }

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        // nome do ficheiro
        filename = ParserUtil.convertDateToString(Date(), "yyyyMMdd_HHmmss")
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            filename, /* prefixo */
            ".jpg", /* sufixo */
            storageDir /* diretório */
        ).apply {
            // guardar um ficheiro: caminho para uso com intents ACTION_VIEW
            pathInDevice = this.absolutePath
        }
    }
}
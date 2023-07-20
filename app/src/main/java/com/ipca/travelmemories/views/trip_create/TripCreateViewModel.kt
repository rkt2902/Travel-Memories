package com.ipca.travelmemories.views.trip_create

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.ipca.travelmemories.models.TripModel
import com.ipca.travelmemories.repositories.AuthRepository
import com.ipca.travelmemories.repositories.TripRepository
import com.ipca.travelmemories.utils.ParserUtil
import java.io.File
import java.io.IOException
import java.util.*

class TripCreateViewModel : ViewModel() {
    private var tripRepository = TripRepository()
    private var authRepository = AuthRepository()

    lateinit var pathInDevice: String
    private lateinit var filename: String
    private lateinit var fullPath: String

    private fun setFullPath(userID: String, tripID: String): String {
        return "/${userID}/${tripID}/${filename}.jpg"
    }

    private fun getFullPath(): String {
        return fullPath
    }

    fun addTripToFirebase(
        country: String,
        cities: String,
        startDate: String,
        endDate: String,
        callback: (Result<TripModel>) -> Unit
    ) {
        // criar novo documento no firebase onde será guardada a nova viagem
        val userID = authRepository.getUserID()!!
        val documentReference = tripRepository.setDocumentBeforeCreate(userID)

        // criar viagem
        val tripID = documentReference.id
        fullPath = setFullPath(userID, tripID)
        val trip = TripModel(
            tripID,
            country,
            cities,
            ParserUtil.convertStringToDate(startDate, "dd-MM-yyyy"),
            ParserUtil.convertStringToDate(endDate, "dd-MM-yyyy"),
            fullPath
        )

        // adicionar à base de dados
        tripRepository.create(documentReference, trip.convertToHashMap())
            .addOnSuccessListener {
                callback.invoke(Result.success(trip))
            }
            .addOnFailureListener {
                callback.invoke(Result.failure(Throwable("Erro ao adicionar viagem.")))
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
package com.ipca.travelmemories.views.trip_detail

import androidx.lifecycle.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.ipca.travelmemories.models.TripModel
import com.ipca.travelmemories.repositories.AuthRepository
import com.ipca.travelmemories.repositories.TripRepository
import com.ipca.travelmemories.utils.ParserUtil

class TripDetailViewModel : ViewModel() {
    private var tripRepository = TripRepository()
    private var authRepository = AuthRepository()

    fun getPhotoURI(coverPath: String, callback: (Result<String>?) -> Unit) {
        val photoReference = Firebase.storage.reference.child(coverPath)

        photoReference.downloadUrl
            .addOnSuccessListener { uri ->
                callback.invoke(Result.success(uri.toString()))
            }
            .addOnFailureListener {
                callback.invoke(Result.failure(Throwable("Erro ao visualizar capa da viagem.")))
            }
    }

    fun editTripFromFirebase(
        tripID: String,
        country: String,
        cities: String,
        startDate: String,
        endDate: String,
        callback: (Result<Boolean>) -> Unit
    ) {
        val userID = authRepository.getUserID()!!
        val trip = TripModel(
            tripID,
            country,
            cities,
            ParserUtil.convertStringToDate(startDate, "dd-MM-yyyy"),
            ParserUtil.convertStringToDate(endDate, "dd-MM-yyyy"),
            null
        )

        tripRepository.updateData(userID, tripID, trip.convertToHashMapWithoutCover())
            .addOnSuccessListener {
                callback(Result.success(true))
            }
            .addOnFailureListener {
                callback(Result.failure(Throwable("Erro ao atualizar dados da despesa.")))
            }
    }

    fun removeTripFromFirebase(
        tripID: String,
        coverPath: String,
        callback: (Result<Boolean>) -> Unit
    ) {
        val userID = authRepository.getUserID()!!

        tripRepository.delete(userID, tripID)
            .addOnSuccessListener {
                // remover ficheiro da foto da capa da viagem
                val storage = Firebase.storage
                val coverReference = storage.reference.child(coverPath)

                coverReference.delete()
                    .addOnFailureListener {
                        callback.invoke(Result.failure(Throwable("Erro ao apagar viagem.")))
                    }

                // remover ficheiros das fotos associadas à viagem
                val photosRef = storage.reference.child("${userID}/${tripID}/photos")

                photosRef.listAll()
                    .addOnSuccessListener { task ->
                        task.items.forEach { item ->
                            item.delete()
                        }
                        callback.invoke(Result.success(true))
                    }
                    .addOnFailureListener {
                        callback.invoke(Result.failure(Throwable("Erro ao apagar viagem.")))
                    }
            }
            .addOnFailureListener {
                callback.invoke(Result.failure(Throwable("Erro ao apagar viagem.")))
            }
    }
}
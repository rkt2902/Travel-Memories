package com.ipca.travelmemories.views.home

import androidx.lifecycle.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.ipca.travelmemories.models.TripModel
import com.ipca.travelmemories.repositories.AuthRepository
import com.ipca.travelmemories.repositories.TripRepository

class HomeViewModel : ViewModel() {
    private var result: MutableLiveData<Result<List<TripModel>>> = MutableLiveData()

    private var tripRepository = TripRepository()
    private var authRepository = AuthRepository()

    fun getTripsFromFirebase(): LiveData<Result<List<TripModel>>> {
        val userID = authRepository.getUserID()!!

        tripRepository.selectAll(userID)
            .addSnapshotListener(EventListener { documents, error ->
                if (error != null) {
                    result.value = Result.failure(Throwable("Erro ao obter viagens."))
                    return@EventListener
                }

                val trips: MutableList<TripModel> = mutableListOf()
                for (document in documents!!) {
                    val trip = TripModel.convertToTripModel(document.id, document.data)
                    trips.add(trip)
                }

                result.value = Result.success(trips)
            })

        return result
    }

    fun getPhotoURI(filePath: String, callback: (Result<String>?) -> Unit) {
        val photoReference = Firebase.storage.reference.child(filePath)

        photoReference.downloadUrl
            .addOnSuccessListener { uri ->
                callback(Result.success(uri.toString()))
            }
            .addOnFailureListener {
                callback(Result.failure(Throwable("Erro ao visualizar foto.")))
            }
    }
}
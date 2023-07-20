package com.ipca.travelmemories.views.photo_all

import androidx.lifecycle.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.ipca.travelmemories.models.PhotoModel
import com.ipca.travelmemories.repositories.AuthRepository
import com.ipca.travelmemories.repositories.PhotoRepository

class PhotoAllViewModel : ViewModel() {
    private var result: MutableLiveData<Result<List<PhotoModel>>> = MutableLiveData()

    private var photoRepository = PhotoRepository()
    private var authRepository = AuthRepository()

    fun getPhotosDataFromFirebase(tripID: String): LiveData<Result<List<PhotoModel>>> {
        val userID = authRepository.getUserID()!!

        photoRepository.selectAll(userID, tripID)
            .addSnapshotListener(EventListener { documents, error ->
                if (error != null) {
                    result.value = Result.failure(Throwable("Erro ao obter fotos."))
                    return@EventListener
                }

                val photos: MutableList<PhotoModel> = mutableListOf()
                for (document in documents!!) {
                    val photo = PhotoModel.convertToPhotoModel(document.id, document.data)
                    photos.add(photo)
                }

                result.value = Result.success(photos)
            })

        return result
    }

    fun getPhotoURI(filePath: String, callback: (Result<String>?) -> Unit) {
        val photoReference = Firebase.storage.reference.child(filePath)

        photoReference.downloadUrl
            .addOnSuccessListener { uri ->
                callback.invoke(Result.success(uri.toString()))
            }
            .addOnFailureListener {
                callback.invoke(Result.failure(Throwable("Erro ao visualizar foto.")))
            }
    }
}
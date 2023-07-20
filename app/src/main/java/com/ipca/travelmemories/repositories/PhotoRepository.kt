package com.ipca.travelmemories.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.HashMap

// para lidar com fotos na base de dados
class PhotoRepository {
    private val db = FirebaseFirestore.getInstance()

    fun setDocumentBeforeCreate(userID: String, tripID: String): DocumentReference {
        val documentReference = db.collection("users")
            .document(userID)
            .collection("trips")
            .document(tripID)
            .collection("photos")
            .document()
        return documentReference
    }

    fun create(documentReference: DocumentReference, photo: HashMap<String, Any?>): Task<Void> {
        return documentReference.set(photo)
    }

    fun selectAll(userID: String, tripID: String): CollectionReference {
        val collectionReference = db.collection("users")
            .document(userID)
            .collection("trips")
            .document(tripID)
            .collection("photos")
        return collectionReference
    }

    fun delete(userID: String, tripID: String, photoID: String): Task<Void> {
        val documentReference = db.collection("users")
            .document(userID)
            .collection("trips")
            .document(tripID)
            .collection("photos")
            .document(photoID)
        return documentReference.delete()
    }
}
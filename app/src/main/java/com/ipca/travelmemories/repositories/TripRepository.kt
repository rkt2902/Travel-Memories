package com.ipca.travelmemories.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.HashMap

class TripRepository {
    private val db = FirebaseFirestore.getInstance()

    fun setDocumentBeforeCreate(userID: String): DocumentReference {
        val documentReference = db.collection("users")
            .document(userID)
            .collection("trips")
            .document()
        return documentReference
    }

    fun create(documentReference: DocumentReference, trip: HashMap<String, Any?>): Task<Void> {
        return documentReference.set(trip)
    }

    fun selectAll(userID: String): CollectionReference {
        val collectionReference = db.collection("users")
            .document(userID)
            .collection("trips")
        return collectionReference
    }

    fun updateData(userID: String, tripID: String, trip: HashMap<String, Any?>): Task<Void> {
        val documentReference = db.collection("users")
            .document(userID)
            .collection("trips")
            .document(tripID)
        return documentReference.update(trip)
    }

    fun delete(userID: String, tripID: String): Task<Void> {
        val documentReference = db.collection("users")
            .document(userID)
            .collection("trips")
            .document(tripID)
        return documentReference.delete()
    }
}
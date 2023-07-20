package com.ipca.travelmemories.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.HashMap

class DiaryDayRepository {
    private val db = FirebaseFirestore.getInstance()

    fun setDocumentBeforeCreate(userID: String, tripID: String): DocumentReference {
        val documentReference = db.collection("users")
            .document(userID)
            .collection("trips")
            .document(tripID)
            .collection("diary")
            .document()
        return documentReference
    }

    fun create(documentReference: DocumentReference, diaryDay: HashMap<String, Any?>): Task<Void> {
        return documentReference.set(diaryDay)
    }

    fun selectAll(userID: String, tripID: String): CollectionReference {
        val collectionReference = db.collection("users")
            .document(userID)
            .collection("trips")
            .document(tripID)
            .collection("diary")
        return collectionReference
    }

    fun update(
        userID: String,
        tripID: String,
        diaryDayID: String,
        diaryDay: HashMap<String, Any?>
    ): Task<Void> {
        val documentReference = db.collection("users")
            .document(userID)
            .collection("trips")
            .document(tripID)
            .collection("diary")
            .document(diaryDayID)
        return documentReference.update(diaryDay)
    }

    fun delete(userID: String, tripID: String, diaryDayID: String): Task<Void> {
        val documentReference = db.collection("users")
            .document(userID)
            .collection("trips")
            .document(tripID)
            .collection("diary")
            .document(diaryDayID)
        return documentReference.delete()
    }
}
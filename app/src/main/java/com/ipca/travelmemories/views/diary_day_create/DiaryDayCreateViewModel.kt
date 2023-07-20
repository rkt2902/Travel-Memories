package com.ipca.travelmemories.views.diary_day_create

import androidx.lifecycle.ViewModel
import com.ipca.travelmemories.models.DiaryDayModel
import com.ipca.travelmemories.repositories.AuthRepository
import com.ipca.travelmemories.repositories.DiaryDayRepository
import com.ipca.travelmemories.utils.ParserUtil

class DiaryDayCreateViewModel : ViewModel() {
    private var diaryDayRepository = DiaryDayRepository()
    private var authRepository = AuthRepository()

    fun addDiaryDayToFirebase(
        tripID: String,
        title: String?,
        body: String,
        date: String,
        callback: (Result<Boolean>) -> Unit
    ) {
        // criar novo documento no firebase onde será guardada o nova dia do diário
        val userID = authRepository.getUserID()!!
        val documentReference = diaryDayRepository.setDocumentBeforeCreate(userID, tripID)

        // criar dia do diário
        val diaryDayID = documentReference.id
        val diaryDay = DiaryDayModel(
            diaryDayID,
            title,
            body,
            ParserUtil.convertStringToDate(date, "dd-MM-yyyy")
        )

        // adicionar à base de dados
        diaryDayRepository.create(documentReference, diaryDay.convertToHashMap())
            .addOnSuccessListener {
                callback(Result.success(true))
            }
            .addOnFailureListener {
                callback(Result.failure(Throwable("Erro ao adicionar dia ao diário.")))
            }
    }
}
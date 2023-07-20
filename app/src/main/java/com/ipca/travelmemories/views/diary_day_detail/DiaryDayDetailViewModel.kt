package com.ipca.travelmemories.views.diary_day_detail

import androidx.lifecycle.ViewModel
import com.ipca.travelmemories.models.DiaryDayModel
import com.ipca.travelmemories.repositories.AuthRepository
import com.ipca.travelmemories.repositories.DiaryDayRepository
import com.ipca.travelmemories.utils.ParserUtil

class DiaryDayDetailViewModel : ViewModel() {
    private var diaryDayRepository = DiaryDayRepository()
    private var authRepository = AuthRepository()

    fun editDiaryDayFromFirebase(
        tripID: String,
        diaryDayID: String,
        title: String,
        body: String,
        date: String,
        callback: (Result<Boolean>) -> Unit
    ) {
        val userID = authRepository.getUserID()!!
        val diaryDay = DiaryDayModel(
            diaryDayID,
            title,
            body,
            ParserUtil.convertStringToDate(date, "dd-MM-yyyy")
        )

        diaryDayRepository.update(userID, tripID, diaryDayID, diaryDay.convertToHashMap())
            .addOnSuccessListener {
                callback(Result.success(true))
            }
            .addOnFailureListener {
                callback(Result.failure(Throwable("Erro ao atualizar dia do diário.")))
            }
    }

    fun removeDiaryDayFromFirebase(
        tripID: String,
        diaryDayID: String,
        callback: (Result<Boolean>) -> Unit
    ) {
        val userID = authRepository.getUserID()!!

        diaryDayRepository.delete(userID, tripID, diaryDayID)
            .addOnSuccessListener {
                callback(Result.success(true))
            }
            .addOnFailureListener {
                callback(Result.failure(Throwable("Erro ao apagar dia do diário.")))
            }
    }
}
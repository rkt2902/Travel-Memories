package com.ipca.travelmemories.views.diary_day_all

import androidx.lifecycle.*
import com.google.firebase.firestore.EventListener
import com.ipca.travelmemories.models.DiaryDayModel
import com.ipca.travelmemories.repositories.AuthRepository
import com.ipca.travelmemories.repositories.DiaryDayRepository

class DiaryDayAllViewModel : ViewModel() {
    private var result: MutableLiveData<Result<List<DiaryDayModel>>> = MutableLiveData()

    private var diaryDayRepository = DiaryDayRepository()
    private var authRepository = AuthRepository()

    fun getDiaryDaysFromFirebase(tripID: String): LiveData<Result<List<DiaryDayModel>>> {
        val userID = authRepository.getUserID()!!

        diaryDayRepository.selectAll(userID, tripID)
            .addSnapshotListener(EventListener { documents, error ->
                if (error != null) {
                    result.value = Result.failure(Throwable("Erro ao obter o diário."))
                    return@EventListener
                }

                val diaryDays: MutableList<DiaryDayModel> = mutableListOf()
                for (document in documents!!) {
                    val diaryDay = DiaryDayModel.convertToDiaryDayModel(document.id, document.data)
                    diaryDays.add(diaryDay)
                }

                result.value = Result.success(diaryDays)
            })

        return result
    }
}
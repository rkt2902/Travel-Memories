package com.ipca.travelmemories.views.expense_all

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.ipca.travelmemories.models.ExpenseModel
import com.ipca.travelmemories.repositories.AuthRepository
import com.ipca.travelmemories.repositories.ExpenseRepository

class ExpenseAllViewModel : ViewModel() {
    private var result: MutableLiveData<Result<List<ExpenseModel>>> = MutableLiveData()

    private var expenseRepository = ExpenseRepository()
    private var authRepository = AuthRepository()

    fun getExpensesFromFirebase(tripID: String): LiveData<Result<List<ExpenseModel>>> {
        val userID = authRepository.getUserID()!!

        expenseRepository.selectAll(userID, tripID)
            .addSnapshotListener(EventListener { documents, error ->
                if (error != null) {
                    result.value = Result.failure(Throwable("Erro ao obter o diário."))
                    return@EventListener
                }

                val expenses: MutableList<ExpenseModel> = mutableListOf()
                for (document in documents!!) {
                    val expense = ExpenseModel.convertToExpenseModel(document.id, document.data)
                    expenses.add(expense)
                }

                result.value = Result.success(expenses)
            })

        return result
    }
}
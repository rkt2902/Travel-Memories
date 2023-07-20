package com.ipca.travelmemories.views.expense_detail

import androidx.lifecycle.ViewModel
import com.ipca.travelmemories.models.ExpenseModel
import com.ipca.travelmemories.repositories.AuthRepository
import com.ipca.travelmemories.repositories.ExpenseRepository
import com.ipca.travelmemories.utils.ParserUtil

class ExpenseDetailViewModel : ViewModel() {
    private var expenseRepository = ExpenseRepository()
    private var authRepository = AuthRepository()

    fun editExpenseFromFirebase(
        tripID: String,
        expenseID: String,
        category: String,
        price: Double,
        description: String,
        date: String,
        callback: (Result<Boolean>) -> Unit
    ) {
        val userID = authRepository.getUserID()!!
        val expense = ExpenseModel(
            expenseID,
            category,
            price,
            description,
            ParserUtil.convertStringToDate(date, "dd-MM-yyyy")
        )

        expenseRepository.update(userID, tripID, expenseID, expense.convertToHashMap())
            .addOnSuccessListener {
                callback(Result.success(true))
            }
            .addOnFailureListener {
                callback(Result.failure(Throwable("Erro ao atualizar dados da despesa.")))
            }
    }

    fun removeExpenseFromFirebase(
        tripID: String,
        expenseID: String,
        callback: (Result<Boolean>) -> Unit
    ) {
        val userID = authRepository.getUserID()!!

        expenseRepository.delete(userID, tripID, expenseID)
            .addOnSuccessListener {
                callback(Result.success(true))
            }
            .addOnFailureListener {
                callback(Result.failure(Throwable("Erro ao apagar despesa.")))
            }
    }
}
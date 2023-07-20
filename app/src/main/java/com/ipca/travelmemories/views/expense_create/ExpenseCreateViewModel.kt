package com.ipca.travelmemories.views.expense_create

import androidx.lifecycle.ViewModel
import com.ipca.travelmemories.models.ExpenseModel
import com.ipca.travelmemories.repositories.AuthRepository
import com.ipca.travelmemories.repositories.ExpenseRepository
import com.ipca.travelmemories.utils.ParserUtil

class ExpenseCreateViewModel : ViewModel() {
    private var expenseRepository = ExpenseRepository()
    private var authRepository = AuthRepository()

    fun addExpensesToFirebase(
        tripID: String,
        category: String,
        price: Double,
        description: String,
        date: String,
        callback: (Result<ExpenseModel>) -> Unit
    ) {
        // criar novo documento no firebase onde será guardada o nova dia do diário
        val userID = authRepository.getUserID()!!
        val documentReference = expenseRepository.setDocumentBeforeCreate(userID, tripID)

        // criar despesa
        val expenseID = documentReference.id
        val expense = ExpenseModel(
            expenseID,
            category,
            price,
            description,
            ParserUtil.convertStringToDate(date, "dd-MM-yyyy")
        )

        // adicionar à base de dados
        expenseRepository.create(documentReference, expense.convertToHashMap())
            .addOnSuccessListener {
                callback(Result.success(expense))
            }
            .addOnFailureListener {
                callback(Result.failure(Throwable("Erro ao adicionar despesa.")))
            }
    }
}
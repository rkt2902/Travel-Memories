package com.ipca.travelmemories.models

import com.google.firebase.Timestamp
import com.ipca.travelmemories.utils.ParserUtil
import java.io.Serializable
import java.util.*

class ExpenseModel : Serializable {
    var id: String? = null
    var category: String? = null
    var price: Double? = null
    var description: String? = null
    var date: Date? = null

    constructor(id: String, category: String?, price: Double?, description: String?, date: Date?) {
        this.id = id
        this.category = category
        this.price = price
        this.description = description
        this.date = date
    }

    // dados que serão armazenados no documento da base de dados pertencente à despesa
    fun convertToHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "category" to category,
            "price" to price,
            "description" to description,
            "date" to Timestamp(date!!)
        )
    }

    companion object {
        // dados que serão recuperados da base de dados através do ID do documento e os dados dentro deste
        fun convertToExpenseModel(id: String, hashMap: MutableMap<String, Any>): ExpenseModel {
            return ExpenseModel(
                id,
                hashMap["category"] as String,
                hashMap["price"] as Double,
                hashMap["description"] as String?,
                ParserUtil.convertTimestampToString(hashMap["date"] as Timestamp),
            )
        }
    }
}
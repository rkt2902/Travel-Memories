package com.ipca.travelmemories.models

import com.google.firebase.Timestamp
import com.ipca.travelmemories.utils.ParserUtil
import java.io.Serializable
import java.util.*

class DiaryDayModel : Serializable {
    var id: String? = null
    var title: String? = null
    var body: String? = null
    var date: Date? = null

    constructor(id: String?, title: String?, body: String?, date: Date?) {
        this.id = id
        this.title = title
        this.body = body
        this.date = date
    }

    // dados que serão armazenados no documento da base de dados pertencente ao dia do diário
    fun convertToHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "title" to title,
            "body" to body,
            "date" to Timestamp(date!!)
        )
    }

    companion object {
        // dados que serão recuperados da base de dados através do ID do documento e os dados dentro deste
        fun convertToDiaryDayModel(id: String, hashMap: MutableMap<String, Any>): DiaryDayModel {
            return DiaryDayModel(
                id,
                hashMap["title"] as String?,
                hashMap["body"] as String,
                ParserUtil.convertTimestampToString(hashMap["date"] as Timestamp),
            )
        }
    }
}
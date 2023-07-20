package com.ipca.travelmemories.models

class UserModel {
    var id: String? = null
    var email: String? = null
    var name: String? = null
    var country: String? = null

    constructor(id: String?, email: String?, name: String?, country: String?) {
        this.id = id
        this.email = email
        this.name = name
        this.country = country
    }

    constructor(name: String?, country: String?) {
        this.name = name
        this.country = country
    }

    // dados que serão armazenados no documento da base de dados pertencente ao utilizador
    fun convertToHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "name" to name,
            "country" to country
        )
    }

    companion object {
        // dados que serão recuperados da base de dados através do ID do documento e os dados dentro deste
        fun convertToUserModel(
            id: String,
            email: String,
            hashMap: MutableMap<String, Any>
        ): UserModel {
            return UserModel(
                id,
                email,
                hashMap["name"] as String,
                hashMap["country"] as String?
            )
        }
    }
}
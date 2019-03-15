package com.death.rxnet

class User {
    var id: Long = 0
    var firstname: String?=null
    var lastname: String?=null
    var isFollowing: Boolean = false

    constructor() {}

    constructor(apiUser: ApiUser) {
        this.id = apiUser.id
        this.firstname = apiUser.firstname
        this.lastname = apiUser.lastname
    }

    override fun toString(): String {
        return "User{" +
                "id=" + id +
                ", firstname='" + firstname + '\''.toString() +
                ", lastname='" + lastname + '\''.toString() +
                ", isFollowing=" + isFollowing +
                '}'.toString()
    }

    override fun hashCode(): Int {
        return id.toInt() + firstname.hashCode() + lastname.hashCode()
    }

    override fun equals(obj: Any?): Boolean {
        if (obj is User) {
            val user = obj as User?

            return (this.id == user!!.id
                    && this.firstname == user.firstname
                    && this.lastname == user.lastname)
        }

        return false
    }
}

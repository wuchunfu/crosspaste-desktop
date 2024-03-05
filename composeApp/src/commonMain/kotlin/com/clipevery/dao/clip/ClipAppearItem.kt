package com.clipevery.dao.clip

import io.realm.kotlin.MutableRealm
import org.mongodb.kbson.ObjectId

interface ClipAppearItem {

    var id: ObjectId

    fun getIdentifiers(): List<String>

    fun getClipType(): Int

    fun getSearchContent(): String?

    var md5: String

    fun update(data: Any, md5: String)

    fun clear(realm: MutableRealm, clearResource: Boolean = true)

    fun getAppearItemId(): ClipAppearItemId {
        return ClipAppearItemId(getClipType(), md5)
    }
}

data class ClipAppearItemId(val clipType: Int, val md5: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClipAppearItemId) return false

        if (clipType != other.clipType) return false
        if (md5 != other.md5) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clipType
        result = 31 * result + md5.hashCode()
        return result
    }
}
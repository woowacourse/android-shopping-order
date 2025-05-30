package woowacourse.shopping.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.util.Base64

object AuthStorage {
    private lateinit var storage: SharedPreferences

    fun init(applicationContext: Context) {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        storage =
            EncryptedSharedPreferences.create(
                FILE_NAME,
                masterKeyAlias,
                applicationContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
    }

    var id: String?
        get() = storage.getString(KEY_ID, DEFAULT_ID)
        set(value) = storage.edit { putString(KEY_ID, value) }

    var pw: String?
        get() = storage.getString(KEY_PW, DEFAULT_PW)
        set(value) = storage.edit { putString(KEY_PW, value) }

    val authorization by lazy {
        val valueToEncode = "$id:$pw".toByteArray()
        "Basic " + Base64.getEncoder().encodeToString(valueToEncode)
    }

    private const val KEY_ID = "woowacourse.shopping.KEY_ID"
    private const val DEFAULT_ID = "jerry8282"
    private const val KEY_PW = "woowacourse.shopping.KEY_PW"
    private const val DEFAULT_PW = "password"
    private const val FILE_NAME = "auth"
}

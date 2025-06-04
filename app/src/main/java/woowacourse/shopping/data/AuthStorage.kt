package woowacourse.shopping.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.util.Base64

class AuthStorage(
    context: Context,
) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val storage: SharedPreferences =
        EncryptedSharedPreferences.create(
            FILE_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

    private var id: String?
        get() = storage.getString(KEY_ID, DEFAULT_ID)
        set(value) = storage.edit { putString(KEY_ID, value) }

    private var pw: String?
        get() = storage.getString(KEY_PW, DEFAULT_PW)
        set(value) = storage.edit { putString(KEY_PW, value) }

    val authorization by lazy {
        val valueToEncode = "$id:$pw".toByteArray()
        "Basic " + Base64.getEncoder().encodeToString(valueToEncode)
    }

    companion object {
        private const val KEY_ID = "woowacourse.shopping.KEY_ID"
        private const val DEFAULT_ID = "jerry8282"
        private const val KEY_PW = "woowacourse.shopping.KEY_PW"
        private const val DEFAULT_PW = "password"
        private const val FILE_NAME = "auth"
    }
}

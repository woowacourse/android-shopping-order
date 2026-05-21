package woowacourse.shopping.data.localdata

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.tink.AeadSerializer
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import java.io.File

class UserDataStore(
    private val context: Context,
) {
    val dataStore: DataStore<UserCredentials> = createEncryptedDataStore()

    private fun createEncryptedDataStore(): DataStore<UserCredentials> {
        AeadConfig.register()

        val keysetHandle =
            AndroidKeysetManager
                .Builder()
                .withSharedPref(context, "keyset", "keyset_prefs")
                .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                .withMasterKeyUri("android-keystore://master_key")
                .build()
                .keysetHandle

        val aeadSerializer =
            AeadSerializer(
                aead = keysetHandle.getPrimitive(RegistryConfiguration.get(), Aead::class.java),
                wrappedSerializer = UserCredentialsSerializer,
                associatedData = "auth_prefs.json".encodeToByteArray(),
            )

        return DataStoreFactory.create(
            serializer = aeadSerializer,
            produceFile = { File(context.filesDir, "datastore/auth_prefs.json") },
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        )
    }

    suspend fun saveUser(
        username: String,
        password: String,
    ) {
        dataStore.updateData { currentData ->
            currentData.copy(username = username, password = password)
        }
    }

    val userCredentialsFlow: Flow<UserCredentials> = dataStore.data
}

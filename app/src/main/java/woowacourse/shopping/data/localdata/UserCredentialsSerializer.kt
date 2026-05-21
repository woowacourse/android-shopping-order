package woowacourse.shopping.data.localdata

import androidx.datastore.core.Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class UserCredentials(
    val username: String = "",
    val password: String = "",
)

object UserCredentialsSerializer : Serializer<UserCredentials> {
    override val defaultValue = UserCredentials()

    override suspend fun readFrom(input: InputStream): UserCredentials =
        try {
            Json.decodeFromString(input.readBytes().decodeToString())
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }

    override suspend fun writeTo(
        t: UserCredentials,
        output: OutputStream,
    ) {
        output.write(Json.encodeToString(t).encodeToByteArray())
    }
}

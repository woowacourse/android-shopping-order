package woowacourse.shopping.data.account

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountLocalDataSourceImpl(
    context: Context,
) : AccountLocalDataSource {
    private val sharedPreferences = context.getSharedPreferences("AccountInfo", Context.MODE_PRIVATE)

    override suspend fun saveBasicKey(basicKey: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                sharedPreferences.edit {
                    putString("basicKey", basicKey)
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun loadBasicKey(): Result<String> =
        withContext(Dispatchers.IO) {
            val basicKey = sharedPreferences.getString("basicKey", "") ?: ""
            if (basicKey.isNotEmpty()) {
                Result.success(basicKey)
            } else {
                Result.failure(IllegalStateException())
            }
        }
}

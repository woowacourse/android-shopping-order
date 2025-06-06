package woowacourse.shopping.data.account

import android.content.Context
import androidx.core.content.edit

class AccountLocalDataSourceImpl(
    context: Context,
) : AccountLocalDataSource {
    private val sharedPreferences = context.getSharedPreferences("AccountInfo", Context.MODE_PRIVATE)

    override fun saveBasicKey(basicKey: String): Result<Unit> {
        try {
            sharedPreferences.edit {
                putString("basicKey", basicKey)
            }
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override fun loadBasicKey(): Result<String> {
        val basicKey = sharedPreferences.getString("basicKey", "") ?: ""
        return if (basicKey.isNotEmpty()) {
            Result.success(basicKey)
        } else {
            Result.failure(IllegalStateException())
        }
    }
}

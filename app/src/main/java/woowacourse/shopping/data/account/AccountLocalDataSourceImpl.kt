package woowacourse.shopping.data.account

import android.content.Context
import androidx.core.content.edit

class AccountLocalDataSourceImpl(
    context: Context,
) : AccountLocalDataSource {
    private val sharedPreferences = context.getSharedPreferences("AccountInfo", Context.MODE_PRIVATE)

    override fun saveBasicKey(
        basicKey: String,
        onComplete: () -> Unit,
        onFail: () -> Unit,
    ) {
        try {
            sharedPreferences.edit {
                putString("basicKey", basicKey)
            }
            onComplete()
        } catch (e: Exception) {
            onFail()
        }
    }

    override fun loadBasicKey(
        onComplete: (basicKey: String) -> Unit,
        onFail: () -> Unit,
    ) {
        try {
            sharedPreferences.getString("basicKey", "")?.let { onComplete(it) }
        } catch (e: Exception) {
            onFail()
        }
    }
}

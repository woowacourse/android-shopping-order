package woowacourse.shopping.ui.common

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import woowacourse.shopping.R
import woowacourse.shopping.util.Event

interface ToastMessageHandler {
    val dataError: LiveData<Event<Unit>>

    fun observeDataError(lifecycleOwner: LifecycleOwner) {
        val context = lifecycleOwner as Context

        dataError.observe(lifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                Toast
                    .makeText(
                        context,
                        context.getString(R.string.fail_data_load_message),
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }
    }
}

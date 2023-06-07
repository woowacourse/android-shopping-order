package woowacourse.shopping.util

import android.app.Activity
import android.util.Log
import androidx.annotation.StringRes
import woowacourse.shopping.R

fun Activity.keyError(key: String) {
    Log.d("hash", "$key is not exist...")
    finish()
}

fun Activity.showToastShort(
    @StringRes
    messageId: Int,
    vararg any: Any
) {
    val message = getString(messageId)
    val formattedMessage = if (any.isNotEmpty()) {
        message.format(*any.map { it.toString() }.toTypedArray())
    } else {
        message
    }
    Toaster.showShort(this, formattedMessage)
}

fun Activity.showToastLong(
    @StringRes
    messageId: Int,
    vararg any: Any
) {
    val message = getString(messageId)
    val formattedMessage = if (any.isNotEmpty()) {
        message.format(*any.map { it.toString() }.toTypedArray())
    } else {
        message
    }
    Toaster.showLong(this, formattedMessage)
}

fun Activity.showToastNetworkError() {
    Toaster.showShort(this, getString(R.string.network_error_message))
}

fun Activity.showToastRetryAgainLater() {
    Toaster.showShort(this, getString(R.string.please_retry_again_later))
}

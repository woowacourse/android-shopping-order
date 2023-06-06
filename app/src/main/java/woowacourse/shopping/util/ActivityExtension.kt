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
    messageId: Int
) {
    Toaster.showShort(this, getString(messageId))
}

fun Activity.showToastLong(
    @StringRes
    messageId: Int
) {
    Toaster.showLong(this, getString(messageId))
}

fun Activity.showToastNetworkError() {
    Toaster.showShort(this, getString(R.string.network_error_message))
}

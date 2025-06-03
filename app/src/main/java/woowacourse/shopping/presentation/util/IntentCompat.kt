package woowacourse.shopping.presentation.util

import android.content.Intent
import android.os.Build.VERSION
import android.os.Build
import android.os.Parcelable

object IntentCompat {
    @Suppress("DEPRECATION")
    inline fun <reified T : Parcelable> getParcelableArrayListExtra(
        intent: Intent,
        key: String,
    ): List<T>? =
        if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(key, T::class.java)
        } else {
            intent.getParcelableArrayListExtra(key)
        }
}

package woowacourse.shopping.presentation.util

import android.content.Intent
import android.os.Build
import android.os.Parcelable

@Suppress("DEPRECATION")
fun <T : Parcelable> Intent.getArrayListExtraCompat(
    key: String,
    clazz: Class<T>,
): ArrayList<T>? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getParcelableArrayListExtra(key, clazz)
    } else {
        this.getParcelableArrayListExtra(key)
    }

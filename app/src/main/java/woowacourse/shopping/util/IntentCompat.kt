package woowacourse.shopping.util

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable

@Suppress("DEPRECATION")
fun <T : Parcelable> Intent.intentParcelableExtra(
    key: String,
    clazz: Class<T>,
): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key, clazz)
    } else {
        getParcelableExtra(key) as? T
    }

inline fun <reified T : Parcelable> Intent.parcelableArray(key: String): Array<T>? =
    when {
        SDK_INT >= 33 -> {
            @Suppress("DEPRECATION")
            getParcelableArrayExtra(key)?.filterIsInstance<T>()?.toTypedArray()
        }

        else -> {
            @Suppress("DEPRECATION")
            getParcelableArrayExtra(key)?.filterIsInstance<T>()?.toTypedArray()
        }
    }

package woowacourse.shopping.ui.utils

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable

inline fun <reified T> Intent.intentParcelable(
    key: String,
    clazz: Class<T>,
): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getParcelableExtra(key, clazz)
    } else {
        this.getParcelableExtra(key)
    }
}

inline fun <reified T> Bundle.bundleParcelable(
    key: String,
    clazz: Class<T>,
): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getParcelable(key, clazz)
    } else {
        this.getParcelable(key)
    }
}

inline fun <reified T : Parcelable> Intent.parcelableList(key: String): List<T>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getParcelableArrayListExtra(key, T::class.java)?.toList()
    } else {
        this.getParcelableArrayListExtra<T>(key)?.toList()
    }
}

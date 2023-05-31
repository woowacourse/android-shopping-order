package woowacourse.shopping.util

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Intent.getParcelableExtraCompat(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key, T::class.java)
    } else {
        getParcelableExtra(key) as? T
    }
}

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Intent.getParcelableListExtraCompat(key: String): List<T>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableArrayListExtra<T>(key)
    } else {
        val array = getParcelableArrayExtra(key)?.filterIsInstance<T>()
        array?.toList()
    }
}
@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Intent.getParcelableArrayExtraCompat(key: String): Array<T>? {
    return getParcelableArrayExtra(key)?.filterIsInstance<T>()?.toTypedArray()
}

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Bundle.getParcelableCompat(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, T::class.java)
    } else {
        getParcelable(key) as? T
    }
}

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Bundle.getParcelableArrayListCompat(key: String): ArrayList<T>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableArrayList(key, T::class.java)
    } else {
        getParcelableArrayList(key)
    }
}

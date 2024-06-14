package woowacourse.shopping.utils

import android.content.Intent
import android.os.Build
import android.os.Parcelable
import retrofit2.Response

inline fun <reified T : Parcelable> Intent.getParcelableExtraCompat(name: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(name, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelableExtra(name) as? T
    }
}

inline fun <reified T : Any> Response<T>.toIdOrNull(): Int? {
    return headers()["LOCATION"]?.substringAfterLast("/")
        ?.toIntOrNull()
}

package woowacourse.shopping.presentation

import android.content.Intent
import android.os.Build
import android.os.Parcelable

inline fun <reified T : Parcelable> Intent.getParcelableArrayListExtraCompat(key: String): ArrayList<T> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableArrayListExtra(key, T::class.java)
            ?: error(ERROR_NO_DATA.format(key))
    } else {
        @Suppress("DEPRECATION")
        getParcelableArrayListExtra<T>(key)
            ?: error(ERROR_NO_DATA.format(key))
    }

const val ERROR_NO_DATA = "%s 데이터를 찾을 수 없습니다."

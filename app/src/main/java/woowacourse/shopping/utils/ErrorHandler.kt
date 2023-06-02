package woowacourse.shopping.utils

import android.util.Log

object ErrorHandler {
    fun Throwable.log() {
        Log.d("Error", message.orEmpty())
    }
}

package woowacourse.shopping.utils

import android.util.Log

object ErrorHandler {
    fun printError(throwable: Throwable) {
        throwable.printStackTrace()
        Log.d("Order Error", "${throwable.message}")
        Log.d("Order Error", throwable.stackTraceToString())
    }
}

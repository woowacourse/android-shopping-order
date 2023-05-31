package woowacourse.shopping.data

import android.util.Log

object HttpErrorHandler {
    fun throwError(httpError: Throwable) {
        Log.d("HttpError", httpError.message.toString())
        throw (httpError)
    }
}

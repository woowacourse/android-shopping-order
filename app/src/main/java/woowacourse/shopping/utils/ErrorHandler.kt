package woowacourse.shopping.utils

import android.util.Log
import woowacourse.shopping.ui.ErrorView

object ErrorHandler {
    fun Throwable.handle(errorView: ErrorView) {
        Log.d("Error", message.orEmpty())
        Log.d("Error", stackTraceToString())
        errorView.showError(message.orEmpty())
    }
}

package woowacourse.shopping.data

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import woowacourse.shopping.R

class HttpErrorHandler(private val context: Context) {
    fun handleHttpError(error: Throwable) {
        if (error.message?.startsWith(INTERNET_CONNECTION_ERROR) == true) {
            Log.d("httpError", error.message.toString())
            showToastMessage(context.getString(R.string.internet_error_message))
            (context as Activity).finish()
        } else {
            Log.d("UnExpectedError", error.message.toString())
            throw (error)
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG)
            .show()
    }

    companion object {
        private const val INTERNET_CONNECTION_ERROR = "Failed to connect"
    }
}

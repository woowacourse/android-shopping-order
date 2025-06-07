package woowacourse.shopping.view

import android.content.Context
import woowacourse.shopping.R
import woowacourse.shopping.domain.exception.NetworkError
import woowacourse.shopping.view.core.ext.showToast

class NetworkExceptionDelegator(
    private val context: Context,
) {
    fun showErrorMessage(error: NetworkError) {
        val resId = asResourceId(error)
        context.showToast(resId)
    }

    private fun asResourceId(error: NetworkError): Int {
        return when (error) {
            NetworkError.HttpError.AuthenticationError -> {
                R.string.http_error_authentication_message
            }
            NetworkError.HttpError.AuthorizationError -> {
                R.string.http_error_authentication_message
            }
            NetworkError.HttpError.BadRequestError -> {
                R.string.http_error_bad_request_message
            }
            NetworkError.HttpError.NotFoundError -> {
                R.string.http_error_not_found_message
            }
            NetworkError.HttpError.ServerError -> {
                R.string.http_error_server_message
            }
            NetworkError.UnknownError -> {
                R.string.error_text_unknown
            }
            NetworkError.MissingLocationHeaderError -> {
                R.string.error_missing_location_header_message
            }
        }
    }
}

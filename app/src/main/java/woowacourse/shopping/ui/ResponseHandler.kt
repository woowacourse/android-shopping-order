package woowacourse.shopping.ui

import woowacourse.shopping.data.common.ApiResponseHandler.onException
import woowacourse.shopping.data.common.ApiResponseHandler.onServerError
import woowacourse.shopping.data.common.ApiResponseHandler.onSuccess
import woowacourse.shopping.data.common.ResponseResult

object ResponseHandler {
    suspend fun <T : Any> handleResponseResult(
        responseResult: ResponseResult<T>,
        onSuccess: (T) -> Unit,
        onError: (String) -> Unit,
    ) {
        responseResult
            .onSuccess { data ->
                onSuccess(data)
            }.onServerError { code, message ->
                onError("$code: $message")
            }.onException { _, message ->
                onError(message)
            }
    }
}

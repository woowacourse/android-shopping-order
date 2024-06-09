package woowacourse.shopping.ui

import androidx.lifecycle.MutableLiveData
import woowacourse.shopping.data.common.ApiResponseHandler.onException
import woowacourse.shopping.data.common.ApiResponseHandler.onServerError
import woowacourse.shopping.data.common.ApiResponseHandler.onSuccess
import woowacourse.shopping.data.common.ResponseResult

object ResponseHandler {
    suspend fun <T: Any> handleResponseResult(
        responseResult: ResponseResult<T>,
        errorMessage: MutableLiveData<String>,
        onSuccess: (T) -> Unit,
    ) {
        responseResult
            .onSuccess { data ->
                onSuccess(data)
            }.onServerError { code, message ->
                errorMessage.value = "$code: $message"
            }.onException { _, message ->
                errorMessage.value = message
            }
    }
}
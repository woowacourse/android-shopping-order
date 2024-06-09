package woowacourse.shopping.ui

import androidx.lifecycle.MutableLiveData
import woowacourse.shopping.data.common.ResponseHandlingUtils.onException
import woowacourse.shopping.data.common.ResponseHandlingUtils.onServerError
import woowacourse.shopping.data.common.ResponseHandlingUtils.onSuccess
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
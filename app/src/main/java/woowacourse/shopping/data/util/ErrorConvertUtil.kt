package woowacourse.shopping.data.util

import com.example.domain.model.BaseResponse
import com.google.gson.Gson
import okhttp3.ResponseBody
import woowacourse.shopping.data.model.WooWaError
import woowacourse.shopping.data.model.dto.response.ErrorDto

internal inline fun <reified T> responseParseCustomError(
    responseBody: ResponseBody?,
    callBack: (BaseResponse<T>) -> Unit
) {
    val errorDto = convert<ErrorDto>(responseBody)
    val wooWaError = WooWaError.findErrorByCode(errorDto?.code)
    callBack(BaseResponse.FAILED(wooWaError.code, wooWaError.message))
}

private inline fun <reified T> convert(responseBody: ResponseBody?): T? {
    return responseBody?.charStream()?.let { errorBodyString ->
        Gson().fromJson(errorBodyString, T::class.java)
    }
}

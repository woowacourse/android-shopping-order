package woowacourse.shopping.data.dto.response

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody

data class ErrorDto(
    @SerializedName("errorCode")
    val errorCode: Int,

    @SerializedName("message")
    val message: String,
) {
    companion object {
        fun mapToErrorDto(errorBody: ResponseBody?): ErrorDto {
            val reader = errorBody?.charStream()
            return Gson().fromJson(reader, ErrorDto::class.java)
        }
    }
}

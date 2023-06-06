package woowacourse.shopping.data.model.dto.response

import com.google.gson.annotations.SerializedName

data class ErrorDto(
    @SerializedName("errorCode")
    val code: Int,
    @SerializedName("message")
    val message: String
)

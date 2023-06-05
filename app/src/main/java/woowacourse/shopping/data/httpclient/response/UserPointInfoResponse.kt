package woowacourse.shopping.data.httpclient.response

import com.google.gson.annotations.SerializedName

data class UserPointInfoResponse(
    @SerializedName("earnRate")
    val earnRate: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("point")
    val point: Int
)

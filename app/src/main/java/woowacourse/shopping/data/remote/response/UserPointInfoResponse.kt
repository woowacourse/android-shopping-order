package woowacourse.shopping.data.remote.response


import com.google.gson.annotations.SerializedName

data class UserPointInfoResponse(
    @SerializedName("earnRate")
    val earnRate: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("point")
    val point: Int
)

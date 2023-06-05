package woowacourse.shopping.data.datasource.response

import com.google.gson.annotations.SerializedName

data class UserEntity(
    @SerializedName("email")
    val email: String,
    @SerializedName("point")
    val point: Int,
    @SerializedName("earnRate")
    val accumulationRate: Int,
)

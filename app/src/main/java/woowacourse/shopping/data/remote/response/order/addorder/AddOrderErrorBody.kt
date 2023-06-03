package woowacourse.shopping.data.remote.response.order.addorder

import com.google.gson.annotations.SerializedName

data class AddOrderErrorBody(
    @SerializedName("errorCode")
    val errorCode: Int,
    @SerializedName("message")
    val message: String
)

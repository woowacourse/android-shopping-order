package woowacourse.shopping.data.httpclient.request

import com.google.gson.annotations.SerializedName

data class AddOrderRequest(
    @SerializedName("cartIds")
    val cartIds: List<Int>,
    @SerializedName("point")
    val point: Int,
    @SerializedName("totalPrice")
    val totalPrice: Int
)

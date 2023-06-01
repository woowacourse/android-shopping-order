package woowacourse.shopping.data.datasource.request

import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("cartIds")
    val basketIds: List<Long>,
    @SerializedName("point")
    val usingPoint: Long,
    @SerializedName("totalPrice")
    val totalPrice: Long,
)

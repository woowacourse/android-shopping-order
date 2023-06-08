package woowacourse.shopping.model.data.dto

import com.google.gson.annotations.SerializedName

data class OrderProductDTO(
    @SerializedName("name")
    val name: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("count")
    val count: Int,
    @SerializedName("price")
    val price: Int
)

package woowacourse.shopping.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("category")
    val category: String,
    @SerializedName("id")
    val productId: Int,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int,
)

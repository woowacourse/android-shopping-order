package woowacourse.shopping.data.remote.dto.product

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("category")
    val category: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int,
)

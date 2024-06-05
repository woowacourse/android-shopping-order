package woowacourse.shopping.data.model.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Long,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("category") val category: String,
)

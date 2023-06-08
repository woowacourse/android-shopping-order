package woowacourse.shopping.model.data.dto

import com.google.gson.annotations.SerializedName

data class ProductDTO(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("imageUrl")
    val imageUrl: String
)

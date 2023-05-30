package woowacourse.shopping.data.dto

import com.example.domain.model.Price
import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,
    @SerializedName("imageUrl")
    val imgUrl: String,

    @SerializedName("price")
    val price: Price,
)

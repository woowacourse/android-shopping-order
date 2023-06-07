package woowacourse.shopping.data.datasource.response

import com.google.gson.annotations.SerializedName

data class ProductEntity(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("imageUrl")
    val imageUrl: String
)

package woowacourse.shopping.data.product

import com.google.gson.annotations.SerializedName

data class ProductDataModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int,
)

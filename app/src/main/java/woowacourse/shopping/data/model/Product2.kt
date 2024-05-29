package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName

data class Product2(
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

package woowacourse.shopping.data.dto.product


import com.google.gson.annotations.SerializedName

data class Content(
    @SerializedName("category")
    val category: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int
)

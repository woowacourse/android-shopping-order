package woowacourse.shopping.data.network.response.carts


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    @SerialName("category")
    val category: String,
    @SerialName("id")
    val id: Int,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int
)

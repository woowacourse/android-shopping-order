package woowacourse.shopping.data.product.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Content(
    @SerialName("category")
    val category: String?,
    @SerialName("id")
    val id: Long?,
    @SerialName("imageUrl")
    val imageUrl: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("price")
    val price: Int?
)
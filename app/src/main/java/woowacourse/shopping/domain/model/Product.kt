package woowacourse.shopping.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("category")
    val category: String?,
)
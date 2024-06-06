package woowacourse.shopping.data.remote.dto.response

import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.CartProduct

@Serializable
data class Product(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
)/*{
    fun toDomain(): CartProduct {
        return CartProduct(
            productId = id.toLong(),
            name = name,
            imgUrl = imageUrl,
            price = price.toLong(),
            quantity = 0,
            category = category,
        )
    }
}
*/

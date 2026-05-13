package woowacourse.shopping.data.network.cart.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.CartContent
import woowacourse.shopping.domain.Money
import woowacourse.shopping.domain.Product

@Serializable
data class Content(
    @SerialName("id")
    val id: Int,
    @SerialName("product")
    val product: ProductDto,
    @SerialName("quantity")
    val quantity: Int,
) {
    fun toDomain(): CartContent {
        return CartContent(
            product = Product(
                name = product.name,
                price = Money(product.price),
                imageUrl = product.imageUrl,
                id = product.id.toString(),
            ),
            quantity = this.quantity,
            id = this.id.toString(),
        )
    }
}

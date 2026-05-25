package woowacourse.shopping.data.network.cart.dto

import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.CartContent
import woowacourse.shopping.domain.Money
import woowacourse.shopping.domain.Product

@Serializable
data class Content(
    val id: Long,
    val product: ProductDto,
    val quantity: Int,
) {
    fun toDomain(): CartContent =
        CartContent(
            product =
                Product(
                    name = product.name,
                    price = Money(product.price),
                    imageUrl = product.imageUrl,
                    id = product.id,
                ),
            quantity = this.quantity,
            id = this.id,
        )
}

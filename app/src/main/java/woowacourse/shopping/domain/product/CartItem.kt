package woowacourse.shopping.domain.product

import java.io.Serializable

data class CartItem(
    val id: Long,
    val productId: Long,
    val name: String,
    val price: Int,
    val quantity: Int = 0,
) : Serializable {
    constructor(id: Long, product: Product, quantity: Int = 0) :
        this(id, product.id, product.name, product.price, quantity)
}

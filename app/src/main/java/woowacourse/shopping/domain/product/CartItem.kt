package woowacourse.shopping.domain.product

import java.io.Serializable

data class CartItem(
    val id: Long,
    private val product: Product,
    val quantity: Int = 0,
) : Serializable {
    val imageUrl: String? = product.imageUrl
    val name = product.name
    val price = product.price
    val productId = product.id

    constructor(
        id: Long,
        productId: Long,
        productName: String,
        productPrice: Int,
        imageUrl: String? = null,
        quantity: Int = 0,
    ) :
        this(id, Product(productId, productName, productPrice, imageUrl), quantity)
}

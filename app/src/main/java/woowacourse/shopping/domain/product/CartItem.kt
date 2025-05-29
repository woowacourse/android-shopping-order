package woowacourse.shopping.domain.product

import java.io.Serializable

data class CartItem(
    val id: Long,
    private val product: Product,
    val quantity: Int = 0,
) : Serializable {
    val imageUrl: String? = product.imageUrl
    val name = product.name
    val productPrice = product.price
    val price get() = productPrice * quantity
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CartItem

        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

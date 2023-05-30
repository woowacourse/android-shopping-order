package woowacourse.shopping.ui.productlist.uistate

import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.product.Product

data class ProductUIState(
    val id: Long,
    val imageUrl: String,
    val name: String,
    val price: Int,
    val cartItemId: Long,
    val quantity: Int = 0,
) {
    companion object {
        fun Product.toUIState(): ProductUIState = ProductUIState(
            id = id,
            imageUrl = imageUrl,
            name = name,
            price = price,
            cartItemId = -1,
            quantity = 0
        )

        fun CartItem.toUIState(): ProductUIState = ProductUIState(
            id = product.id,
            imageUrl = product.imageUrl,
            name = product.name,
            price = product.price,
            cartItemId = id,
            quantity = quantity
        )
    }
}

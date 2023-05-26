package woowacourse.shopping.ui.productlist.uistate

import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Product

data class ProductUIState(
    val id: Long,
    val imageUrl: String,
    val name: String,
    val price: Int,
    val cartItemId: Long,
    val count: Int = 0,
) {
    companion object {
        fun Product.toUIState(): ProductUIState = ProductUIState(
            id = id,
            imageUrl = imageUrl,
            name = name,
            price = price,
            cartItemId = -1,
            count = 0
        )

        fun CartItem.toUIState(): ProductUIState = ProductUIState(
            id = product.id,
            imageUrl = product.imageUrl,
            name = product.name,
            price = product.price,
            cartItemId = id,
            count = count
        )
    }
}

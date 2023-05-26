package woowacourse.shopping.ui.productdetail.uistate

import woowacourse.shopping.domain.Product

data class ProductDetailUIState(
    val id: Long,
    val imageUrl: String,
    val name: String,
    val price: Int,
    val isInCart: Boolean
) {
    companion object {
        fun Product.toUIState(isInCart: Boolean): ProductDetailUIState = ProductDetailUIState(
            id = id, imageUrl = imageUrl, name = name, price = price, isInCart = isInCart
        )
    }
}

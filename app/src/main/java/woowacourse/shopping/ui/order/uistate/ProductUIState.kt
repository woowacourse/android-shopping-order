package woowacourse.shopping.ui.order.uistate

import woowacourse.shopping.domain.Product

data class ProductUIState(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String
) {
    companion object {
        fun from(product: Product): ProductUIState {
            return ProductUIState(
                id = product.id,
                name = product.name,
                price = product.price,
                imageUrl = product.imageUrl
            )
        }
    }
}

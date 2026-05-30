package woowacourse.shopping.ui.shopping

import woowacourse.shopping.domain.model.product.Product

object ShoppingProductUiStateMapper {
    fun toUiStates(
        products: List<Product>,
        quantityByProductId: Map<Long, Int>,
    ): List<ShoppingProductUiState> =
        products.map { product ->
            ShoppingProductUiState(
                product = product,
                quantity = quantityByProductId[product.id] ?: 0,
            )
        }
}

package woowacourse.shopping.ui.common.model

import woowacourse.shopping.model.cart.Cart
import woowacourse.shopping.model.product.Product

object ProductUiModelMapper {
    fun fromProducts(products: List<Product>, cart: Cart): List<ProductUiModel> =
        products.map { product ->
            val cartItem = cart.findByProductId(product.id)
            ProductUiModel(
                product = product,
                cartItemId = cartItem?.id,
                quantity = cartItem?.quantity ?: 0,
            )
        }

    fun syncWithCart(uiModels: List<ProductUiModel>, cart: Cart): List<ProductUiModel> =
        uiModels.map { ui ->
            val cartItem = cart.findByProductId(ui.product.id)
            ui.copy(
                cartItemId = cartItem?.id,
                quantity = cartItem?.quantity ?: 0
            )
        }
}

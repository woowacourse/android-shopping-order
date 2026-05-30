package woowacourse.shopping.ui.cart.list.uistate

import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.repository.query.CartPageItem

object CartItemUiModelMapper {
    fun toUiModels(
        cartItems: List<CartItem>,
        productsById: Map<Long, Product>,
        deselectedProductIds: Set<Long> = emptySet(),
    ): List<CartItemUiModel> =
        cartItems.mapNotNull { cartItem ->
            val product = productsById[cartItem.productId] ?: return@mapNotNull null

            CartItemUiModel(
                cartItemId = cartItem.productId,
                productId = cartItem.productId,
                name = product.name,
                imageUrl = product.imageUrl,
                price = product.price.value,
                quantity = cartItem.quantity,
                isSelected = cartItem.productId !in deselectedProductIds,
            )
        }

    fun toUiModelsFromCartPage(
        cartItems: List<CartPageItem>,
        productsById: Map<Long, Product>,
        deselectedProductIds: Set<Long> = emptySet(),
    ): List<CartItemUiModel> =
        cartItems.mapNotNull { cartItem ->
            val product = productsById[cartItem.productId] ?: return@mapNotNull null

            CartItemUiModel(
                cartItemId = cartItem.cartItemId,
                productId = cartItem.productId,
                name = product.name,
                imageUrl = product.imageUrl,
                price = product.price.value,
                quantity = cartItem.quantity,
                isSelected = cartItem.productId !in deselectedProductIds,
            )
        }
}

package woowacourse.shopping.ui.cart

import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Product
import woowacourse.shopping.repository.cart.CartPageItem

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

package woowacourse.shopping.data.shoppingCart.source

import woowacourse.shopping.data.ProductsHttpClient
import woowacourse.shopping.data.product.entity.CartItemEntity
import woowacourse.shopping.data.shoppingCart.PageableCartItemData
import woowacourse.shopping.data.shoppingCart.dto.CartResponse

class RemoteShoppingCartDataSource(
    private val productsHttpClient: ProductsHttpClient = ProductsHttpClient(),
) : ShoppingCartDataSource {
    override fun pageableCartItems(
        page: Int,
        size: Int,
    ): PageableCartItemData {
        val response = productsHttpClient.getCart(page, size)
        return PageableCartItemData(
            cartItems = response.content?.mapNotNull { it.toCartItemEntityOrNull() } ?: emptyList(),
            hasPrevious = response.hasPrevious,
            hasNext = response.hasNext,
        )
    }

    private fun CartResponse.Content.toCartItemEntityOrNull(): CartItemEntity? =
        if (id == null || product?.id == null || product.name == null || product.price == null || quantity == null) {
            null
        } else {
            CartItemEntity(
                id = id,
                productId = product.id,
                name = product.name,
                price = product.price,
                quantity = quantity,
            )
        }

    override fun addCartItem(
        productId: Long,
        quantity: Int,
    ) {
        productsHttpClient.postCartItem(productId, quantity)
    }

    override fun remove(cartItemId: Long) {
        productsHttpClient.deleteShoppingCartItem(cartItemId)
    }

    override fun updateCartItemQuantity(
        cartItemId: Long,
        newQuantity: Int,
    ) {
        productsHttpClient.patchCartItemQuantity(cartItemId, newQuantity)
    }

    override fun cartItemsSize(): Int {
        val response = productsHttpClient.getCartItemQuantity()
        return response.quantity ?: 0
    }
}

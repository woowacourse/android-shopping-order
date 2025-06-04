package woowacourse.shopping.data.cart.source

import woowacourse.shopping.data.API
import woowacourse.shopping.data.cart.PageableCartItemData
import woowacourse.shopping.data.cart.dto.CartItemQuantityRequest
import woowacourse.shopping.data.cart.dto.CartResponse
import woowacourse.shopping.data.cart.service.CartService
import woowacourse.shopping.data.product.dto.CartItemRequest
import woowacourse.shopping.data.product.entity.CartItemEntity

class RemoteCartDataSource(
    private val cartService: CartService = API.cartService,
) : CartDataSource {
    override fun pageableCartItems(
        page: Int,
        size: Int,
    ): PageableCartItemData {
        val response: CartResponse? = cartService.getCart(page = page, size = size).execute().body()

        return PageableCartItemData.from(
            cartItems =
                response?.content?.mapNotNull { it.toCartItemEntityOrNull() }
                    ?: emptyList(),
            pageNumber = response?.pageable?.pageNumber,
            totalPages = response?.totalPages,
        )
    }

    override fun cart(): List<CartItemEntity> {
        val response: CartResponse? = cartService.getAllCart().execute().body()
        return response?.content?.mapNotNull { it.toCartItemEntityOrNull() } ?: emptyList()
    }

    private fun CartResponse.Content.toCartItemEntityOrNull(): CartItemEntity? =
        if (id == null ||
            product?.id == null ||
            product.name == null ||
            product.price == null ||
            product.category == null ||
            quantity == null
        ) {
            null
        } else {
            CartItemEntity(
                id = id,
                productId = product.id,
                name = product.name,
                price = product.price,
                category = product.category,
                imageUrl = product.imageUrl,
                quantity = quantity,
            )
        }

    override fun addCartItem(
        productId: Long,
        quantity: Int,
    ) {
        cartService.postCartItem(CartItemRequest(productId, quantity)).execute()
    }

    override fun remove(cartItemId: Long) {
        cartService.deleteShoppingCartItem(cartItemId).execute()
    }

    override fun updateCartItemQuantity(
        cartItemId: Long,
        newQuantity: Int,
    ) {
        cartService
            .patchCartItemQuantity(
                cartItemId,
                CartItemQuantityRequest(newQuantity),
            ).execute()
    }

    override fun cartItemsSize(): Int {
        val response = cartService.getCartItemQuantity().execute().body()
        return response?.quantity ?: 0
    }
}

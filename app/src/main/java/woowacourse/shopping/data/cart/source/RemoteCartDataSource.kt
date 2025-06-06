package woowacourse.shopping.data.cart.source

import retrofit2.Response
import woowacourse.shopping.data.API
import woowacourse.shopping.data.cart.PageableCartItemData
import woowacourse.shopping.data.cart.dto.CartItemRequest
import woowacourse.shopping.data.cart.dto.CartResponse
import woowacourse.shopping.data.cart.service.CartService
import woowacourse.shopping.data.product.dto.CartRequest
import woowacourse.shopping.data.product.entity.CartItemEntity

class RemoteCartDataSource(
    private val cartService: CartService = API.cartService,
) : CartDataSource {
    override suspend fun pageableCartItems(
        page: Int,
        size: Int,
    ): PageableCartItemData {
        val response: CartResponse? = cartService.getCart(page = page, size = size)
        return PageableCartItemData(
            cartItems =
                response?.content?.mapNotNull { it.toCartItemEntityOrNull() }
                    ?: emptyList(),
            hasPrevious = response?.hasPrevious ?: false,
            hasNext = response?.hasNext ?: false,
        )
    }

    override suspend fun cart(): List<CartItemEntity> {
        val response: CartResponse? = cartService.getAllCart()
        return response?.content?.mapNotNull { it.toCartItemEntityOrNull() } ?: emptyList()
    }

    private suspend fun CartResponse.Content.toCartItemEntityOrNull(): CartItemEntity? =
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

    override suspend fun addCartItem(
        productId: Long,
        quantity: Int,
    ): Long? {
        val response: Response<Unit> = cartService.postCartItem(CartRequest(productId, quantity))
        val cartItemId: Long? =
            response.headers()["Location"]?.substringAfter("/cart-items/", "")?.toLongOrNull()
        return cartItemId
    }

    override suspend fun remove(cartItemId: Long) {
        cartService.deleteShoppingCartItem(cartItemId)
    }

    override suspend fun updateCartItemQuantity(
        cartItemId: Long,
        newQuantity: Int,
    ) {
        cartService
            .patchCartItemQuantity(
                cartItemId,
                CartItemRequest(newQuantity),
            )
    }

    override suspend fun cartItemsSize(): Int {
        val response = cartService.getCartItemQuantity()
        return response.quantity ?: 0
    }
}

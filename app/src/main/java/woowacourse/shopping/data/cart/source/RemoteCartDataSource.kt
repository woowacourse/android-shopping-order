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
            hasPrevious = response?.hasPrevious == true,
            hasNext = response?.hasNext == true,
        )
    }

    private val CartResponse.hasPrevious: Boolean
        get() = if (pageable?.pageNumber == null) false else pageable.pageNumber > 0

    private val CartResponse.hasNext: Boolean
        get() {
            val pageNumber = pageable?.pageNumber ?: return false
            val totalPages = totalPages ?: return false

            return pageNumber + 1 < totalPages
        }

    override suspend fun cart(): List<CartItemEntity> {
        val response: CartResponse? = cartService.getAllCart()
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

    override suspend fun addCartItem(
        productId: Long,
        quantity: Int,
    ): Long? {
        val response: Response<Unit> = cartService.postCartItem(CartRequest(productId, quantity))
        val cartItemId: Long? =
            response
                .headers()[HEADER_KEY_CART_ITEM_ID]
                ?.substringAfter(CART_ITEM_ID_PREFIX)
                ?.toLongOrNull()
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

    companion object {
        private const val HEADER_KEY_CART_ITEM_ID = "Location"
        private const val CART_ITEM_ID_PREFIX = "/cart-items/"
    }
}

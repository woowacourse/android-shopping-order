package woowacourse.shopping.data.remote.source

import retrofit2.Call
import woowacourse.shopping.data.remote.api.CartApiService
import woowacourse.shopping.data.remote.api.NetworkManager
import woowacourse.shopping.data.remote.dto.cart.CartItemDto
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemResponse
import woowacourse.shopping.data.remote.dto.product.ProductResponse
import woowacourse.shopping.data.source.CartItemDataSource
import woowacourse.shopping.domain.model.CartItem

class CartItemDataSourceImpl(
    private val cartApiService: CartApiService = NetworkManager.cartService(),
): CartItemDataSource {
    override fun loadCartItems(): Call<CartItemResponse> {
        return cartApiService.requestCartItems(
            page = DEFAULT_ITEM_OFFSET,
            size = MAX_CART_ITEM_SIZE,
        )
    }

    override fun loadCartItems(page: Int, size: Int): Call<CartItemResponse> {
        return cartApiService.requestCartItems(
            page = page,
            size = size,
        )
    }

    override fun addCartItem(productId: Int, quantity: Int): Call<Unit> {
        return cartApiService.insertCartItem(
            productId = productId,
            quantity = quantity,
        )
    }

    override fun deleteCartItem(id: Int): Call<Unit> {
        return cartApiService.deleteCartItem(id = id)
    }

    override fun updateCartItem(id: Int, quantity: Int): Call<Unit> {
        return cartApiService.updateCartItem(
            id = id,
            quantity = quantity,
        )
    }

    override fun loadCartItemCount(): Call<CartItemQuantityDto> {
        return cartApiService.requestCartItemCount()
    }

    companion object {
        private const val MAX_CART_ITEM_SIZE = 50
        private const val DEFAULT_ITEM_OFFSET = 0
    }
}

package woowacourse.shopping.data.shoppingCart.datasource

import woowacourse.shopping.data.shoppingCart.remote.dto.CartCountsResponseDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemQuantityRequestDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemRequestDto
import woowacourse.shopping.data.shoppingCart.remote.dto.ShoppingCartItemsResponseDto

interface ShoppingCartRemoteDataSource {
    fun getCartCounts(onCallback: (Result<CartCountsResponseDto?>) -> Unit)

    fun saveCartItem(
        cartItemRequestDto: CartItemRequestDto,
        onCallback: (Result<Unit>) -> Unit,
    )

    fun updateCartItemQuantity(
        shoppingCartId: Long,
        cartItemQuantityRequestDto: CartItemQuantityRequestDto,
        onCallback: (Result<Unit>) -> Unit,
    )

    fun getCartItems(
        page: Int,
        size: Int,
        onCallback: (Result<ShoppingCartItemsResponseDto?>) -> Unit,
    )

    fun deleteCartItem(
        shoppingCartId: Long,
        onCallback: (Result<Unit>) -> Unit,
    )
}

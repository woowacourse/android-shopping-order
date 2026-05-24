package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import woowacourse.shopping.data.datasource.cart.CartRemoteDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.cart.CartItems
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartRemoteDataSource: CartRemoteDataSource,
) : CartRepository {
    private val _cartItems = MutableStateFlow(CartItems())
    override val cartItems: StateFlow<CartItems> = _cartItems.asStateFlow()

    override suspend fun refreshCartItems() {
        _cartItems.update { cartRemoteDataSource.getCartItems(0, 100).toDomain() }
    }

    override suspend fun addProduct(
        productId: Int,
        quantity: Int,
    ) {
        cartRemoteDataSource.addCartItem(productId, quantity)
        refreshCartItems()
    }

    override suspend fun updateQuantity(
        cartId: Int,
        quantity: Int,
    ) {
        cartRemoteDataSource.updateCartItem(cartId, quantity)
        refreshCartItems()
    }

    override suspend fun removeCartItem(cartId: Int) {
        cartRemoteDataSource.deleteCartItem(cartId)
        refreshCartItems()
    }

    override suspend fun order(cartItemIds: List<Int>) {
        cartRemoteDataSource.order(cartItemIds)
    }

    override suspend fun getCartItemsByIds(cartIds: List<Int>): CartItems {
        if (_cartItems.value.values.isEmpty()) {
            refreshCartItems()
        }
        return CartItems(values = _cartItems.value.values.filter { it.id in cartIds })
    }
}

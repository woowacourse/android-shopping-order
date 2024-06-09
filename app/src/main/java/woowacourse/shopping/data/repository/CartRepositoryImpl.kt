package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.RemoteCartDataSource
import woowacourse.shopping.data.model.cart.CartItem
import woowacourse.shopping.data.model.cart.CartItemRequestBody
import woowacourse.shopping.data.model.cart.CartQuantity
import woowacourse.shopping.data.model.cart.toCartData
import woowacourse.shopping.data.model.cart.toCartDomain
import woowacourse.shopping.data.model.cart.toCartItemDomain
import woowacourse.shopping.domain.model.CartData
import woowacourse.shopping.domain.model.CartDomain
import woowacourse.shopping.domain.model.CartItemDomain
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val remoteCartDataSource: RemoteCartDataSource,
) : CartRepository {
    override suspend fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
    ): Result<CartDomain> {
        return runCatching {
            remoteCartDataSource.getCartItems(page, size, sort).toCartDomain()
        }
    }

    override suspend fun getEntireCartData(): Result<List<CartData>> {
        return runCatching {
            val totalCartQuantity = remoteCartDataSource.getCartTotalQuantity().quantity
            remoteCartDataSource.getCartItems(
                PAGE_CART_ITEMS,
                totalCartQuantity,
                SORT_CART_ITEMS,
            ).cartItems.map(CartItem::toCartData)
        }
    }

    override suspend fun getEntireCartItems(): Result<List<CartItemDomain>> {
        return runCatching {
            val totalCartQuantity = remoteCartDataSource.getCartTotalQuantity().quantity
            remoteCartDataSource.getCartItems(
                PAGE_CART_ITEMS,
                totalCartQuantity,
                SORT_CART_ITEMS,
            ).cartItems.map(CartItem::toCartItemDomain)
        }
    }

    override suspend fun getEntireCartItemsForCart(): Result<CartDomain> {
        return runCatching {
            val totalCartQuantity = remoteCartDataSource.getCartTotalQuantity().quantity
            remoteCartDataSource.getCartItems(
                PAGE_CART_ITEMS,
                totalCartQuantity,
                SORT_CART_ITEMS,
            ).toCartDomain()
        }
    }

    override suspend fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Result<Unit> {
        return runCatching {
            val requestBody = CartItemRequestBody(productId, quantity)
            remoteCartDataSource.addCartItem(requestBody)
        }
    }

    override suspend fun deleteCartItem(cartItemId: Int): Result<Unit> {
        return runCatching {
            remoteCartDataSource.deleteCartItem(cartItemId)
        }
    }

    override suspend fun updateCartItem(
        cartItemId: Int,
        quantity: Int,
    ): Result<Unit> {
        return runCatching {
            remoteCartDataSource.updateCartItem(cartItemId, CartQuantity(quantity))
        }
    }

    override suspend fun getCartTotalQuantity(): Result<Int> {
        return runCatching {
            remoteCartDataSource.getCartTotalQuantity().quantity
        }
    }

    companion object {
        private const val PAGE_CART_ITEMS = 0
        private const val SORT_CART_ITEMS = "asc"
    }
}

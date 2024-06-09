package woowacourse.shopping.data.repository

import woowacourse.shopping.domain.model.CartData
import woowacourse.shopping.domain.model.CartDomain
import woowacourse.shopping.domain.model.CartItemDomain
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.util.getFixtureCartItems
import woowacourse.shopping.util.getFixtureProducts

class FakeCartRepository(count: Int = 100) : CartRepository {
    private val products = getFixtureProducts(count)
    private var cartItems: List<CartItemDomain> = getFixtureCartItems(10)

    override suspend fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
    ): Result<CartDomain> {
        return runCatching {
            val fromIndex = page * size
            val toIndex = cartItems.size
            val items = cartItems.subList(fromIndex, toIndex)
            val last = cartItems.getOrNull(toIndex) == null
            CartDomain(
                cartItems = items,
                empty = items.isEmpty(),
                first = true,
                last = last,
                totalPages = 1,
            )
        }
    }

    override suspend fun getEntireCartData(): Result<List<CartData>> {
        return runCatching {
            cartItems.map {
                CartData(
                    cartItemId = it.cartItemId,
                    productId = it.product.id,
                    quantity = it.quantity,
                )
            }
        }
    }

    override suspend fun getEntireCartItems(): Result<List<CartItemDomain>> {
        return runCatching {
            val totalCartQuantity = cartItems.sumOf { it.quantity }
            getCartItems(
                PAGE_CART_ITEMS,
                totalCartQuantity,
                SORT_CART_ITEMS,
            ).getOrNull()?.cartItems ?: emptyList()
        }
    }

    override suspend fun getEntireCartItemsForCart(): Result<CartDomain> {
        return runCatching {
            val totalCartQuantity = cartItems.sumOf { it.quantity }
            getCartItems(
                PAGE_CART_ITEMS,
                totalCartQuantity,
                SORT_CART_ITEMS,
            ).getOrThrow()
        }
    }

    override suspend fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Result<Unit> {
        return runCatching {
            cartItems = cartItems +
                CartItemDomain(
                    cartItemId = cartItems.last().cartItemId + 1,
                    quantity = quantity,
                    product = products.first { it.id == productId },
                )
        }
    }

    override suspend fun deleteCartItem(cartItemId: Int): Result<Unit> {
        return runCatching {
            cartItems = cartItems.filter { it.cartItemId != cartItemId }
        }
    }

    override suspend fun updateCartItem(
        cartItemId: Int,
        quantity: Int,
    ): Result<Unit> {
        return runCatching {
            cartItems =
                cartItems.map {
                    if (it.cartItemId == cartItemId) {
                        it.copy(quantity = quantity)
                    } else {
                        it
                    }
                }
        }
    }

    override suspend fun getCartTotalQuantity(): Result<Int> {
        return runCatching {
            cartItems.sumOf { it.quantity }
        }
    }

    companion object {
        private const val PAGE_CART_ITEMS = 0
        private const val SORT_CART_ITEMS = "asc"
    }
}

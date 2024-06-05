package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Order

interface CartRepository {
    fun fetchCartItemsInfo(resultCallback: (Result<List<CartItem>>) -> Unit)

    fun fetchCartItemsInfoWithPage(
        page: Int,
        pageSize: Int,
        resultCallback: (List<CartItem>) -> Unit,
    )

    fun fetchTotalQuantity(resultCallback: (Result<Int>) -> Unit)

    fun findCartItemWithProductId(productId: Long): CartItem?

    fun fetchItemQuantityWithProductId(productId: Long): Int

    fun fetchCartItem(cartItemId: Long): CartItem

    fun addCartItem(
        productId: Long,
        quantity: Int,
        resultCallback: (Result<Unit>) -> Unit,
    )

    fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
        resultCallback: (Result<Unit>) -> Unit,
    )

    fun updateCartItemQuantityWithProductId(
        productId: Long,
        quantity: Int,
        resultCallback: (Result<Unit>) -> Unit,
    )

    fun deleteCartItem(
        cartItemId: Long,
        resultCallback: (Result<Unit>) -> Unit,
    )

    fun deleteCartItemWithProductId(
        productId: Long,
        resultCallback: (Result<Unit>) -> Unit,
    )

    fun deleteAllItems()

    fun makeOrder(
        order: Order,
        resultCallback: (Result<Unit>) -> Unit,
    )
}

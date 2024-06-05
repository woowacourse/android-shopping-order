package woowacourse.shopping.presentation.ui.shopping

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.ui.testCartItem0

class FakeCartRepositoryImpl() : CartRepository {
    private val cartItems = mutableListOf<CartItem>()
    override fun fetchCartItemsInfo(resultCallback: (Result<List<CartItem>>) -> Unit) {
        resultCallback(Result.success(cartItems.toList()))
    }

    override fun fetchCartItemsInfoWithPage(
        page: Int,
        pageSize: Int,
        resultCallback: (List<CartItem>) -> Unit
    ) {
        resultCallback(cartItems.toList())
    }

    override fun fetchTotalQuantity(resultCallback: (Result<Int>) -> Unit) {
        resultCallback(Result.success(cartItems.size))
    }

    override fun fetchItemQuantityWithProductId(productId: Long): Int {
        return 0
    }

    override fun findCartItemWithProductId(productId: Long): CartItem? {
        return null
    }

    override fun fetchCartItem(cartItemId: Long): CartItem {
        return testCartItem0
    }

    override fun addCartItem(
        productId: Long,
        quantity: Int,
        resultCallback: (Result<Unit>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
        resultCallback: (Result<Unit>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun updateCartItemQuantityWithProductId(
        productId: Long,
        quantity: Int,
        resultCallback: (Result<Unit>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteCartItem(cartItemId: Long, resultCallback: (Result<Unit>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteCartItemWithProductId(
        productId: Long,
        resultCallback: (Result<Unit>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteAllItems() {
        cartItems.clear()
    }

    override fun makeOrder(order: Order, resultCallback: (Result<Unit>) -> Unit) {
        TODO("Not yet implemented")
    }
}

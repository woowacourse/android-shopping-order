package woowacourse.shopping.repository

import woowacourse.shopping.domain.cart.CartItem

interface CartItemRepository {

    fun save(cartItem: CartItem, onFinish: (CartItem) -> Unit)

    fun findAll(onFinish: (List<CartItem>) -> Unit)

    fun findAllByIds(ids: List<Long>, onFinish: (List<CartItem>) -> Unit)

    fun findAllOrderByAddedTime(limit: Int, offset: Int, onFinish: (List<CartItem>) -> Unit)

    fun findById(id: Long, onFinish: (CartItem?) -> Unit)

    fun findByProductId(productId: Long, onFinish: (CartItem?) -> Unit)

    fun countAll(onFinish: (Int) -> Unit)

    fun existByProductId(productId: Long, onFinish: (Boolean) -> Unit)

    fun updateCountById(id: Long, count: Int, onFinish: () -> Unit)

    fun deleteById(id: Long, onFinish: () -> Unit)
}

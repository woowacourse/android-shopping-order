package woowacourse.shopping.data.cart

import woowacourse.shopping.domain.CartItem

interface CartItemDataSource {

    fun save(cartItem: CartItem, onFinish: (CartItem) -> Unit)

    fun findAll(onFinish: (List<CartItem>) -> Unit)

    fun findAll(limit: Int, offset: Int, onFinish: (List<CartItem>) -> Unit)

    fun updateCountById(id: Long, count: Int, onFinish: () -> Unit)

    fun deleteById(id: Long, onFinish: () -> Unit)
}

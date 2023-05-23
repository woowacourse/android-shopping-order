package woowacourse.shopping.data.datasource.cart

import woowacourse.shopping.domain.CartItem

interface CartItemDataSource {

    fun save(cartItem: CartItem, onFinish: (CartItem) -> Unit)

    fun findAll(onFinish: (List<CartItem>) -> Unit)

    fun updateCountById(id: Long, count: Int)

    fun deleteById(id: Long)
}

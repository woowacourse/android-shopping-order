package woowacourse.shopping.data.cart

import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.user.User

interface CartItemDataSource {

    fun save(cartItem: CartItem, user: User, onFinish: (CartItem) -> Unit)

    fun findAll(user: User, onFinish: (List<CartItem>) -> Unit)

    fun findAll(limit: Int, offset: Int, user: User, onFinish: (List<CartItem>) -> Unit)

    fun updateCountById(id: Long, count: Int, user: User, onFinish: () -> Unit)

    fun deleteById(id: Long, user: User, onFinish: () -> Unit)
}

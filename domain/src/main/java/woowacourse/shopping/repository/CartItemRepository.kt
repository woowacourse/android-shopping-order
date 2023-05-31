package woowacourse.shopping.repository

import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.user.User

interface CartItemRepository {

    fun save(cartItem: CartItem, user: User, onFinish: (CartItem) -> Unit)

    fun findAll(user: User, onFinish: (List<CartItem>) -> Unit)

    fun findAllByIds(ids: List<Long>, user: User, onFinish: (List<CartItem>) -> Unit)

    fun findAllOrderByAddedTime(
        limit: Int,
        offset: Int,
        user: User,
        onFinish: (List<CartItem>) -> Unit
    )

    fun findById(id: Long, user: User, onFinish: (CartItem?) -> Unit)

    fun findByProductId(productId: Long, user: User, onFinish: (CartItem?) -> Unit)

    fun countAll(user: User, onFinish: (Int) -> Unit)

    fun existByProductId(productId: Long, user: User, onFinish: (Boolean) -> Unit)

    fun updateCountById(id: Long, count: Int, user: User, onFinish: () -> Unit)

    fun deleteById(id: Long, user: User, onFinish: () -> Unit)
}

package woowacourse.shopping.repository

import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.user.User

interface CartItemRepository {

    fun save(cartItem: CartItem, user: User, onFinish: (Result<CartItem>) -> Unit)

    fun findAll(user: User, onFinish: (Result<List<CartItem>>) -> Unit)

    fun findAllByIds(ids: List<Long>, user: User, onFinish: (Result<List<CartItem>>) -> Unit)

    fun findAllOrderByAddedTime(
        limit: Int,
        offset: Int,
        user: User,
        onFinish: (Result<List<CartItem>>) -> Unit
    )

    fun findById(id: Long, user: User, onFinish: (Result<CartItem>) -> Unit)

    fun findByProductId(productId: Long, user: User, onFinish: (Result<CartItem>) -> Unit)

    fun countAll(user: User, onFinish: (Result<Int>) -> Unit)

    fun existByProductId(productId: Long, user: User, onFinish: (Result<Boolean>) -> Unit)

    fun updateCountById(id: Long, count: Int, user: User, onFinish: (Result<Unit>) -> Unit)

    fun deleteById(id: Long, user: User, onFinish: (Result<Unit>) -> Unit)
}

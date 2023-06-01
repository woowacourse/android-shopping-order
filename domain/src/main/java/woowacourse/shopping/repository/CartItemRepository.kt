package woowacourse.shopping.repository

import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.user.User

interface CartItemRepository {

    fun save(cartItem: CartItem, user: User): Result<CartItem>

    fun findAll(user: User): Result<List<CartItem>>

    fun findAllByIds(ids: List<Long>, user: User): Result<List<CartItem>>

    fun findAllOrderByAddedTime(limit: Int, offset: Int, user: User): Result<List<CartItem>>

    fun findById(id: Long, user: User): Result<CartItem>

    fun findByProductId(productId: Long, user: User): Result<CartItem>

    fun countAll(user: User): Result<Int>

    fun existByProductId(productId: Long, user: User): Result<Boolean>

    fun updateCountById(id: Long, count: Int, user: User): Result<Unit>

    fun deleteById(id: Long, user: User): Result<Unit>
}

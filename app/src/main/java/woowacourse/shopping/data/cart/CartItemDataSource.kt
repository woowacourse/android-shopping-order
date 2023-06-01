package woowacourse.shopping.data.cart

import woowacourse.shopping.data.entity.CartItemEntity
import woowacourse.shopping.domain.user.User

interface CartItemDataSource {

    fun save(productId: Long, user: User, onFinish: (Result<Long>) -> Unit)

    fun findAll(userToken: String, onFinish: (Result<List<CartItemEntity>>) -> Unit)

    fun findAll(limit: Int, offset: Int, userToken: String, onFinish: (Result<List<CartItemEntity>>) -> Unit)

    fun updateCountById(id: Long, count: Int, userToken: String, onFinish: (Result<Unit>) -> Unit)

    fun deleteById(id: Long, userToken: String, onFinish: (Result<Unit>) -> Unit)
}

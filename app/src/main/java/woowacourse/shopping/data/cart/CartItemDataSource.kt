package woowacourse.shopping.data.cart

import woowacourse.shopping.data.entity.CartItemEntity

interface CartItemDataSource {

    fun save(productId: Long, userToken: String): Result<Long>

    fun findAll(userToken: String): Result<List<CartItemEntity>>

    fun findAll(limit: Int, offset: Int, userToken: String): Result<List<CartItemEntity>>

    fun updateCountById(id: Long, count: Int, userToken: String): Result<Unit>

    fun deleteById(id: Long, userToken: String): Result<Unit>
}

package woowacourse.shopping.data.order

import woowacourse.shopping.data.entity.DiscountsEntity
import woowacourse.shopping.data.entity.OrderEntity

interface OrderDataSource {
    fun save(cartItemIds: List<Long>, userToken: String): Result<Long>

    fun findById(id: Long, userToken: String): Result<OrderEntity>

    fun findAll(userToken: String): Result<List<OrderEntity>>

    fun findDiscountPolicy(price: Int, memberGrade: String): Result<DiscountsEntity>
}

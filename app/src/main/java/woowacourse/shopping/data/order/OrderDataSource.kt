package woowacourse.shopping.data.order

import woowacourse.shopping.data.entity.DiscountsEntity
import woowacourse.shopping.data.entity.OrderEntity

interface OrderDataSource {
    fun save(cartItemIds: List<Long>, userToken: String, onFinish: (Result<Long>) -> Unit)

    fun findById(id: Long, userToken: String, onFinish: (Result<OrderEntity>) -> Unit)

    fun findAll(userToken: String, onFinish: (Result<List<OrderEntity>>) -> Unit)

    fun findDiscountPolicy(
        price: Int,
        memberGrade: String,
        onFinish: (Result<DiscountsEntity>) -> Unit
    )
}

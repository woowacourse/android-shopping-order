package woowacourse.shopping.data.order

import woowacourse.shopping.data.entity.DiscountEntity.Companion.toDomain
import woowacourse.shopping.data.entity.OrderEntity.Companion.toDomain
import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.domain.order.Payment
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.repository.OrderRepository

class DefaultOrderRepository(private val orderDataSource: OrderDataSource) : OrderRepository {
    override fun save(cartItemIds: List<Long>, user: User, onFinish: (Result<Long>) -> Unit) {
        orderDataSource.save(cartItemIds, user.token, onFinish)
    }

    override fun findById(id: Long, user: User, onFinish: (Result<Order>) -> Unit) {
        orderDataSource.findById(id, user.token) { result ->
            onFinish(result.mapCatching { it.toDomain() })
        }
    }

    override fun findAll(user: User, onFinish: (Result<List<Order>>) -> Unit) {
        orderDataSource.findAll(user.token) { result ->
            onFinish(result.mapCatching { orders -> orders.map { it.toDomain() } })
        }
    }

    override fun findDiscountPolicy(
        price: Int,
        memberGrade: String,
        onFinish: (Result<Payment>) -> Unit
    ) {
        orderDataSource.findDiscountPolicy(price, memberGrade) { result ->
            onFinish(result.mapCatching { discounts -> Payment(discounts.discountInformation.map { it.toDomain() }) })
        }
    }
}

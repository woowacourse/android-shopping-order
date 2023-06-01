package woowacourse.shopping.data.order

import woowacourse.shopping.data.entity.DiscountEntity.Companion.toDomain
import woowacourse.shopping.data.entity.OrderEntity.Companion.toDomain
import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.domain.order.Payment
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.repository.OrderRepository

class DefaultOrderRepository(private val orderDataSource: OrderDataSource) : OrderRepository {
    override fun save(cartItemIds: List<Long>, user: User): Result<Long> {
        return orderDataSource.save(cartItemIds, user.token)
    }

    override fun findById(id: Long, user: User): Result<Order> {
        return orderDataSource.findById(id, user.token).mapCatching {
            it.toDomain()
        }
    }

    override fun findAll(user: User): Result<List<Order>> {
        return orderDataSource.findAll(user.token).mapCatching { orders ->
            orders.map { it.toDomain() }
        }
    }

    override fun findDiscountPolicy(price: Int, memberGrade: String): Result<Payment> {
        return orderDataSource.findDiscountPolicy(price, memberGrade).mapCatching { discounts ->
            Payment(discounts.discountInformation.map { it.toDomain() })
        }
    }
}

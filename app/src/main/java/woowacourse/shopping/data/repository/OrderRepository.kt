package woowacourse.shopping.data.repository

import woowacourse.shopping.data.database.OrderDatabase
import woowacourse.shopping.data.database.client.ProductClient
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.data.model.dto.CartItemsDto
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.service.RetrofitService

class OrderRepository(private val service: RetrofitService = ProductClient.service) :
    OrderRepository {
    override suspend fun fetchCouponList(): Result<List<Coupon>> {
        val response = service.requestCoupons()
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            val coupons: List<Coupon> = body.map { it.toDomainModel() }
            Result.success(coupons)
        } else {
            Result.failure(RuntimeException("Wrong coupons request. Check network state."))
        }
    }

    override suspend fun makeOrder(paymentAmount: Int): Result<Unit> {
        val order = OrderDatabase.getOrder()
        val cartItemIds = order.map.keys.toList()
        val response = service.makeOrder(CartItemsDto(cartItemIds))
        return if (response.isSuccessful) {
            OrderDatabase.postOrder(Order())
            Result.success(Unit)
        } else {
            Result.failure(RuntimeException("Failed to make order. Check Item Ids. code: ${response.code()}"))
        }
    }
}

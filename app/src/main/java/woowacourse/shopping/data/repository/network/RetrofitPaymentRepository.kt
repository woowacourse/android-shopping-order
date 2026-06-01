package woowacourse.shopping.data.repository.network

import woowacourse.shopping.data.remote.dto.toDomain
import woowacourse.shopping.data.remote.service.PaymentService
import woowacourse.shopping.data.repository.PaymentRepository
import woowacourse.shopping.model.coupon.Coupon

class RetrofitPaymentRepository(
    private val service: PaymentService,
) : PaymentRepository {
    override suspend fun getCoupons(): List<Coupon> {
        val response = service.getCoupons()
        return response.map { it.toDomain() }
    }
}

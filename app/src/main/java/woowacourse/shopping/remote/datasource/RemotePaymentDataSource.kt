package woowacourse.shopping.remote.datasource

import woowacourse.shopping.data.datasource.PaymentDataSource
import woowacourse.shopping.remote.dto.CouponDto
import woowacourse.shopping.remote.service.PaymentService

class RemotePaymentDataSource(private val paymentService: PaymentService) : PaymentDataSource {
    override suspend fun getCoupons(): Result<List<CouponDto>> {
        return runCatching { paymentService.getCoupons() }
    }
}

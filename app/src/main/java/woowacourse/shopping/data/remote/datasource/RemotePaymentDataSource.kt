package woowacourse.shopping.data.remote.datasource

import woowacourse.shopping.data.datasource.PaymentDataSource
import woowacourse.shopping.data.dto.CouponDto
import woowacourse.shopping.data.remote.service.PaymentService

class RemotePaymentDataSource(private val paymentService: PaymentService) : PaymentDataSource {
    override suspend fun getCoupons(): Result<List<CouponDto>> {
        return runCatching { paymentService.getCoupons() }
    }
}

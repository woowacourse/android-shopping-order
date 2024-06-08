package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.CouponDto

interface PaymentDataSource {
    suspend fun getCoupons(): Result<List<CouponDto>>
}

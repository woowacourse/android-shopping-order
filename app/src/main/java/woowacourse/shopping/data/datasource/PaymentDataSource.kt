package woowacourse.shopping.data.datasource

import woowacourse.shopping.remote.dto.CouponDto

interface PaymentDataSource {
    suspend fun getCoupons(): Result<List<CouponDto>>
}

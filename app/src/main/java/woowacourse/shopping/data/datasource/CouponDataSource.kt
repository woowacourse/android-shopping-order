package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.dto.response.CouponDto

interface CouponDataSource {
    suspend fun getCoupons(): List<CouponDto>
}

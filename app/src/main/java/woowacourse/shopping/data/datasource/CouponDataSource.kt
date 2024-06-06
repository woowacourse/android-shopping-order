package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.dto.response.Coupon

interface CouponDataSource {
    suspend fun getCoupons(): List<Coupon>
}

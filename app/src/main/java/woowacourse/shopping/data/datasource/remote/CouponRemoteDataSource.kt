package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.domain.model.coupons.Coupon

interface CouponRemoteDataSource {
    suspend fun getCoupons(): List<Coupon>
}

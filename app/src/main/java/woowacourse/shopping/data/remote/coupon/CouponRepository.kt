package woowacourse.shopping.data.remote.coupon

import woowacourse.shopping.data.remote.NetworkClient

class CouponRepository {
    suspend fun fetchAllCoupons(): List<CouponResponse> = NetworkClient.getCouponService().requestCoupons()
}

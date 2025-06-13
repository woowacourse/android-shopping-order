package woowacourse.shopping.data.coupons.repository

import woowacourse.shopping.data.coupons.CouponRequest

interface OrderRemoteDataSource {
    suspend fun fetchCoupons(): CouponRequest
    suspend fun postOrder(cartItemIds: List<Int>)
}

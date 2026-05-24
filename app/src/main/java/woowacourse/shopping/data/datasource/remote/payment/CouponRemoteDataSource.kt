package woowacourse.shopping.data.datasource.remote.payment

import woowacourse.shopping.data.remote.retrofit.dto.CouponItem

interface CouponRemoteDataSource {
    suspend fun requestCoupons(): List<CouponItem>
}

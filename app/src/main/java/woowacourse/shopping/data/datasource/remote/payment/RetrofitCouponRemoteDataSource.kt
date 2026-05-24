package woowacourse.shopping.data.datasource.remote.payment

import woowacourse.shopping.data.remote.retrofit.api.CouponRetrofitInterface
import woowacourse.shopping.data.remote.retrofit.dto.CouponItem

class RetrofitCouponRemoteDataSource(
    private val apiService: CouponRetrofitInterface,
) : CouponRemoteDataSource {
    override suspend fun requestCoupons(): List<CouponItem> = apiService.requestCoupons()
}

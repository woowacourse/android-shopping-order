package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.response.CouponResponseItem
import woowacourse.shopping.data.service.CouponService
import woowacourse.shopping.data.util.safeApiCall

class CouponDataSourceImpl(
    private val couponService: CouponService,
) : CouponDataSource {
    override suspend fun fetchCoupons(): Result<List<CouponResponseItem>> =
        safeApiCall {
            couponService.fetchCoupons()
        }
}

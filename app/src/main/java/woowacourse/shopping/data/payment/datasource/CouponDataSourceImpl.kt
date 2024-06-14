package woowacourse.shopping.data.payment.datasource

import woowacourse.shopping.data.payment.model.CouponData
import woowacourse.shopping.data.payment.toData
import woowacourse.shopping.data.util.handleResponse
import woowacourse.shopping.remote.service.CouponService

class CouponDataSourceImpl(
    private val couponService: CouponService,
) : CouponDataSource {
    override suspend fun loadCoupons(): Result<List<CouponData>> {
        val response = couponService.loadCoupons()
        val coupon = response.body()?.map { it.toData() } ?: throw Exception("Empty body")
        return handleResponse(response, coupon)
    }
}

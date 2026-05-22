package woowacourse.shopping.repository.http.repository

import woowacourse.shopping.model.coupon.Coupon
import woowacourse.shopping.repository.CouponRepository
import woowacourse.shopping.repository.http.api.CouponApiService
import woowacourse.shopping.repository.http.dto.coupon.toCoupon

class HttpCouponRepository(
    private val apiService: CouponApiService,
) : CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> =
        runCatching {
            val response = apiService.getCoupons()
            if (response.isSuccessful) {
                response.body()?.map { it.toCoupon() } ?: emptyList()
            } else {
                throw Exception("쿠폰을 불러오는 데 실패했습니다.")
            }
        }
}

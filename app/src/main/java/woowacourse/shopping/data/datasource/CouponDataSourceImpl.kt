package woowacourse.shopping.data.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import woowacourse.shopping.data.dto.order.Coupons
import woowacourse.shopping.data.service.CouponService

class CouponDataSourceImpl(
    private val couponService: CouponService,
) : CouponDataSource {
    override suspend fun fetchCoupons(): Coupons =
        withContext(Dispatchers.IO) {
            val response: Response<Coupons> =
                couponService.requestCoupons()

            if (response.isSuccessful) {
                response.body() ?: Coupons()
            } else {
                throw Exception("서버 응답 실패 : ${response.code()}")
            }
        }
}

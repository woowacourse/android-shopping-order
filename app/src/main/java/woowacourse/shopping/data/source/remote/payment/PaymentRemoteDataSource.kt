package woowacourse.shopping.data.source.remote.payment

import woowacourse.shopping.data.model.CouponResponse
import woowacourse.shopping.data.source.remote.api.CouponApiService

class PaymentRemoteDataSource(
    private val api: CouponApiService,
) : PaymentDataSource {
    override suspend fun getCoupons(): Result<List<CouponResponse>> =
        runCatching {
            val response = api.getCoupons()

            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                throw Exception(GET_COUPONS_ERROR_MESSAGE)
            }
        }

    companion object {
        private const val GET_COUPONS_ERROR_MESSAGE = "[ERROR] 쿠폰을 불러오는 데 실패했습니다"
    }
}

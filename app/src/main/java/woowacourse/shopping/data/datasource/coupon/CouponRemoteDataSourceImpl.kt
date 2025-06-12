package woowacourse.shopping.data.datasource.coupon

import retrofit2.HttpException
import woowacourse.shopping.data.api.CouponApi
import woowacourse.shopping.data.model.response.CouponsResponse

class CouponRemoteDataSourceImpl(
    private val api: CouponApi,
) : CouponRemoteDataSource {
    override suspend fun fetchAllCoupons(): CouponsResponse {
        val response = api.getAllCoupons()
        val body = response.body() ?: throw IllegalStateException()

        return if (response.isSuccessful) body else throw HttpException(response)
    }
}

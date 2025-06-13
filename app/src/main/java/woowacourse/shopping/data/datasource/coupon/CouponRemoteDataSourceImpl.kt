package woowacourse.shopping.data.datasource.coupon

import woowacourse.shopping.data.api.CouponApi
import woowacourse.shopping.data.di.ApiResult
import woowacourse.shopping.data.model.response.CouponsResponse

class CouponRemoteDataSourceImpl(
    private val api: CouponApi,
) : CouponRemoteDataSource {
    override suspend fun fetchAllCoupons(): ApiResult<CouponsResponse> =
        try {
            val response = api.getAllCoupons()
            when {
                response.isSuccessful -> {
                    response.body()?.let { ApiResult.Success(it) } ?: ApiResult.UnknownError
                }

                response.code() in 400..499 -> {
                    ApiResult.ClientError(response.code(), response.message())
                }

                response.code() >= 500 -> {
                    ApiResult.ServerError(response.code(), response.message())
                }

                else -> ApiResult.UnknownError
            }
        } catch (e: Exception) {
            ApiResult.NetworkError(e)
        }
}

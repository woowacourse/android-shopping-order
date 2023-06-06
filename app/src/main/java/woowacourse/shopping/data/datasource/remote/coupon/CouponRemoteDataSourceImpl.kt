package woowacourse.shopping.data.datasource.remote.coupon

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.data.remote.request.CouponDTO
import woowacourse.shopping.data.remote.request.CouponDiscountPriceDTO
import woowacourse.shopping.utils.CustomException
class CouponRemoteDataSourceImpl :
    CouponRemoteDataSource {

    override fun getCoupons(callback: (Result<List<CouponDTO>>) -> Unit) {
        RetrofitClient.getInstance().couponDataService.getCoupons()
            .enqueue(object : Callback<List<CouponDTO>> {
                override fun onResponse(
                    call: Call<List<CouponDTO>>,
                    response: Response<List<CouponDTO>>,
                ) {
                    if (response.isSuccessful) {
                        callback(
                            Result.success(
                                response.body() ?: throw IllegalArgumentException(),
                            ),
                        )
                    } else {
                        callback(Result.failure(CustomException.ServerException(SERVER_ERROR_MESSAGE)))
                    }
                }

                override fun onFailure(call: Call<List<CouponDTO>>, t: Throwable) {
                    throw t
                }
            })
    }

    override fun getPriceWithCoupon(
        originalPrice: Int,
        couponId: Long,
        callback: (Result<CouponDiscountPriceDTO>) -> Unit,
    ) {
        RetrofitClient.getInstance().couponDataService.getCouponDiscount(originalPrice, couponId)
            .enqueue(object : Callback<CouponDiscountPriceDTO> {
                override fun onResponse(
                    call: Call<CouponDiscountPriceDTO>,
                    response: Response<CouponDiscountPriceDTO>,
                ) {
                    if (response.isSuccessful) {
                        callback(
                            Result.success(
                                response.body() ?: throw IllegalArgumentException(),
                            ),
                        )
                    } else {
                        callback(Result.failure(CustomException.ServerException(SERVER_ERROR_MESSAGE)))
                    }
                }

                override fun onFailure(call: Call<CouponDiscountPriceDTO>, t: Throwable) {
                    throw t
                }
            })
    }

    companion object {
        private const val SERVER_ERROR_MESSAGE = "서버와의 통신이 원활하지 않습니다."
    }
}

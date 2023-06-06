package woowacourse.shopping.data.datasource.remote.coupon

import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.data.remote.request.CouponDTO
import woowacourse.shopping.data.remote.request.CouponDiscountPriceDTO
import java.util.concurrent.Executors

class CouponRemoteDataSourceImpl :
    CouponRemoteDataSource {

    override fun getCoupons(): Result<List<CouponDTO>> {
        val executor = Executors.newSingleThreadExecutor()
        val result = executor.submit<Result<List<CouponDTO>>> {
            val response = RetrofitClient.getInstance().couponDataService.getCoupons().execute()
            if (response.isSuccessful) {
                Result.success(response.body() ?: throw IllegalArgumentException())
            } else {
                Result.failure(Throwable(response.message()))
            }
        }.get()
        executor.shutdown()
        return result
    }

    override fun getPriceWithCoupon(
        originalPrice: Int,
        couponId: Long,
    ): Result<CouponDiscountPriceDTO> {
        val executor = Executors.newSingleThreadExecutor()
        val result = executor.submit<Result<CouponDiscountPriceDTO>> {
            val response =
                RetrofitClient.getInstance().couponDataService.getCouponDiscount(
                    originalPrice,
                    couponId,
                )
                    .execute()
            if (response.isSuccessful) {
                Result.success(response.body() ?: throw IllegalArgumentException())
            } else {
                Result.failure(Throwable(response.message()))
            }
        }.get()
        executor.shutdown()
        return result
    }
}

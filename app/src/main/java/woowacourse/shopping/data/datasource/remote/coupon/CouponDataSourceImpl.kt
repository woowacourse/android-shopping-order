package woowacourse.shopping.data.datasource.remote.coupon

import woowacourse.shopping.data.datasource.local.AuthInfoDataSource
import woowacourse.shopping.data.datasource.remote.retrofit.ServicePool
import woowacourse.shopping.data.remote.request.CouponDTO
import woowacourse.shopping.data.remote.request.CouponDiscountPriceDTO
import java.util.concurrent.Executors

class CouponDataSourceImpl(private val authInfoDataSource: AuthInfoDataSource) : CouponDataSource {

    private val token = authInfoDataSource.getAuthInfo() ?: throw IllegalArgumentException()

    override fun getCoupons(): Result<List<CouponDTO>> {
        val executor = Executors.newSingleThreadExecutor()
        val result = executor.submit<Result<List<CouponDTO>>> {
            val response = ServicePool.couponDataService.getCoupons(token).execute()
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
                ServicePool.couponDataService.getCouponDiscount(token, originalPrice, couponId)
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

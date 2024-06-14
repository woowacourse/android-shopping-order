package woowacourse.shopping.remote.source

import woowacourse.shopping.data.model.CouponData
import woowacourse.shopping.data.source.CouponDataSource
import woowacourse.shopping.remote.model.response.toData
import woowacourse.shopping.remote.service.CouponApiService

class CouponRemoteDataSource(
    private val couponApiService: CouponApiService,
) : CouponDataSource {
    override suspend fun couponsData(): Result<List<CouponData>> =
        runCatching {
            couponApiService.coupons().map { it.toData() }
        }

    override suspend fun coupon(couponId: Long): Result<CouponData> {
        return runCatching {
            couponApiService.coupons().find {
                it.id == couponId
            }?.toData() ?: throw NoSuchElementException("Coupon not found")
        }
    }

    companion object {
        private const val TAG = "CouponRemoteDataSource"
    }
}

package woowacourse.shopping.data.repository

import com.example.domain.model.Coupon
import com.example.domain.model.CouponDiscountPrice
import com.example.domain.repository.CouponRepository
import woowacourse.shopping.data.datasource.remote.coupon.CouponRemoteDataSource
import woowacourse.shopping.mapper.toDomain

class CouponRepositoryImpl(private val couponRemoteDataSource: CouponRemoteDataSource) : CouponRepository {
    override fun getCoupons(): Result<List<Coupon>> {
        val result = couponRemoteDataSource.getCoupons()
        return if (result.isSuccess) {
            val couponDomain = result.getOrNull()?.map { it.toDomain() }
            Result.success(couponDomain ?: emptyList())
        } else {
            Result.failure(Throwable(result.exceptionOrNull()?.message))
        }
    }

    override fun getPriceWithCoupon(
        originalPrice: Int,
        couponId: Long,
    ): Result<CouponDiscountPrice> {
        val result = couponRemoteDataSource.getPriceWithCoupon(originalPrice, couponId)
        return if (result.isSuccess) {
            Result.success(result.getOrNull()?.toDomain() ?: throw IllegalArgumentException())
        } else {
            Result.failure(Throwable(result.exceptionOrNull()?.message))
        }
    }
}

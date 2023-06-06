package woowacourse.shopping.data.repository

import com.example.domain.model.Coupon
import com.example.domain.model.CouponDiscountPrice
import com.example.domain.repository.CouponRepository
import woowacourse.shopping.data.datasource.remote.coupon.CouponRemoteDataSource
import woowacourse.shopping.mapper.toDomain

class CouponRepositoryImpl(private val couponRemoteDataSource: CouponRemoteDataSource) :
    CouponRepository {
    override fun getCoupons(callback: (List<Coupon>) -> Unit) {
        couponRemoteDataSource.getCoupons(
            callback = { result ->
                if (result.isSuccess) {
                    val couponsDomain = result.getOrThrow().map { it.toDomain() }
                    callback(couponsDomain)
                } else {
                    throw IllegalArgumentException()
                }
            },
        )
    }

    override fun getPriceWithCoupon(
        originalPrice: Int,
        couponId: Long,
        callback: (CouponDiscountPrice) -> Unit,
    ) {
        couponRemoteDataSource.getPriceWithCoupon(originalPrice, couponId) { result ->
            if (result.isSuccess) {
                val couponDiscountPrice = result.getOrThrow().toDomain()
                callback(couponDiscountPrice)
            } else {
                throw IllegalArgumentException()
            }
        }
    }
}

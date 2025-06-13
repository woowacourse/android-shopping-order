package woowacourse.shopping.data.repository

import woowacourse.shopping.domain.Coupon
import woowacourse.shopping.data.source.CouponDataSource
import woowacourse.shopping.data.source.OrderDataSource
import java.time.LocalDate

class PayRepositoryImpl(
    private val orderDataSource: OrderDataSource,
    private val couponDataSource: CouponDataSource,
) : PayRepository {
    override suspend fun getCoupons(): List<Coupon> {
        return couponDataSource.getCoupons().map { coupon ->
            Coupon(
                id = coupon.id,
                code = coupon.code,
                description = coupon.description,
                expirationDate = LocalDate.parse(coupon.expirationDate),
                discountType = coupon.discountType,
                discount = coupon.discount,
                minimumAmount = coupon.minimumAmount,
            )
        }
    }

    companion object {
        private var instance: PayRepositoryImpl? = null

        @Synchronized
        fun initialize(
            orderDataSource: OrderDataSource,
            couponDataSource: CouponDataSource,
        ): PayRepositoryImpl =
            instance ?: PayRepositoryImpl(
                orderDataSource,
                couponDataSource,
            ).also { instance = it }
    }
}

package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CouponDataSource
import woowacourse.shopping.data.datasource.impl.RemoteCouponDataSource
import woowacourse.shopping.data.remote.dto.response.BuyXGetYCoupon
import woowacourse.shopping.data.remote.dto.response.FixedDiscountCoupon
import woowacourse.shopping.data.remote.dto.response.FreeShippingCoupon
import woowacourse.shopping.data.remote.dto.response.PercentageDiscountCoupon
import woowacourse.shopping.domain.model.coupon.Buy2Free1
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.Discount5000
import woowacourse.shopping.domain.model.coupon.FreeShipping
import woowacourse.shopping.domain.model.coupon.MiracleCoupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(private val dataSource: CouponDataSource = RemoteCouponDataSource()) :
    CouponRepository {
    override suspend fun getCoupons(): List<Coupon> = dataSource.getCoupons().map { it.toDomain() }

    private fun woowacourse.shopping.data.remote.dto.response.Coupon.toDomain(): Coupon =
        when (this) {
            is BuyXGetYCoupon ->
                Buy2Free1(
                    id,
                    code,
                    description,
                    expirationDate,
                    discountType,
                    buyQuantity,
                    getQuantity,
                )

            is FixedDiscountCoupon ->
                Discount5000(
                    id,
                    description,
                    expirationDate,
                    discountType,
                    discount,
                    minimumAmount,
                    code,
                )

            is FreeShippingCoupon ->
                FreeShipping(
                    id,
                    code,
                    description,
                    expirationDate,
                    minimumAmount,
                    discountType,
                )

            is PercentageDiscountCoupon ->
                MiracleCoupon(
                    id,
                    code,
                    description,
                    expirationDate,
                    discount,
                    discountType,
                    availableTime.start,
                    availableTime.end,
                )
        }
}

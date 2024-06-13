package woowacourse.shopping.view.order.model

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.coupon.BogoDiscountStrategy
import woowacourse.shopping.domain.model.coupon.FixedDiscountStrategy
import woowacourse.shopping.domain.model.coupon.FreeShippingStrategy
import woowacourse.shopping.domain.model.coupon.TimeBasedDiscountStrategy

object CouponUiModelMapper {
    private const val SHIPPING_COST = 3000

    fun Coupon.toUiModel(): CouponUiModel {
        val discountStrategy =
            when {
                this.discount != null && this.availableTime == null -> FixedDiscountStrategy(this.discount)
                this.buyQuantity != null && this.getQuantity != null -> BogoDiscountStrategy(this.buyQuantity, this.getQuantity)
                this.discount == null && this.minimumAmount != null -> FreeShippingStrategy(SHIPPING_COST)
                this.discount != null && this.availableTime != null -> {
                    val startTime = this.availableTime.start
                    val endTime = this.availableTime.end
                    TimeBasedDiscountStrategy(this.discount, startTime, endTime)
                }
                else -> throw IllegalArgumentException("Invalid discount type")
            }

        return CouponUiModel(
            id = this.id,
            code = this.code,
            description = this.description,
            expirationDate = this.expirationDate,
            discountType = this.discountType,
            minimumAmount = this.minimumAmount,
            discount = this.discount,
            buyQuantity = this.buyQuantity,
            getQuantity = this.getQuantity,
            availableTimeStart = this.availableTime?.start,
            availableTimeEnd = this.availableTime?.end,
            discountStrategy = discountStrategy,
        )
    }

    fun CouponUiModel.toDomainModel(): Coupon {
        return Coupon(
            id = this.id,
            code = this.code,
            description = this.description,
            expirationDate = this.expirationDate,
            discountType = this.discountType,
            minimumAmount = this.minimumAmount,
            discount = this.discount,
            buyQuantity = this.buyQuantity,
            getQuantity = this.getQuantity,
            availableTime =
                this.availableTimeStart?.let {
                    Coupon.AvailableTime(
                        start = this.availableTimeStart,
                        end = this.availableTimeEnd!!,
                    )
                },
            discountStrategy = this.discountStrategy,
        )
    }
}

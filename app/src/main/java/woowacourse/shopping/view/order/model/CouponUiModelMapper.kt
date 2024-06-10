package woowacourse.shopping.view.order.model

import woowacourse.shopping.domain.model.Coupon

object CouponUiModelMapper {
    fun Coupon.FixedDiscountCoupon.toUiModel(): CouponUiModel.FixedDiscountCouponUiModel {
        return CouponUiModel.FixedDiscountCouponUiModel(
            id = this.id,
            code = this.code,
            description = this.description,
            expirationDate = this.expirationDate,
            discountType = this.discountType,
            minimumAmount = this.minimumAmount,
            discount = this.discount,
            isSelected = false,
        )
    }

    fun Coupon.BogoCoupon.toUiModel(): CouponUiModel.BogoCouponUiModel {
        return CouponUiModel.BogoCouponUiModel(
            id = this.id,
            code = this.code,
            description = this.description,
            expirationDate = this.expirationDate,
            discountType = this.discountType,
            buyQuantity = this.buyQuantity,
            getQuantity = this.getQuantity,
            isSelected = false,
        )
    }

    fun Coupon.FreeShippingCoupon.toUiModel(): CouponUiModel.FreeShippingCouponUiModel {
        return CouponUiModel.FreeShippingCouponUiModel(
            id = this.id,
            code = this.code,
            description = this.description,
            expirationDate = this.expirationDate,
            discountType = this.discountType,
            minimumAmount = this.minimumAmount,
            isSelected = false,
        )
    }

    fun Coupon.TimeBasedDiscountCoupon.toUiModel(): CouponUiModel.TimeBasedDiscountCouponUiModel {
        return CouponUiModel.TimeBasedDiscountCouponUiModel(
            id = this.id,
            code = this.code,
            description = this.description,
            expirationDate = this.expirationDate,
            discountType = this.discountType,
            discount = this.discount,
            availableTimeStart = this.availableTimeStart,
            availableTimeEnd = this.availableTimeEnd,
            isSelected = false,
        )
    }
}

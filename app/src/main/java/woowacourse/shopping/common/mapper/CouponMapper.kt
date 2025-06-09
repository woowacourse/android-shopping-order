package woowacourse.shopping.common.mapper

import woowacourse.shopping.data.dto.coupon.CouponResponse
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponBase
import woowacourse.shopping.domain.model.coupon.CouponBuyXGetY
import woowacourse.shopping.domain.model.coupon.CouponDiscountType
import woowacourse.shopping.domain.model.coupon.CouponFixedDiscount
import woowacourse.shopping.domain.model.coupon.CouponFreeShipping
import woowacourse.shopping.domain.model.coupon.CouponPercentDiscount
import woowacourse.shopping.presentation.uimodel.CouponUiModel
import java.time.LocalDate
import java.time.LocalTime

private const val ERROR_REQUIRED_FIELD_NULL = "%s는 null 일 수 없습니다."

fun CouponResponse.toDomain(): Coupon {
    val couponType = CouponDiscountType.from(this.discountType)
    val couponBase = CouponBase(id, code, description, LocalDate.parse(expirationDate))
    return when (couponType) {
        CouponDiscountType.FIXED ->
            CouponFixedDiscount(
                couponBase,
                requireNotNull(discount) { ERROR_REQUIRED_FIELD_NULL.format("discount") },
                requireNotNull(minimumAmount) { ERROR_REQUIRED_FIELD_NULL.format("minimumAmount") },
                couponType,
            )

        CouponDiscountType.BUY_X_GET_Y ->
            CouponBuyXGetY(
                couponBase,
                requireNotNull(buyQuantity) { ERROR_REQUIRED_FIELD_NULL.format("buyQuantity") },
                requireNotNull(getQuantity) { ERROR_REQUIRED_FIELD_NULL.format("getQuantity") },
                couponType,
            )

        CouponDiscountType.FREE_SHIPPING ->
            CouponFreeShipping(
                couponBase,
                requireNotNull(minimumAmount) { ERROR_REQUIRED_FIELD_NULL.format("minimumAmount") },
                couponType,
            )

        CouponDiscountType.PERCENTAGE -> {
            val availableTime =
                requireNotNull(availableTime) { ERROR_REQUIRED_FIELD_NULL.format("availableTime") }
            CouponPercentDiscount(
                couponBase,
                requireNotNull(discount) { ERROR_REQUIRED_FIELD_NULL.format("discount") },
                LocalTime.parse(availableTime.start),
                LocalTime.parse(availableTime.end),
                couponType,
            )
        }

        CouponDiscountType.NOT_FOUND -> throw NoSuchElementException("해당 할인 종류를 찾을 수 없습니다.")
    }
}

fun Coupon.toUiModel(): CouponUiModel =
    when (this) {
        is CouponFixedDiscount ->
            CouponUiModel(
                couponBase.id,
                couponBase.description,
                couponBase.expirationDate,
                100_000,
                null,
                null,
                false,
            )

        is CouponBuyXGetY ->
            CouponUiModel(
                couponBase.id,
                couponBase.description,
                couponBase.expirationDate,
                null,
                null,
                null,
                false,
            )

        is CouponFreeShipping ->
            CouponUiModel(
                couponBase.id,
                couponBase.description,
                couponBase.expirationDate,
                50_000,
                null,
                null,
                false,
            )

        is CouponPercentDiscount ->
            CouponUiModel(
                couponBase.id,
                couponBase.description,
                couponBase.expirationDate,
                null,
                availableStartTime,
                availableEndTime,
                false,
            )
    }

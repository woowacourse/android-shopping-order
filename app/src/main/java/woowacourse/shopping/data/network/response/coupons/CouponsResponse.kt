package woowacourse.shopping.data.network.response.coupons

import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.coupon.BogoCoupon
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.FixedCoupon
import woowacourse.shopping.domain.coupon.FreeshippingCoupon
import woowacourse.shopping.domain.coupon.MiracleSaleCoupon
import java.time.LocalDate

@Serializable
data class CouponsResponse(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discountType: String,
    val availableTime: AvailableTime? = null,
    val buyQuantity: Int? = null,
    val discount: Int? = null,
    val getQuantity: Int? = null,
    val minimumAmount: Int? = null,
) {
    fun toDomain(): Coupon {
        return when (code) {
            "FIXED5000" -> {
                requireNotNull(discount) { ERROR_FIXED_DISCOUNT_NULL }
                requireNotNull(minimumAmount) { ERROR_FIXED_MINIMUM_NULL }

                FixedCoupon(
                    id = id.toInt(),
                    code = code,
                    description = description,
                    discountType = discountType,
                    expirationDate = LocalDate.parse(expirationDate),
                    minimumAmount = minimumAmount,
                    discount = discount,
                )
            }

            "FREESHIPPING" -> {
                requireNotNull(minimumAmount) { ERROR_FREESHIP_MINIMUM_NULL }

                FreeshippingCoupon(
                    id = id.toInt(),
                    code = code,
                    description = description,
                    discountType = discountType,
                    expirationDate = LocalDate.parse(expirationDate),
                    minimumAmount = minimumAmount,
                )
            }

            "BOGO" -> {
                requireNotNull(buyQuantity) { ERROR_BOGO_BUY_QUANTITY_NULL }
                requireNotNull(getQuantity) { ERROR_BOGO_GET_QUANTITY_NULL }

                BogoCoupon(
                    id = id.toInt(),
                    code = code,
                    description = description,
                    discountType = discountType,
                    buyQuantity = buyQuantity,
                    expirationDate = LocalDate.parse(expirationDate),
                    getQuantity = getQuantity,
                )
            }

            "MIRACLESALE" -> {
                requireNotNull(discount) { ERROR_FIXED_DISCOUNT_NULL }
                val time = requireNotNull(availableTime) { ERROR_MIRACLESALE_AVAILABLE_TIME_NULL }

                MiracleSaleCoupon(
                    id = id.toInt(),
                    code = code,
                    description = description,
                    discountType = discountType,
                    expirationDate = LocalDate.parse(expirationDate),
                    discount = discount,
                    availableTime = time.toDomain(),
                )
            }

            else -> throw IllegalArgumentException(ERROR_INVALID_CODE + code)
        }
    }

    companion object {
        private const val ERROR_INVALID_CODE = "존재하지 않는 쿠폰 코드입니다."

        private const val ERROR_FIXED_DISCOUNT_NULL = "할인 금액이 존재하지 않습니다."
        private const val ERROR_FIXED_MINIMUM_NULL = "최소 주문 금액이 존재하지 않습니다."

        private const val ERROR_FREESHIP_MINIMUM_NULL = "최소 주문 금액이 필요합니다."

        private const val ERROR_BOGO_BUY_QUANTITY_NULL = "필수 구매 수량이 필요합니다."
        private const val ERROR_BOGO_GET_QUANTITY_NULL = "무료 제공 수량이 필요합니다."

        private const val ERROR_MIRACLESALE_AVAILABLE_TIME_NULL = "사용 가능 시간이이 필요합니다."
    }
}

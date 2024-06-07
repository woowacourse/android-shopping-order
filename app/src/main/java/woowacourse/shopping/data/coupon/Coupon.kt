package woowacourse.shopping.data.coupon

import woowacourse.shopping.data.dto.response.ResponseCouponDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class Coupon(
    val id: Long,
    val code: CouponCode,
    val description: String,
    val expirationDate: LocalDate,
    val discountType: String,
    val discount: Int = 0,
    val minimumAmount: Int = 0,
    val buyQuantity: Int = 0,
    val getQuantity: Int = 0,
    val availableTime: AvailableTime? = null,
    private val orderAmount: Int = 0,
    private val currentDateTime: LocalDateTime = LocalDateTime.now(),
) {
    fun couponCondition(): String =
        when (code) {
            CouponCode.FIXED5000 -> code.condition.format(minimumAmount)
            CouponCode.BOGO -> code.condition
            CouponCode.FREESHIPPING -> code.condition.format(minimumAmount)
            CouponCode.MIRACLESALE ->
                code.condition.format(
                    availableTime?.start?.hour,
                    availableTime?.end?.hour,
                )
        }

    fun isValid(): Boolean =
        when (code) {
            CouponCode.FIXED5000 -> isValidFixed5000()
            CouponCode.BOGO -> isValidBogo()
            CouponCode.FREESHIPPING -> isValidFreeShipping()
            CouponCode.MIRACLESALE -> isValidMiracleSale()
        }

    private fun isValidFixed5000(): Boolean = orderAmount >= minimumAmount && currentDateTime.toLocalDate() <= expirationDate

    private fun isValidBogo(): Boolean = currentDateTime.toLocalDate() <= expirationDate

    private fun isValidFreeShipping(): Boolean = orderAmount >= minimumAmount && currentDateTime.toLocalDate() <= expirationDate

    private fun isValidMiracleSale(): Boolean =
        currentDateTime.toLocalDate() <= expirationDate && availableTime?.isAvailableTime(
            currentDateTime.toLocalTime(),
        ) ?: false
}

fun ResponseCouponDto.toCoupon(): Coupon {
    return Coupon(
        id = this.id,
        code = CouponCode.findCode(this.code),
        description = this.description,
        expirationDate = this.expirationDate.toLocalDate(),
        discountType = this.discountType,
        discount = this.discount,
        minimumAmount = this.minimumAmount,
        buyQuantity = this.buyQuantity,
        getQuantity = this.getQuantity,
        availableTime = this.availableTime?.toModel(),
    )
}

fun String.toLocalTime(): LocalTime = LocalTime.parse(this, DateTimeFormatter.ofPattern("HH:mm:ss"))

fun String.toLocalDate(): LocalDate = LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

package woowacourse.shopping.data.coupon

import woowacourse.shopping.data.dto.response.ResponseCouponDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

abstract class CouponState {
    abstract val coupon: Coupon

    abstract fun condition(): String

    abstract fun isValid(): Boolean

    abstract fun discountAmount(): Int
}

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
)

fun ResponseCouponDto.toDomain(): CouponState =
    when (CouponCode.findCode(this.code)) {
        CouponCode.FIXED5000 -> Fixed5000(coupon = this.toCoupon())
        CouponCode.BOGO -> Bogo(coupon = this.toCoupon())
        CouponCode.FREESHIPPING -> Freeshipping(coupon = this.toCoupon())
        CouponCode.MIRACLESALE -> MiracleSale(coupon = this.toCoupon())
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

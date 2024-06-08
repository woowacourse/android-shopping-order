package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartWithProduct
import java.time.LocalDate
import java.time.LocalTime

data class MiracleCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val discount: Int,
    val availableStartTime: LocalTime,
    val availableEndTime: LocalTime,
) : Coupon(DiscountType.Percentage) {
    override fun canUse(products: List<CartWithProduct>): Boolean {
        val currentTime = LocalTime.now()
        val isAvailableTime =
            currentTime.isAfter(availableStartTime) && currentTime.isBefore(availableEndTime)
        val isNotExpired = LocalDate.now().isBefore(expirationDate)
        return isAvailableTime && isNotExpired
    }

    override fun discountPrice(products: List<CartWithProduct>): Int {
        if (!canUse(products)) error("$description 쿠폰을 적용할 수 없습니다.")
        val totalPrice = products.sumOf { it.product.price * it.quantity.value }
        return totalPrice * (1 - discount / 100.0).toInt()
    }
}

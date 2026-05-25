package woowacourse.shopping.data.model

import java.time.LocalDate
import java.time.LocalTime

sealed class Coupon(
    val id: Long,
    val title: String,
    val code: String,
    val expiryDate: LocalDate,
) {
    abstract fun discount(
        items: List<CartItem>,
        shippingFee: Long,
        now: LocalTime = LocalTime.now(),
    ): Long

    fun isExpired(today: LocalDate = LocalDate.now()): Boolean = today.isAfter(expiryDate)

    class FixedAmount(
        id: Long,
        title: String,
        code: String,
        expiryDate: LocalDate,
        private val amount: Long,
        private val minOrderAmount: Long,
    ) : Coupon(id, title, code, expiryDate) {
        override fun discount(
            items: List<CartItem>,
            shippingFee: Long,
            now: LocalTime,
        ): Long {
            val orderAmount = items.sumOf { it.totalPrice.value }
            return if (orderAmount >= minOrderAmount) amount.coerceAtMost(orderAmount + shippingFee) else 0
        }
    }

    class BuyOneGetOne(
        id: Long,
        title: String,
        code: String,
        expiryDate: LocalDate,
        private val buyQuantity: Int,
        private val freeQuantity: Int,
    ) : Coupon(id, title, code, expiryDate) {
        override fun discount(
            items: List<CartItem>,
            shippingFee: Long,
            now: LocalTime,
        ): Long =
            items
                .filter { it.quantity >= buyQuantity + freeQuantity }
                .maxOfOrNull { it.product.price.value * freeQuantity }
                ?: 0
    }

    class FreeShipping(
        id: Long,
        title: String,
        code: String,
        expiryDate: LocalDate,
        private val minOrderAmount: Long,
    ) : Coupon(id, title, code, expiryDate) {
        override fun discount(
            items: List<CartItem>,
            shippingFee: Long,
            now: LocalTime,
        ): Long {
            val orderAmount = items.sumOf { it.totalPrice.value }
            return if (orderAmount >= minOrderAmount) shippingFee else 0
        }
    }

    class TimeRate(
        id: Long,
        title: String,
        code: String,
        expiryDate: LocalDate,
        private val rate: Double,
        private val startTime: LocalTime,
        private val endTime: LocalTime,
    ) : Coupon(id, title, code, expiryDate) {
        override fun discount(
            items: List<CartItem>,
            shippingFee: Long,
            now: LocalTime,
        ): Long {
            val orderAmount = items.sumOf { it.totalPrice.value }
            val inTimeRange = !now.isBefore(startTime) && now.isBefore(endTime)
            return if (inTimeRange) (orderAmount * rate).toLong() else 0
        }
    }

    companion object {
        fun defaults(): List<Coupon> =
            listOf(
                FixedAmount(
                    id = 1,
                    title = "5,000원 할인 쿠폰",
                    code = "FIXED5000",
                    amount = 5_000,
                    minOrderAmount = 100_000,
                    expiryDate = LocalDate.of(2024, 11, 30),
                ),
                BuyOneGetOne(
                    id = 2,
                    title = "2개 구매 시 1개 무료 쿠폰",
                    code = "BOGO",
                    buyQuantity = 2,
                    freeQuantity = 1,
                    expiryDate = LocalDate.of(2024, 5, 30),
                ),
                FreeShipping(
                    id = 3,
                    title = "5만원 이상 구매 시 무료 배송 쿠폰",
                    code = "FREESHIPPING",
                    minOrderAmount = 50_000,
                    expiryDate = LocalDate.of(2024, 8, 31),
                ),
                TimeRate(
                    id = 4,
                    title = "미라클모닝 30% 할인 쿠폰",
                    code = "MIRACLESALE",
                    rate = 0.3,
                    startTime = LocalTime.of(4, 0),
                    endTime = LocalTime.of(7, 0),
                    expiryDate = LocalDate.of(2024, 7, 31),
                ),
            )
    }
}

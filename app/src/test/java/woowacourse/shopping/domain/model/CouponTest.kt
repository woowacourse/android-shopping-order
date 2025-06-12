package woowacourse.shopping.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime

class CouponTest {
    private val product =
        Product(
            id = 1,
            category = "category",
            name = "name",
            imageUrl = "url",
            price = Price(1000),
        )
    private val cartItem =
        CartItem(
            cartId = 1,
            product = product,
            amount = 5,
        )
    private val cartItems = listOf(cartItem)

    @Test
    fun `FixedAmountCoupon - 최소 금액 이상이면 사용 가능하고 할인 적용`() {
        val coupon =
            Coupon.FixedAmountCoupon(
                id = 1L,
                code = "FIX5000",
                description = "1000원 할인",
                expirationDate = LocalDate.now().plusDays(1),
                discount = 1000,
                minimumAmount = 5000,
            )

        assertTrue(coupon.isAvailable(cartItems))
        assertEquals(1000, coupon.calculateDiscountAmount(cartItems))
    }

    @Test
    fun `FixedAmountCoupon - 최소 금액 미만이면 사용 불가하고 할인 없음`() {
        val coupon =
            Coupon.FixedAmountCoupon(
                id = 1L,
                code = "FIX6000",
                description = "1000원 할인",
                expirationDate = LocalDate.now().plusDays(1),
                discount = 1000,
                minimumAmount = 6000,
            )

        assertFalse(coupon.isAvailable(cartItems))
        assertEquals(0, coupon.calculateDiscountAmount(cartItems))
    }

    @Test
    fun `BuyXGetYCoupon - 조건 만족하면 무료 상품 수만큼 할인 적용`() {
        val coupon =
            Coupon.BuyXGetYCoupon(
                id = 2L,
                code = "BUY2GET1",
                description = "2개 사면 1개 무료",
                expirationDate = LocalDate.now().plusDays(1),
                buyQuantity = 2,
                getQuantity = 1,
            )

        assertTrue(coupon.isAvailable(cartItems))
        assertEquals(1000, coupon.calculateDiscountAmount(cartItems))
    }

    @Test
    fun `BuyXGetYCoupon - 조건 불만족 시 사용 불가, 할인 없음`() {
        val coupon =
            Coupon.BuyXGetYCoupon(
                id = 2L,
                code = "BUY5GET1",
                description = "5개 사면 1개 무료",
                expirationDate = LocalDate.now().plusDays(1),
                buyQuantity = 5,
                getQuantity = 1,
            )

        assertFalse(coupon.isAvailable(cartItems))
        assertEquals(0, coupon.calculateDiscountAmount(cartItems))
    }

    @Test
    fun `FreeShippingCoupon - 최소 금액 이상이면 사용 가능, 할인은 항상 0`() {
        val coupon =
            Coupon.FreeShippingCoupon(
                id = 3L,
                code = "FREESHIPPING",
                description = "무료배송",
                expirationDate = LocalDate.now().plusDays(1),
                minimumAmount = 3000,
            )

        assertTrue(coupon.isAvailable(cartItems))
        assertEquals(0, coupon.calculateDiscountAmount(cartItems))
    }

    @Test
    fun `FreeShippingCoupon - 최소 금액 미만이면 사용 불가`() {
        val coupon =
            Coupon.FreeShippingCoupon(
                id = 3L,
                code = "FREESHIPPING",
                description = "무료배송",
                expirationDate = LocalDate.now().plusDays(1),
                minimumAmount = 6000,
            )

        assertFalse(coupon.isAvailable(cartItems))
        assertEquals(0, coupon.calculateDiscountAmount(cartItems))
    }

    @Test
    fun `PercentageCoupon - 현재 시간이 사용 가능 시간 내라면 할인 적용`() {
        val now = LocalTime.now()
        val coupon =
            Coupon.PercentageCoupon(
                id = 4L,
                code = "PERCENT10",
                description = "10% 할인",
                expirationDate = LocalDate.now().plusDays(1),
                discountPercent = 10,
                availableTime = TimeRange(now.minusMinutes(1), now.plusMinutes(1)),
            )

        assertTrue(coupon.isAvailable(cartItems))
        assertEquals(500, coupon.calculateDiscountAmount(cartItems))
    }

    @Test
    fun `PercentageCoupon - 현재 시간이 사용 가능 시간 밖이면 사용 불가`() {
        val now = LocalTime.now()
        val coupon =
            Coupon.PercentageCoupon(
                id = 4L,
                code = "PERCENT10",
                description = "10% 할인",
                expirationDate = LocalDate.now().plusDays(1),
                discountPercent = 10,
                availableTime = TimeRange(now.minusHours(2), now.minusHours(1)),
            )

        assertFalse(coupon.isAvailable(cartItems))
        assertEquals(500, coupon.calculateDiscountAmount(cartItems))
    }
}

package woowacourse.shopping.domain.coupon

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.Payment
import woowacourse.shopping.domain.cart.ShoppingCarts
import woowacourse.shopping.fixture.shoppingCartFixtures
import java.time.LocalDate
import java.time.LocalTime

class CouponApplierFactoryTest {
    private lateinit var factory: CouponApplierFactory
    private val nextDay = LocalDate.now().plusDays(1)

    @BeforeEach
    fun setUp() {
        factory = CouponApplierFactory()
    }

    @Test
    fun `BOGO 쿠폰은 조건에 맞는 가장 비싼 상품의 가격만큼 할인된다`() {
        // given
        val coupon =
            BogoCoupon(
                id = 1,
                code = "BOGO",
                description = "2개 구매 시 1개 무료 쿠폰",
                expirationDate = nextDay,
                buyQuantity = 2,
                getQuantity = 1,
                discountType = "BOGO",
            )
        val originPayment =
            Payment(
                totalPayment = 40_000,
                originPayment = 0,
                deliveryFee = 3_000,
                couponDiscount = 0,
            )

        val mostExpensiveProductPrice =
            ShoppingCarts(shoppingCartFixtures)
                .mostExpensiveCartPriceWithStandardQuantity(3)

        // when
        val appliedPayment = factory.apply(originPayment, ShoppingCarts(shoppingCartFixtures), coupon)

        // then
        assertEquals(-mostExpensiveProductPrice, appliedPayment.couponDiscount)
        assertEquals(
            originPayment.totalPayment - mostExpensiveProductPrice,
            appliedPayment.totalPayment,
        )
    }

    @Test
    fun `Fixed 쿠폰은 고정 금액만큼 할인된다`() {
        // given
        val coupon =
            FixedCoupon(
                id = 2,
                code = "FIXED5000",
                description = "5,000원 할인 쿠폰",
                expirationDate = LocalDate.parse("2099-11-30"),
                discount = 5_000,
                minimumAmount = 100_000,
                discountType = "fixed",
            )
        val originPayment =
            Payment(
                totalPayment = 120_000,
                originPayment = 0,
                deliveryFee = 3_000,
                couponDiscount = 0,
            )

        // when
        val appliedPayment = factory.apply(originPayment, ShoppingCarts(shoppingCartFixtures), coupon)

        // then
        assertEquals(-coupon.discount, appliedPayment.couponDiscount)
        assertEquals(originPayment.totalPayment - coupon.discount, appliedPayment.totalPayment)
    }

    @Test
    fun `FreeShipping 쿠폰은 배송비만큼 할인된다`() {
        // given
        val coupon =
            FreeshippingCoupon(
                id = 3,
                code = "FREESHIPPING",
                description = "5만원 이상 구매 시 무료 배송 쿠폰",
                expirationDate = LocalDate.parse("2099-08-31"),
                minimumAmount = 50_000,
                discountType = "freeShipping",
            )
        val originPayment =
            Payment(
                totalPayment = 70_000,
                originPayment = 0,
                deliveryFee = 3_000,
                couponDiscount = 0,
            )

        // when
        val appliedPayment = factory.apply(originPayment, ShoppingCarts(shoppingCartFixtures), coupon)

        // then
        assertEquals(-originPayment.deliveryFee, appliedPayment.couponDiscount)
        assertEquals(
            originPayment.totalPayment - originPayment.deliveryFee,
            appliedPayment.totalPayment,
        )
    }

    @Test
    fun `MiracleSale 쿠폰은 설정된 할인률만큼 할인된다`() {
        // given
        val coupon =
            MiracleSaleCoupon(
                id = 4,
                code = "MIRACLESALE",
                description = "미라클모닝 30% 할인 쿠폰",
                expirationDate = LocalDate.parse("2099-07-31"),
                discount = 30,
                discountType = "percentage",
                availableTime =
                    MiracleSaleCoupon.AvailableTime(
                        LocalTime.parse("00:00"),
                        LocalTime.parse("00:00"),
                    ),
            )
        val originPayment =
            Payment(
                totalPayment = 100_000,
                originPayment = 0,
                deliveryFee = 3_000,
                couponDiscount = 0,
            )
        val expectedDiscount = originPayment.totalPayment * coupon.discount / 100

        // when
        val appliedPayment =
            factory.apply(originPayment, ShoppingCarts(shoppingCartFixtures), coupon)

        // then
        assertEquals(expectedDiscount, appliedPayment.couponDiscount)
        assertEquals(originPayment.totalPayment - expectedDiscount, appliedPayment.totalPayment)
    }
}

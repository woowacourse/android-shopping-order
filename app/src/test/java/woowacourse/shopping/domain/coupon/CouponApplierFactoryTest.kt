package woowacourse.shopping.domain.coupon

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.Payment
import woowacourse.shopping.fixture.shoppingCartFixtures
import java.time.LocalDate
import java.time.LocalTime

class CouponApplierFactoryTest {
    private lateinit var factory: CouponApplierFactory

    @BeforeEach
    fun setUp() {
        factory = CouponApplierFactory()
    }

    @Test
    fun `쿠폰에 명시된 수량만큼 구매한 상품 중 가장 비싼 상품의 가격이 할인된다`() {
        // given
        val coupon =
            BogoCoupon(
                id = 1,
                code = "BOGO",
                description = "2개 구매 시 1개 무료 쿠폰",
                expirationDate = LocalDate.parse("2099-06-30"),
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
            shoppingCartFixtures
                .filter { it.quantityValue == coupon.standardQuantity }
                .maxBy { it.product.priceValue }
                .product
                .priceValue

        // when
        val appliedPayment =
            factory.bogoCouponApplier.apply(
                origin = originPayment,
                order = shoppingCartFixtures,
                coupon = coupon,
            )

        // then
        assertEquals(mostExpensiveProductPrice, 20_000)
        assertEquals(appliedPayment.couponDiscount, -mostExpensiveProductPrice)
        assertEquals(appliedPayment.totalPayment, originPayment.totalPayment - mostExpensiveProductPrice)
    }

    @Test
    fun `구매 가격에서 5천원이 할인된다`() {
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
        val appliedPayment =
            factory.fixedCouponApplier.apply(
                origin = originPayment,
                coupon = coupon,
            )

        // then
        assertEquals(appliedPayment.couponDiscount, -coupon.discount)
        assertEquals(appliedPayment.totalPayment, originPayment.totalPayment - coupon.discount)
    }

    @Test
    fun `배송비가 할인된다`() {
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
        val appliedPayment =
            factory.freeShippingCouponApplier.apply(
                origin = originPayment,
            )

        // then
        assertEquals(appliedPayment.couponDiscount, -originPayment.deliveryFee)
        assertEquals(appliedPayment.totalPayment, originPayment.totalPayment - originPayment.deliveryFee)
    }

    @Test
    fun `miracle sale coupon에 명시된 할인률 만큼 할인된다`() {
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
            factory.miracleSaleCouponApplier.apply(
                origin = originPayment,
                coupon = coupon,
            )

        // then
        assertEquals(appliedPayment.couponDiscount, expectedDiscount)
        assertEquals(appliedPayment.totalPayment, originPayment.totalPayment - expectedDiscount)
    }
}

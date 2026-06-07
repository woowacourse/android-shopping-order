package woowacourse.shopping.domain.model.coupon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.domain.model.cart.CartItems
import woowacourse.shopping.domain.model.cart.Quantity
import woowacourse.shopping.domain.model.product.Category
import woowacourse.shopping.domain.model.product.ImageUrl
import woowacourse.shopping.domain.model.product.Price
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.product.ProductName
import java.time.LocalDate
import java.time.LocalDateTime

class CouponCalculatorTest {
    private fun makeCart(vararg entries: Triple<Int, Int, Int>): CartItems {
        val items =
            entries.map { (id, price, qty) ->
                CartItem(
                    id = id,
                    product =
                        Product(
                            id = id,
                            imageUrl = ImageUrl(""),
                            name = ProductName("product$id"),
                            price = Price(price),
                            category = Category("cat"),
                        ),
                    quantity = Quantity(qty),
                )
            }
        return CartItems(items)
    }

    @Test
    fun `FIXED5000_적용_케이스`() {
        val cart = makeCart(Triple(1, 40000, 1), Triple(2, 80000, 1)) // subtotal 120_000
        val coupon =
            Coupon(
                code = "FIXED5000",
                type = CouponTypes.FIXED5000,
                amount = 5_000,
                minOrderAmount = 100_000,
                expireAt = LocalDate.of(2026, 12, 31),
            )

        val result = CouponCalculator.apply(coupon, cart, now = LocalDateTime.of(2024, 5, 1, 12, 0))

        assertThat(result.applied).isTrue()
        assertThat(result.discount).isEqualTo(5_000)
        assertThat(result.shippingFee).isEqualTo(3_000)
    }

    @Test
    fun `FIXED5000_미적용_케이스`() {
        val cart = makeCart(Triple(1, 50_000, 1), Triple(2, 40_000, 0)) // subtotal 50_000
        val coupon =
            Coupon(
                code = "FIXED5000",
                type = CouponTypes.FIXED5000,
                amount = 5_000,
                minOrderAmount = 100_000,
                expireAt = LocalDate.of(2026, 12, 31),
            )

        val result = CouponCalculator.apply(coupon, cart)

        assertThat(result.applied).isFalse()
    }

    @Test
    fun `BOGO_단일상품_3개_할인`() {
        val cart = makeCart(Triple(1, 10_000, 4)) // 3+1 번들 1회 성립
        val coupon =
            Coupon(
                code = "3+1",
                type = CouponTypes.BOGO,
                buyQuantity = 3,
                getQuantity = 1,
                expireAt = LocalDate.of(2026, 12, 31),
            )

        val result = CouponCalculator.apply(coupon, cart)

        assertThat(result.applied).isTrue()
        assertThat(result.discount).isEqualTo(10_000)
    }

    @Test
    fun `BOGO_5plus3_조건을_만족하는_경우`() {
        val cart = makeCart(Triple(1, 10_000, 8)) // 5+3 번들 1회 성립
        val coupon =
            Coupon(
                code = "5+3",
                type = CouponTypes.BOGO,
                buyQuantity = 5,
                getQuantity = 3,
                expireAt = LocalDate.of(2026, 12, 31),
            )

        val result = CouponCalculator.apply(coupon, cart)

        assertThat(result.applied).isTrue()
        assertThat(result.discount).isEqualTo(30_000)
    }

    @Test
    fun `BOGO_5plus3_수량_5개는_미적용`() {
        val cart = makeCart(Triple(1, 10_000, 5)) // 5개만으로는 5+3 미성립
        val coupon =
            Coupon(
                code = "5+3",
                type = CouponTypes.BOGO,
                buyQuantity = 5,
                getQuantity = 3,
                expireAt = LocalDate.of(2026, 12, 31),
            )

        val result = CouponCalculator.apply(coupon, cart)

        assertThat(result.applied).isFalse()
        assertThat(result.discount).isEqualTo(0)
    }

    @Test
    fun `BOGO_여러상품_중_가장_단가_높은_상품에_적용`() {
        val cart = makeCart(Triple(1, 10_000, 4), Triple(2, 8_000, 4))
        val coupon =
            Coupon(
                code = "3+1",
                type = CouponTypes.BOGO,
                buyQuantity = 3,
                getQuantity = 1,
                expireAt = LocalDate.of(2026, 12, 31),
            )

        val result = CouponCalculator.apply(coupon, cart)

        assertThat(result.applied).isTrue()
        assertThat(result.discount).isEqualTo(10_000) // 1개 무료
    }

    @Test
    fun `FREESHIPPING_적용`() {
        val cart = makeCart(Triple(1, 30_000, 1), Triple(2, 25_000, 1)) // subtotal 55_000
        val coupon =
            Coupon(
                code = "FREESHIPPING",
                type = CouponTypes.FREESHIPPING,
                minOrderAmount = 50_000,
                expireAt = LocalDate.of(2026, 12, 31),
            )

        val result = CouponCalculator.apply(coupon, cart)

        assertThat(result.applied).isTrue()
        assertThat(result.shippingFee).isEqualTo(0)
    }

    @Test
    fun `MIRACLESALE_오전_적용`() {
        val cart = makeCart(Triple(1, 80_000, 1)) // subtotal 80_000
        val coupon =
            Coupon(
                code = "MIRACLESALE",
                type = CouponTypes.MIRACLESALE,
                rate = 0.3,
                expireAt = LocalDate.of(2026, 12, 31),
            )

        val result = CouponCalculator.apply(coupon, cart, now = LocalDateTime.of(2024, 5, 1, 5, 0))

        assertThat(result.applied).isTrue()
        assertThat(result.discount).isEqualTo((80_000 * 0.3).toInt())
    }

    @Test
    fun `PERCENT_시간제한_없으면_항상_적용`() {
        val cart = makeCart(Triple(1, 100_000, 1))
        val coupon =
            Coupon(
                code = "PERCENT10",
                type = CouponTypes.MIRACLESALE,
                rate = 0.1,
                expireAt = LocalDate.of(2026, 12, 31),
            )

        val result = CouponCalculator.apply(coupon, cart, now = LocalDateTime.of(2024, 5, 1, 22, 0))

        assertThat(result.applied).isTrue()
        assertThat(result.discount).isEqualTo(10_000)
    }
}

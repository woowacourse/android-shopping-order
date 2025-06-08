package woowacourse.shopping.domain.product

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import java.time.LocalDate
import java.time.LocalDateTime

class FreeShippingCouponTest {
    private lateinit var freeShippingCoupon: FreeShippingCoupon

    @BeforeEach
    fun setUp() {
        freeShippingCoupon = FreeShippingCoupon(
            couponId = 3L,
            expirationDate = LocalDate.of(2025, 8, 31),
            minimumOrderPrice = 50_000
        )
    }

    @Test
    fun `만료일이 넘지 않고 최소 금액 보다 많으면 쿠폰이 적용 가능하다`() {

        val currentDateTime = LocalDateTime.of(2025, 6, 8, 20, 47)

        val cart = Cart(
            listOf(
                CartItem(
                    id = 1,
                    product = Product(
                        id = 1,
                        name = "밥",
                        price = 50_001,
                        category = "식료품",
                    ),
                    quantity = 1
                )
            )
        )

        val actual = freeShippingCoupon.isAvailable(cart, currentDateTime)
        val expected = true

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `만료일이 넘었고 최소 금액 보다 많으면 쿠폰이 적용 불가능하다`() {

        val currentDateTime = LocalDateTime.of(2025, 12, 1, 20, 47)

        val cart = Cart(
            listOf(
                CartItem(
                    id = 1,
                    product = Product(
                        id = 1,
                        name = "밥",
                        price = 50_001,
                        category = "식료품",
                    ),
                    quantity = 1
                )
            )
        )

        val actual = freeShippingCoupon.isAvailable(cart, currentDateTime)
        val expected = false

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `만료일이 넘지 않고 최소 금액 보다 적으면 쿠폰이 적용 불가능하다`() {

        val currentDateTime = LocalDateTime.of(2025, 6, 8, 20, 47)

        val cart = Cart(
            listOf(
                CartItem(
                    id = 1,
                    product = Product(
                        id = 1,
                        name = "밥",
                        price = 9_999,
                        category = "식료품",
                    ),
                    quantity = 1
                )
            )
        )

        val actual = freeShippingCoupon.isAvailable(cart, currentDateTime)
        val expected = false

        assertThat(actual).isEqualTo(expected)
    }
}
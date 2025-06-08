package woowacourse.shopping.domain.product

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.coupon.MiracleSaleCoupon
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MiracleSaleCouponTest {
    private lateinit var miracleSaleCoupon: MiracleSaleCoupon
    private var cart: Cart = Cart(emptyList())

    @BeforeEach
    fun setUp() {
        miracleSaleCoupon = MiracleSaleCoupon(
            couponId = 1L,
            expirationDate = LocalDate.of(2025, 8, 31),
            startHour = LocalTime.of(4, 0),
            endHour = LocalTime.of(7, 0),
            discountRate = 30.0,
        )
        cart = Cart(
            listOf(
                CartItem(
                    id = 1,
                    product = Product(
                        id = 1,
                        name = "밥",
                        price = 100_001,
                        category = "식료품",
                    ),
                    quantity = 3
                )
            )
        )
    }

    @Test
    fun `만료일이 넘지 않고 현재시간이 쿠폰 적용 가능한 시간 내에 있으면 쿠폰이 적용 가능하다`() {

        val currentDateTime = LocalDateTime.of(2025, 6, 8, 5, 47)

        val actual = miracleSaleCoupon.isAvailable(cart, currentDateTime)
        val expected = true

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `만료일이 넘지 않고 현재시간이 쿠폰 적용 가능한 시간 내에 있지 않으면 쿠폰이 적용 불가능하다`() {

        val currentDateTime = LocalDateTime.of(2025, 6, 8, 20, 47)

        val actual = miracleSaleCoupon.isAvailable(cart, currentDateTime)
        val expected = false

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `만료일이 넘고 현재시간이 쿠폰 적용 가능한 시간 내에 있으면 쿠폰이 적용 불가능하다`() {

        val currentDateTime = LocalDateTime.of(2025, 11, 8, 5, 47)


        val actual = miracleSaleCoupon.isAvailable(cart, currentDateTime)
        val expected = false

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `쿠폰을 적용하게 되면 할인율 만큼 할인된 금액이 나온다`() {
        val cart = Cart(
            listOf(
                CartItem(
                    id = 1,
                    product = Product(
                        id = 1,
                        name = "밥",
                        price = 10_000,
                        category = "식료품",
                    ),
                    quantity = 1
                )
            ),
        )

        val actual = miracleSaleCoupon.discountPrice(cart)
        val expected = 3000

        assertThat(actual).isEqualTo(expected)
    }
}
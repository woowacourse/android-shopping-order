package woowacourse.shopping.domain.product

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.coupon.BoGoCoupon
import java.time.LocalDate
import java.time.LocalDateTime

class BoGoCouponTest {
    private lateinit var boGoCoupon: BoGoCoupon

    @BeforeEach
    fun setUp() {
        boGoCoupon = BoGoCoupon(
            couponId = 1L,
            expirationDate = LocalDate.of(2025, 8, 31),
            buyQuantity = 2,
            getQuantity = 1,
        )
    }

    @Test
    fun `만료일이 넘지 않고 동일한 품목의 갯수가 3개 보다 적지 않으면 쿠폰이 적용 가능하다`() {

        val currentDateTime = LocalDateTime.of(2025, 6, 8, 20, 47)

        val cart = Cart(
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

        val actual = boGoCoupon.isAvailable(cart, currentDateTime)
        val expected = true

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `만료일이 넘지 않고 동일한 품목의 갯수가 3개 보다 적으면 쿠폰이 적용 불가능하다`() {

        val currentDateTime = LocalDateTime.of(2025, 6, 8, 20, 47)

        val cart = Cart(
            listOf(
                CartItem(
                    id = 1,
                    product = Product(
                        id = 1,
                        name = "밥",
                        price = 100_001,
                        category = "식료품",
                    ),
                    quantity = 2
                )
            )
        )

        val actual = boGoCoupon.isAvailable(cart, currentDateTime)
        val expected = false

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `만료일이 넘고 동일한 품목의 갯수가 3개 보다 적지 않으면 쿠폰이 적용 불가능하다`() {

        val currentDateTime = LocalDateTime.of(2025, 11, 8, 20, 47)

        val cart = Cart(
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

        val actual = boGoCoupon.isAvailable(cart, currentDateTime)
        val expected = false

        assertThat(actual).isEqualTo(expected)
    }
}
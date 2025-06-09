package woowacourse.shopping.domain.product

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.cart.Receipt
import woowacourse.shopping.domain.coupon.FixedCoupon
import java.time.LocalDate
import java.time.LocalDateTime

class FixedCouponTest {
    private lateinit var fixedCoupon: FixedCoupon

    @BeforeEach
    fun setUp() {
        fixedCoupon = FixedCoupon(
            description = "",
            expirationDate = LocalDate.of(2025, 11, 30),
            minimumOrderPrice = 100_000,
            disCountPrice = 5_000
        )
    }

    @Test
    fun `만료일이 넘지 않고 최소 금액 보다 많으면 쿠폰이 적용 가능하다`() {

        val currentDateTime = LocalDateTime.of(2025, 6, 8, 20, 47)

        val receipt = Receipt(
            listOf(
                CartItem(
                    id = 1,
                    product = Product(
                        id = 1,
                        name = "밥",
                        price = 100_001,
                        category = "식료품",
                    ),
                    quantity = 1
                )
            )
        )

        val actual = fixedCoupon.isAvailable(receipt, currentDateTime)
        val expected = true

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `만료일이 넘었고 최소 금액 보다 많으면 쿠폰이 적용 불가능하다`() {

        val currentDateTime = LocalDateTime.of(2025, 12, 1, 20, 47)

        val receipt = Receipt(
            listOf(
                CartItem(
                    id = 1,
                    product = Product(
                        id = 1,
                        name = "밥",
                        price = 100_001,
                        category = "식료품",
                    ),
                    quantity = 1
                )
            )
        )

        val actual = fixedCoupon.isAvailable(receipt, currentDateTime)
        val expected = false

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `만료일이 넘지 않고 최소 금액 보다 적으면 쿠폰이 적용 불가능하다`() {

        val currentDateTime = LocalDateTime.of(2025, 6, 8, 20, 47)

        val receipt = Receipt(
            listOf(
                CartItem(
                    id = 1,
                    product = Product(
                        id = 1,
                        name = "밥",
                        price = 99_999,
                        category = "식료품",
                    ),
                    quantity = 1
                )
            )
        )

        val actual = fixedCoupon.isAvailable(receipt, currentDateTime)
        val expected = false

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `쿠폰을 적용하게 되면 할인된 금액이 나온다`() {
        val receipt = Receipt(
            listOf(
                CartItem(
                    id = 1,
                    product = Product(
                        id = 1,
                        name = "밥",
                        price = 100_001,
                        category = "식료품",
                    ),
                    quantity = 1
                )
            )
        )

        val actual = fixedCoupon.discountPrice(receipt)
        val expected = 5_000

        assertThat(actual).isEqualTo(expected)
    }
}
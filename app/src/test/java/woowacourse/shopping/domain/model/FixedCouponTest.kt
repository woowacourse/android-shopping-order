package woowacourse.shopping.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import woowacourse.shopping.fixedCoupon
import java.lang.IllegalArgumentException
import java.time.LocalDate

class FixedCouponTest {
    @Test
    fun `쿠폰 만료일이 2024년 3월 1일이고 쿠폰을 2024년 6월 1일에 사용한다면 적용할 수 있다`() {
        // given
        val fixedCoupon = fixedCoupon(id = 0, discount = 5_000, expirationDate = LocalDate.of(2024, 3, 1))

        // when
        val actual = fixedCoupon.isExpired(LocalDate.of(2024, 6, 1))

        // given
        assertThat(actual).isTrue
    }

    @Test
    fun `쿠폰 만료일이 2024년 3월 1일이고 쿠폰을 2024년 2월 1일에 사용한다면 적용할 수 없다`() {
        // given
        val fixedCoupon = fixedCoupon(id = 0, discount = 5_000, expirationDate = LocalDate.of(2024, 3, 1))

        // when
        val actual = fixedCoupon.isExpired(LocalDate.of(2024, 2, 1))

        // given
        assertThat(actual).isFalse
    }

    @Test
    fun `쿠폰을 적용할 수 있는 최소 주문 금액이 10,000원이고 주문 금액이 5,000원이라면 적용할 수 없다`() {
        // given
        val fixedCoupon = fixedCoupon(id = 0, discount = 1_000, minimumPrice = 10_000)
        val cartItems = cartItems(totalPrice = 5_000)

        // when
        val actual = fixedCoupon.available(cartItems)

        // given
        assertThat(actual).isFalse
    }

    @Test
    fun `쿠폰을 적용할 수 있는 최소 주문 금액이 10,000원이고 주문 금액이 15,000원이라면 적용할 수 없다`() {
        // given
        val fixedCoupon = fixedCoupon(id = 0, discount = 1_000, minimumPrice = 10_000)
        val cartItems = cartItems(totalPrice = 15_000)

        // when
        val actual = fixedCoupon.available(cartItems)

        // given
        assertThat(actual).isTrue
    }

    @Test
    fun `쿠폰을 적용할 수 없는 상태에서 쿠폰 할인 금액을 구하면 예외가 발생한다`() {
        // given
        val fixedCoupon = fixedCoupon(id = 0, discount = 1_000, minimumPrice = 10_000)
        val cartItems = cartItems(totalPrice = 5_000)

        // when
        assertThrows<IllegalArgumentException> {
            fixedCoupon.discountPrice(cartItems)
        }
    }

    @Test
    fun `쿠폰 할인 금액이 5,000원이다`() {
        // given
        val fixedCoupon = fixedCoupon(id = 0, discount = 5_000, minimumPrice = 10_000)
        val cartItems = cartItems(totalPrice = 15_000)

        // when
        val actual = fixedCoupon.discountPrice(cartItems)

        // given
        assertThat(actual).isEqualTo(5_000)
    }

    private fun cartItems(totalPrice: Int): List<CartItem> {
        return listOf(CartItem(id = 0, product = Product(0, "", totalPrice, "", ""), quantity = Quantity(1)))
    }
}

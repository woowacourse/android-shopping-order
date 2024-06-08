package woowacourse.shopping.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class BuyXgetYCouponTest {
    @Test
    fun `쿠폰을 적용할 수 있는 개수가 3개이고 동일한 제품 1개를 주문했다면 적용할 수 없다`() {
        // given
        val buyXgetYCoupon = buyXgetYCoupon(buyQuantity = 2, getQuantity = 1)
        val cartItems = cartItems(productId = 0, quantity = 1, price = 1_000)

        // when
        val actual = buyXgetYCoupon.available(cartItems)

        // then
        assertThat(actual).isFalse
    }

    @Test
    fun `쿠폰을 적용할 수 있는 개수가 3개이고 동일한 제품 3개를 주문했다면 적용할 수 있다`() {
        // given
        val buyXgetYCoupon = buyXgetYCoupon(buyQuantity = 2, getQuantity = 1)
        val cartItems = cartItems(productId = 0, quantity = 3, price = 1_000)

        // when
        val actual = buyXgetYCoupon.available(cartItems)

        // then
        assertThat(actual).isTrue
    }

    @Test
    fun `제품이 2,00원이고 무료 제공 수량이 1개라면 2,000원 할인된다`() {
        // given
        val buyXgetYCoupon = buyXgetYCoupon(buyQuantity = 2, getQuantity = 1)
        val cartItems = cartItems(productId = 0, quantity = 3, price = 2_000)

        // when
        val actual = buyXgetYCoupon.discountPrice(cartItems)

        // then
        assertThat(actual).isEqualTo(2_000)
    }

    @Test
    fun `쿠폰을 적용할 수 있는 개수가 3개이고 동일한 제품 3개를 여러개 주문했다면 가장 비싼 제품만큼 할인된다`() {
        // given
        val buyXgetYCoupon = buyXgetYCoupon(buyQuantity = 2, getQuantity = 1)
        val cartItems =
            cartItems(productId = 0, quantity = 3, price = 1_000) +
                cartItems(productId = 1, quantity = 3, price = 2_000) +
                cartItems(productId = 2, quantity = 3, price = 3_000)

        // when
        val actual = buyXgetYCoupon.discountPrice(cartItems)

        // then
        assertThat(actual).isEqualTo(3_000)
    }

    private fun buyXgetYCoupon(
        id: Int = 0,
        expirationDate: LocalDate = LocalDate.of(3000, 10, 10),
        buyQuantity: Int,
        getQuantity: Int,
    ): BuyXgetYCoupon {
        return BuyXgetYCoupon(id, "", "", expirationDate, buyQuantity, getQuantity)
    }

    private fun cartItems(
        productId: Int,
        quantity: Int,
        price: Int,
    ): List<CartItem> {
        return listOf(CartItem(id = 0, product = Product(productId, "", price, "", ""), quantity = Quantity(quantity)))
    }
}

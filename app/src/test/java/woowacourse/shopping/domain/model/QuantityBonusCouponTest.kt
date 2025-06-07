package woowacourse.shopping.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.model.DUMMY_COUPON_2
import woowacourse.shopping.model.DUMMY_LOCAL_DATE_TIME_1
import woowacourse.shopping.model.DUMMY_PRODUCT_DETAIL_2
import woowacourse.shopping.model.DUMMY_PRODUCT_DETAIL_3
import java.time.LocalDate

class QuantityBonusCouponTest {
    @Test
    fun `조건을 만족하면 가장 비싼 상품이 할인된다`() {
        // given
        val coupon = DUMMY_COUPON_2
        val product1 = Product(DUMMY_PRODUCT_DETAIL_2.copy(price = 5000), cartId = 1, quantity = 3, isSelected = true)
        val product2 = Product(DUMMY_PRODUCT_DETAIL_3.copy(price = 2500), cartId = 1, quantity = 3, isSelected = true)
        val products = Products(listOf(product1, product2))

        // when
        val price = coupon.apply(products, DUMMY_LOCAL_DATE_TIME_1)

        // then
        assertThat(price.original).isEqualTo(22500)
        assertThat(price.discount).isEqualTo(5000)
        assertThat(price.shipping).isEqualTo(3000)
        assertThat(price.result).isEqualTo(20500)
    }

    @Test
    fun `수량이 6개이면 2세트가 적용되어 2개 할인된다`() {
        // given
        val coupon = DUMMY_COUPON_2
        val product = Product(DUMMY_PRODUCT_DETAIL_2.copy(price = 5000), cartId = 1, quantity = 6, isSelected = true)
        val products = Products(listOf(product))

        // when
        val price = coupon.apply(products, DUMMY_LOCAL_DATE_TIME_1)

        // then
        assertThat(price.original).isEqualTo(30000)
        assertThat(price.discount).isEqualTo(5000 * 2)
        assertThat(price.result).isEqualTo(23000)
    }

    @Test
    fun `조건을 만족하지 않으면 쿠폰은 적용되지 않는다`() {
        // given
        val coupon = DUMMY_COUPON_2
        val product = Product(DUMMY_PRODUCT_DETAIL_2.copy(price = 5000), cartId = 1, quantity = 2, isSelected = true)
        val products = Products(listOf(product))

        // when
        val price = coupon.apply(products, DUMMY_LOCAL_DATE_TIME_1)

        // then
        assertThat(price.original).isEqualTo(10000)
        assertThat(price.discount).isEqualTo(0)
        assertThat(price.result).isEqualTo(13000)
    }

    @Test
    fun `유효기간이 지나면 쿠폰은 사용할 수 없다`() {
        // given
        val expiredCoupon =
            DUMMY_COUPON_2.copy(
                detail = DUMMY_COUPON_2.detail.copy(expirationDate = LocalDate.of(2020, 1, 1)),
            )
        val product = Product(DUMMY_PRODUCT_DETAIL_2, cartId = 1, quantity = 3, isSelected = true)
        val products = Products(listOf(product))

        // when
        val result = expiredCoupon.getIsAvailable(products, DUMMY_LOCAL_DATE_TIME_1)

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `모든 조건을 만족하면 쿠폰을 사용할 수 있다`() {
        // given
        val coupon = DUMMY_COUPON_2
        val product = Product(DUMMY_PRODUCT_DETAIL_2, cartId = 1, quantity = 3, isSelected = true)
        val products = Products(listOf(product))

        // when
        val result = coupon.getIsAvailable(products, DUMMY_LOCAL_DATE_TIME_1)

        // then
        assertThat(result).isTrue()
    }
}

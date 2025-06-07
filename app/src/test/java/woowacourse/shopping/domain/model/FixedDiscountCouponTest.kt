package woowacourse.shopping.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.model.DUMMY_COUPON_1
import woowacourse.shopping.model.DUMMY_LOCAL_DATE_TIME_1
import woowacourse.shopping.model.DUMMY_PRODUCTS_2
import woowacourse.shopping.model.DUMMY_PRODUCTS_3
import woowacourse.shopping.model.DUMMY_PRODUCTS_4

class FixedDiscountCouponTest {
    @Test
    fun `조건을 만족하면 쿠폰 할인이 적용된다`() {
        // given
        val coupon = DUMMY_COUPON_1
        val products = DUMMY_PRODUCTS_3

        // when
        val price = coupon.apply(products, DUMMY_LOCAL_DATE_TIME_1)

        // then
        assertThat(price.original).isEqualTo(1119200)
        assertThat(price.discount).isEqualTo(5000)
        assertThat(price.shipping).isEqualTo(3000)
        assertThat(price.result).isEqualTo(1117200)
    }

    @Test
    fun `조건을 만족하지 않으면 할인이 적용되지 않는다`() {
        // given
        val coupon = DUMMY_COUPON_1
        val products = DUMMY_PRODUCTS_4

        // when
        val price = coupon.apply(products, DUMMY_LOCAL_DATE_TIME_1)

        // then
        assertThat(price.original).isEqualTo(11900)
        assertThat(price.discount).isEqualTo(0)
        assertThat(price.result).isEqualTo(14900)
    }

    @Test
    fun `유효기간이 지나면 할인은 적용되지 않는다`() {
        // given
        val expiredCoupon =
            DUMMY_COUPON_1.copy(
                detail =
                    DUMMY_COUPON_1.detail.copy(
                        expirationDate = DUMMY_LOCAL_DATE_TIME_1.toLocalDate().minusDays(1),
                    ),
            )
        val products = DUMMY_PRODUCTS_3

        // when
        val price = expiredCoupon.apply(products, DUMMY_LOCAL_DATE_TIME_1)

        // then
        assertThat(price.discount).isEqualTo(0)
        assertThat(price.result).isEqualTo(products.selectedProductsPrice + 3000)
    }

    @Test
    fun `최소 금액과 날짜 조건을 만족하면 쿠폰을 사용할 수 있다`() {
        // given
        val coupon = DUMMY_COUPON_1
        val products = DUMMY_PRODUCTS_3

        // when
        val result = coupon.getIsAvailable(products, DUMMY_LOCAL_DATE_TIME_1)

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `최소 금액을 만족하지 않으면 쿠폰을 사용할 수 없다`() {
        // given
        val coupon = DUMMY_COUPON_1
        val products = DUMMY_PRODUCTS_2

        // when
        val result = coupon.getIsAvailable(products, DUMMY_LOCAL_DATE_TIME_1)

        // then
        assertThat(result).isFalse()
    }
}

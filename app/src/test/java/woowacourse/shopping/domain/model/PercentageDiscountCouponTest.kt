package woowacourse.shopping.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.model.DUMMY_COUPON_4
import woowacourse.shopping.model.DUMMY_LOCAL_DATE_TIME_1
import woowacourse.shopping.model.DUMMY_LOCAL_DATE_TIME_2
import woowacourse.shopping.model.DUMMY_PRODUCTS_3
import woowacourse.shopping.model.DUMMY_PRODUCTS_4

class PercentageDiscountCouponTest {
    @Test
    fun `조건을 만족하면 퍼센트 할인 쿠폰이 적용된다`() {
        // given
        val coupon = DUMMY_COUPON_4
        val products = DUMMY_PRODUCTS_3

        // when
        val price = coupon.apply(products, DUMMY_LOCAL_DATE_TIME_1)

        // then
        assertThat(price.original).isEqualTo(1119200)
        assertThat(price.discount).isEqualTo((1119200 * 0.3).toInt())
        assertThat(price.shipping).isEqualTo(3000)
        assertThat(price.result).isEqualTo(1119200 - (1119200 * 0.3).toInt() + 3000)
    }

    @Test
    fun `조건을 만족하지 않으면 할인은 적용되지 않는다`() {
        // given
        val coupon = DUMMY_COUPON_4
        val products = DUMMY_PRODUCTS_4

        // when
        val price = coupon.apply(products, DUMMY_LOCAL_DATE_TIME_2)

        // then
        assertThat(price.original).isEqualTo(11900)
        assertThat(price.discount).isEqualTo(0)
        assertThat(price.result).isEqualTo(14900)
    }

    @Test
    fun `할인 시간이 아닐 경우 쿠폰을 사용할 수 없다`() {
        // given
        val products = DUMMY_PRODUCTS_3
        val invalidTime = DUMMY_LOCAL_DATE_TIME_2

        // when
        val result = DUMMY_COUPON_4.getIsAvailable(products, invalidTime)

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `유효기간이 지나면 쿠폰을 사용할 수 없다`() {
        // given
        val expiredCoupon =
            DUMMY_COUPON_4.copy(
                detail =
                    DUMMY_COUPON_4.detail.copy(
                        expirationDate = DUMMY_LOCAL_DATE_TIME_1.toLocalDate().minusDays(1),
                    ),
            )
        val products = DUMMY_PRODUCTS_3

        // when
        val result = expiredCoupon.getIsAvailable(products, DUMMY_LOCAL_DATE_TIME_1)

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `할인 시간과 유효기간이 유효하면 쿠폰을 사용할 수 있다`() {
        // given
        val coupon = DUMMY_COUPON_4
        val products = DUMMY_PRODUCTS_3

        // when
        val result = coupon.getIsAvailable(products, DUMMY_LOCAL_DATE_TIME_1)

        // then
        assertThat(result).isTrue()
    }
}

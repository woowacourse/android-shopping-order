package woowacourse.shopping.domain.order

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DiscountTypeTest {
    @Test
    fun `title이 fixed이면 PRICE_DISCOUNT를 반환한다`() {
        // given:
        // when:
        // then:
        val result = DiscountType.from("fixed")
        assertThat(result).isEqualTo(DiscountType.PRICE_DISCOUNT)
    }

    @Test
    fun `title이 buyXgetY이면 BONUS를 반환한다`() {
        // given:
        // when:
        // then:
        val result = DiscountType.from("buyXgetY")
        assertThat(result).isEqualTo(DiscountType.BONUS)
    }

    @Test
    fun `title이 freeShipping이면 FREE_SHIPPING을 반환한다`() {
        // given:
        // when:
        // then:
        val result = DiscountType.from("freeShipping")
        assertThat(result).isEqualTo(DiscountType.FREE_SHIPPING)
    }

    @Test
    fun `title이 percentage이면 PERCENTAGE_DISCOUNT를 반환한다`() {
        // given:
        // when:
        // then:
        val result = DiscountType.from("percentage")
        assertThat(result).isEqualTo(DiscountType.PERCENTAGE_DISCOUNT)
    }

    @Test
    fun `일치하는 title이 없으면 예외를 던진다`() {
        // given:
        // when:
        // then:
        val exception =
            assertThrows<IllegalArgumentException> {
                DiscountType.from("moongchi")
            }
        assertThat(exception.message).isEqualTo("moongchi 에 해당하는 할인 타입이 없습니다.")
    }
}

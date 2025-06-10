package woowacourse.shopping.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PriceTest {
    @Test
    fun `할인 가격과 배송비에 따라 최종 금액을 반환한다`() {
        // given
        val price =
            Price(
                original = 10_000,
                discount = 2_000,
                shipping = 3_000,
            )

        // when
        val result = price.result

        // then
        assertThat(result).isEqualTo(11_000)
    }
}

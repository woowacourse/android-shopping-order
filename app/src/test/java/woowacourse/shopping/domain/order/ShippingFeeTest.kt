package woowacourse.shopping.domain.order

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ShippingFeeTest {
    @Test
    fun `배송비가 0원 미만일 경우 에러를 발생한다`() {
        // given:
        // when:
        // then:
        assertThrows<IllegalArgumentException> { ShippingFee(-100) }
    }

    @Test
    fun `기본 배송비는 3000원이다`() {
        // given:
        // when:
        val shippingFee = ShippingFee()

        // then:
        assertThat(shippingFee.amount).isEqualTo(3_000)
    }
}

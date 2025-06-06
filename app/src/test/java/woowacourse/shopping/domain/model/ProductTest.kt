package woowacourse.shopping.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.model.DUMMY_CATALOG_PRODUCT_1

class ProductTest {
    @Test
    fun `금액에 따른 총 가격을 반환한다`() {
        // given
        val product = DUMMY_CATALOG_PRODUCT_1

        // when
        val price = product.totalPrice

        // then
        assertThat(price).isEqualTo(11900 * 5)
    }

    @Test
    fun `수량을 증가시키면 수량이 증가된 객체를 반환한다`() {
        // given
        val product = DUMMY_CATALOG_PRODUCT_1.copy(quantity = 3)

        // when
        val updated = product.increaseQuantity()

        // then
        assertThat(updated.quantity).isEqualTo(4)
    }

    @Test
    fun `수량을 감소시키면 수량이 감소된 객체를 반환한다`() {
        // given
        val product = DUMMY_CATALOG_PRODUCT_1.copy(quantity = 3)

        // when
        val updated = product.decreaseQuantity()

        // then
        assertThat(updated.quantity).isEqualTo(2)
    }

    @Test
    fun `수량은 0개 이하로 감소되지 않는다`() {
        // given
        val product = DUMMY_CATALOG_PRODUCT_1.copy(quantity = 0)

        // when
        val updated = product.decreaseQuantity()

        // then
        assertThat(updated.quantity).isEqualTo(0)
    }
}

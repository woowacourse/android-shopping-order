package woowacourse.shopping.domain

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.cartProduct

class CartProductTest {
    @Test
    fun `갯수를 증가시킬 수 있다`() {
        val newCartProduct = cartProduct.copy(quantity = 2)
        newCartProduct.plusQuantity()
        newCartProduct.quantity shouldBe 3
    }

    @Test
    fun `갯수를 감소시킬 수 있다`() {
        val newCartProduct = cartProduct.copy(quantity = 2)
        newCartProduct.minusQuantity()
        newCartProduct.quantity shouldBe 1
    }

    @Test
    fun `감소시키는 갯수는 0 미만으로 내려가지 않는다`() {
        val newCartProduct = cartProduct.copy(quantity = 2)
        repeat(10) {
            newCartProduct.minusQuantity()
        }
        newCartProduct.quantity shouldBe 0
    }
}

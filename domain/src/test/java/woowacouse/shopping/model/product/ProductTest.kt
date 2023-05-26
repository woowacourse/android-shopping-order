package woowacouse.shopping.model.product

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProductTest {
    @Test
    fun `상품 개수를 하나 추가한다`() {
        // given
        val product = Product(1L, "피자", 12_000, 0)

        // when
        val actual = product.increaseCount()

        // then
        val expected = 1
        assertEquals(expected, actual.count)
    }

    @Test
    fun `상품 개수가 99개일 때 추가 해도 개수가 증가하지 않는다`() {
        // given
        val product = Product(1L, "피자", 12_000, 99)

        // when
        val actual = product.increaseCount()

        // then
        val expected = 99
        assertEquals(expected, actual.count)
    }

    @Test
    fun `상품 개수를 하나 감소 할 수 있다`() {
        // given
        val product = Product(1L, "피자", 12_000, 2)

        // when
        val actual = product.decreaseCount()

        // then
        val expected = 1
        assertEquals(expected, actual.count)
    }

    @Test
    fun `상품 개수가 0개일 때 개수를 빼도 개수가 음수가 되지 않는다`() {
        // given
        val product = Product(1L, "피자", 12_000, 0)

        // when
        val actual = product.decreaseCount()

        // then
        val expected = 0
        assertEquals(expected, actual.count)
    }
}

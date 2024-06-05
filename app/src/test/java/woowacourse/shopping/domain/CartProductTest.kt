package woowacourse.shopping.domain

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test

class CartProductTest {
    @Test
    fun `카트 상품의 수량은 1 이상이다`() {
        shouldThrow<IllegalArgumentException> {
            fakeCartProduct(count = 0)
        }
        shouldNotThrow<IllegalArgumentException> {
            fakeCartProduct(count = 1)
        }
    }
}
package woowacourse.shopping.model

import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PriceTest {

    @ValueSource(ints = [-1, -10, -100])
    @ParameterizedTest
    fun `가격은 음수일 수 없다`(value: Int) {
        assertThrows<IllegalArgumentException> { Price(value) }
    }

    @ValueSource(ints = [0, 1, 10])
    @ParameterizedTest
    fun `가격은 0원 이상이어야 한다`(value: Int) {
        assertDoesNotThrow { Price(value) }
    }
}

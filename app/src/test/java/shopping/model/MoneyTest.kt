@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class MoneyTest {
    @Test
    fun `0 이상의 정수가 주어지면 Money 객체가 정상적으로 생성된다`() {
        val expected = 0L
        val actual = Money(0L).value

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @ValueSource(longs = [-1, -10, -100, -9999])
    fun `음수의 정수로 생성하려고 할 경우 적절한 예외가 발생한다`(invalidValue: Long) {
        assertThrows<IllegalArgumentException> { Money(invalidValue) }
    }

    @Test
    fun `동일한 금액을 가진 두 Money 객체는 동등한 것으로 간주한다`() {
        val expected = Money(1000L)
        val actual = Money(1000L)

        assertEquals(expected, actual)
    }
}

package woowacourse.shopping.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PointTest {
    @Test
    fun `포인트는 음수일 수 없다`() {
        assertThrows<IllegalArgumentException> { Point(-3) }
    }
}

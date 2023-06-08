package woowacourse.shopping.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class EarnRateTest {
    @Test
    fun `적립률은 음수일 수 없다`() {
        assertThrows<IllegalArgumentException> { EarnRate(-3) }
    }
}

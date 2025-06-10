package woowacourse.shopping.util

import woowacourse.shopping.data.util.TimeProvider
import java.time.LocalDateTime

class TestTimeProvider(
    private val testTime: LocalDateTime,
) : TimeProvider {
    override fun currentTime(): LocalDateTime = testTime
}

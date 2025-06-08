package woowacourse.shopping.data.util

import java.time.LocalDateTime

class TestTimeProvider(private val testTime: LocalDateTime) : TimeProvider {
    override fun currentTime(): LocalDateTime {
        return testTime
    }
}
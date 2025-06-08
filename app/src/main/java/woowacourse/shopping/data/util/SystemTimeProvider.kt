package woowacourse.shopping.data.util

import java.time.LocalDateTime
import java.time.ZoneId

class SystemTimeProvider : TimeProvider {
    override fun currentTime(): LocalDateTime {
        return LocalDateTime.now(ZoneId.of("Asia/Seoul"))
    }
}

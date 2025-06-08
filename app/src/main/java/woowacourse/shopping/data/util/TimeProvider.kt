package woowacourse.shopping.data.util

import java.time.LocalDateTime

interface TimeProvider {
    fun currentTime(): LocalDateTime
}
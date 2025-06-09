package woowacourse.shopping.domain.coupon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.LocalTime

class AvailableTimeTest {
    val availableTime =
        AvailableTime(
            LocalTime.of(4, 0),
            LocalTime.of(7, 0),
        )

    @Test
    fun `사용 가능 시간이면 true를 반환한다`() {
        val actual = availableTime.isAvailable(LocalDateTime.of(2025, 6, 30, 5, 0))
        assertThat(actual).isTrue()
    }

    @Test
    fun `사용 불가능 시간이면 false를 반환한다`() {
        val actual = availableTime.isAvailable(LocalDateTime.of(2025, 6, 30, 20, 0))
        assertThat(actual).isFalse()
    }
}

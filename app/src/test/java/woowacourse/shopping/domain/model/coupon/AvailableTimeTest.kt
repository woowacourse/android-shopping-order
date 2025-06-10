package woowacourse.shopping.domain.model.coupon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalTime

class AvailableTimeTest {
    @Test
    fun `사용 가능 여부를 확인할 수 있다`() {
        // Give
        val availableTime = AvailableTime(LocalTime.of(0, 0), LocalTime.of(23, 59))

        // When
        val isAvailable = availableTime.isAvailable(LocalTime.of(12, 0))

        // Then
        assertThat(isAvailable).isTrue()
    }
}

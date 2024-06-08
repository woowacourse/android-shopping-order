package woowacourse.shopping.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException
import java.time.LocalTime

class AvailableTimeTest {
    @Test
    fun `쿠폰 적용의 시작 시간이 끝 시간보다 이전이라면 예외가 발생하지 않는다`() {
        assertDoesNotThrow {
            availableTime(9, 12)
        }
    }

    @Test
    fun `쿠폰 적용의 시작 시간이 끝 시간과 같으면 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            availableTime(12, 12)
        }
    }

    @Test
    fun `쿠폰 적용의 시작 시간이 끝 시간보다 이후라면 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            availableTime(12, 9)
        }
    }

    @Test
    fun `쿠폰 적용 시간이 9시 00분부터 12시 00분까지이고, 현재 시간이 9시 00분이라면 쿠폰을 적용할 수 있다`() {
        // given
        val availableTime = availableTime(9, 12)

        // when
        val now = LocalTime.of(9, 0)
        val actual = availableTime.available(now)

        // then
        assertThat(actual).isTrue
    }

    @Test
    fun `쿠폰 적용 시간이 9시 00분부터 12시 00분까지이고, 현재 시간이 10시 00분이라면 쿠폰을 적용할 수 있다`() {
        // given
        val availableTime = availableTime(9, 12)

        // when
        val now = LocalTime.of(10, 0)
        val actual = availableTime.available(now)

        // then
        assertThat(actual).isTrue
    }

    @Test
    fun `쿠폰 적용 시간이 9시 00분부터 12시 00분까지이고, 현재 시간이 15시 00분이라면 쿠폰을 적용할 수 없다`() {
        // given
        val availableTime = availableTime(9, 12)

        // when
        val now = LocalTime.of(15, 0)
        val actual = availableTime.available(now)

        // then
        assertThat(actual).isFalse
    }

    private fun availableTime(
        startHour: Int,
        endHour: Int,
    ): AvailableTime {
        val start = LocalTime.of(startHour, 0)
        val end = LocalTime.of(endHour, 0)
        return AvailableTime(start, end)
    }
}

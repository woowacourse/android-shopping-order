package woowacourse.shopping.domain.coupon

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MiracleSaleCouponTest {
    @Test
    fun `쿠폰이 만료되었으면 false를 반환한다`() {
        val coupon =
            MiracleSaleCoupon(
                id = 0,
                code = "",
                description = "",
                discountType = "",
                expirationDate = LocalDate.of(2024, 12, 31),
                availableTime =
                    MiracleSaleCoupon.AvailableTime(
                        start = LocalTime.of(9, 0),
                        end = LocalTime.of(17, 0),
                    ),
            )

        val result =
            coupon.isUsable(
                LocalDateTime.of(2025, 1, 1, 10, 0),
            )

        assertFalse(result)
    }

    @Test
    fun `사용 가능한 날짜지만 시간이 범위를 벗어나면 false를 반환한다`() {
        val coupon =
            MiracleSaleCoupon(
                id = 0,
                code = "",
                description = "",
                discountType = "",
                expirationDate = LocalDate.of(2025, 12, 31),
                availableTime =
                    MiracleSaleCoupon.AvailableTime(
                        start = LocalTime.of(10, 0),
                        end = LocalTime.of(18, 0),
                    ),
            )

        val result =
            coupon.isUsable(
                LocalDateTime.of(2025, 6, 6, 9, 59),
            )

        assertFalse(result)
    }

    @Test
    fun `사용 가능한 날짜와 시간이 모두 유효하면 true를 반환한다`() {
        val coupon =
            MiracleSaleCoupon(
                id = 0,
                code = "",
                description = "",
                discountType = "",
                expirationDate = LocalDate.of(2025, 12, 31),
                availableTime =
                    MiracleSaleCoupon.AvailableTime(
                        start = LocalTime.of(10, 0),
                        end = LocalTime.of(18, 0),
                    ),
            )

        val result =
            coupon.isUsable(
                LocalDateTime.of(2025, 6, 6, 10, 0),
            )

        assertTrue(result)
    }

    @Test
    fun `쿠폰 만료일과 시간 끝이 정확히 일치하는 경우 true를 반환한다`() {
        val coupon =
            MiracleSaleCoupon(
                id = 0,
                code = "",
                description = "",
                discountType = "",
                expirationDate = LocalDate.of(2025, 6, 6),
                availableTime =
                    MiracleSaleCoupon.AvailableTime(
                        start = LocalTime.of(10, 0),
                        end = LocalTime.of(18, 0),
                    ),
            )

        val result =
            coupon.isUsable(
                LocalDateTime.of(2025, 6, 6, 18, 0),
            )

        assertTrue(result)
    }

    @Test
    fun `쿠폰 만료일 이전이지만 시간 범위보다 이후면 false를 반환한다`() {
        val coupon =
            MiracleSaleCoupon(
                id = 0,
                code = "",
                description = "",
                discountType = "",
                expirationDate = LocalDate.of(2025, 6, 7),
                availableTime =
                    MiracleSaleCoupon.AvailableTime(
                        start = LocalTime.of(10, 0),
                        end = LocalTime.of(12, 0),
                    ),
            )

        val result =
            coupon.isUsable(
                LocalDateTime.of(2025, 6, 6, 12, 1),
            )

        assertFalse(result)
    }
}

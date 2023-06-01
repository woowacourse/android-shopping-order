package woowacouse.shopping.model.point

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PointTest {
    @Test
    fun `포인트가 음수라면 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> { Point(-1) }
    }

    @Test
    fun `사용할 포인트가 가진 포인트보다 많으면 가진 포인트만큼을 반환한다`() {
        val hasPoint = Point(1_000)

        val actual = hasPoint.usePoint(10_000)
        val expected = 1_000

        assertEquals(expected, actual.getPoint())
    }

    @Test
    fun `사용할 포인트가 음수라면 0 포인트를 반환한다`() {
        val hasPoint = Point(1_000)

        val actual = hasPoint.usePoint(-1)
        val expected = 0

        assertEquals(expected, actual.getPoint())
    }

    @Test
    fun `기존 포인트가 1_000일 때, 1_500으로 갱신 할 수 있다`() {
        val point = Point(1_000)

        val actual = point.updatePoint(1_500)

        val expected = 1_500

        assertEquals(expected, actual.getPoint())
    }
}

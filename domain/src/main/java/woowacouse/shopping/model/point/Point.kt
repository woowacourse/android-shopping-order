package woowacouse.shopping.model.point

data class Point(private val value: Int) {
    init {
        require(value >= POINT_CONDITION) { ERROR_POINT_IS_NOT_POSITIVE_NUMBER }
    }

    fun usePoint(point: Int): Point {
        return when {
            point > value -> this
            point <= 0 -> Point(0)
            else -> Point(point)
        }
    }

    fun updatePoint(point: Int): Point = copy(value = point)

    fun getPoint(): Int = value

    companion object {
        private const val POINT_CONDITION = 0
        private const val ERROR_POINT_IS_NOT_POSITIVE_NUMBER = "[ERROR] 포인트는 음수 일 수 없습니다"
    }
}

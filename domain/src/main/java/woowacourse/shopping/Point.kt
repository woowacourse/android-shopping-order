package woowacourse.shopping

class Point(val value: Int) {
    init {
        require(value >= 0) { POINT_ERROR_MESSAGE }
    }

    companion object {
        private const val POINT_ERROR_MESSAGE = "포인트는 0원 미만이 될 수 없다."
    }
}

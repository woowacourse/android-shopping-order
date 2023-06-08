package woowacourse.shopping.domain

@JvmInline
value class Point(val value: Int) {
    init {
        validateValue()
    }

    private fun validateValue() {
        require(value >= 0) { POINT_NEGATIVE_ERROR }
    }

    companion object {
        private const val POINT_NEGATIVE_ERROR = "포인트는 음수일 수 없습니다."
    }
}

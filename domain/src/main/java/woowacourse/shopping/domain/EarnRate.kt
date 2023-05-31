package woowacourse.shopping.domain

@JvmInline
value class EarnRate(val value: Int) {
    init {
        validateValue()
    }

    private fun validateValue() {
        require(value >= 0) { EARN_RATE_NEGATIVE_ERROR }
    }

    companion object {
        private const val EARN_RATE_NEGATIVE_ERROR = "포인트 적립률은 음수일 수 없습니다."
    }
}

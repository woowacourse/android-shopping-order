package woowacourse.shopping.data.remote.response.addorder

enum class AddOrderErrorCode(val numberCode: Int) {
    SHORTAGE_STOCK(1), LACK_OF_POINT(2);

    companion object {
        private const val NON_EXIST_NUMBER_CODE_ERROR = "입력된 숫자 오류코드가 로컬에 있는 오류 코드중에 존재하지 않습니다."
        fun getErrorCodeFromNumberCode(numberCode: Int): AddOrderErrorCode =
            AddOrderErrorCode.values().find { it.numberCode == numberCode }
                ?: throw IllegalArgumentException(NON_EXIST_NUMBER_CODE_ERROR)
    }
}

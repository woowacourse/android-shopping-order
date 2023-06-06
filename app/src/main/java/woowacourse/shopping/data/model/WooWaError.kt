package woowacourse.shopping.data.model

enum class WooWaError(val code: Int, val message: String) {
    MONEY_SALE_POLICY_WRONG(4001, "요금 정책과 다른 금액입니다"),
    NO_EXIST_DATA(404, "존재하지 않는 데이터 입니다"),
    EXIST_WRONG_PRODUCT(4002, "존재하지 않는 상품이 존재합니다"),
    NO_AUTHORITY(403, "권한이 없는 요청입니다"),
    FAILED_AUTHORITY(401, "인증에 실패했습니다"),
    PARAM_FORMAT_ERROR(400, "잘못된 request param 형식입니다"),
    UNKNOWN_ERROR(-1, "정의되지 않은 에러입니다");

    companion object {
        private val wooWaErrorMap = values().associateBy { it.code }
        fun findErrorByCode(code: Int?): WooWaError {
            return wooWaErrorMap[code] ?: UNKNOWN_ERROR
        }
    }
}

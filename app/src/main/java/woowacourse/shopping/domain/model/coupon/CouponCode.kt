package woowacourse.shopping.domain.model.coupon

enum class CouponCode(val code: String, val condition: String) {
    FIXED5000("FIXED5000", "최소 주문 금액: %,d원"),
    BOGO("BOGO", ""),
    FREESHIPPING("FREESHIPPING", "최소 주문 금액: %,d원"),
    MIRACLESALE("MIRACLESALE", "사용 가능 시간: 오전 %d시부터 %d시까지"),
    ;

    companion object {
        fun findCode(code: String): CouponCode {
            return entries.first { it.code == code }
        }
    }
}

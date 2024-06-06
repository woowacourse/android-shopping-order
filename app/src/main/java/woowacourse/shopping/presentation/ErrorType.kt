package woowacourse.shopping.presentation

enum class ErrorType(val message: String, val code: Int) {
    ERROR_RECENT_INSERT("최근 아이템 추가 에러", 1),
    ERROR_RECENT_LOAD("최근 아이템 로드 에러", 2),
    ERROR_PRODUCT_LOAD("아이템 로드 오류", 3),
    ERROR_PRODUCT_PLUS("아이템 증가 오류", 4),
    ERROR_PRODUCT_MINUS("아이템 감소 오류", 5),
    ERROR_PRODUCT_PLUS_MINUS("아이템 증감 오류", 6),
    ERROR_CART_LOAD("카트 아이템 로드 에러", 7),
    ERROR_CART_DELETE("카트 아이템 제거 에러", 8),
    ERROR_CART_COUNT_LOAD("카트 카운트 로드 에러", 9),
    ERROR_CURATION_LOAD("큐레이션 로드 에러", 10),
    ERROR_ORDER("주문 에러", 11),
    ERROR_COUPON_LOAD("쿠폰 로드 에러", 12)
}

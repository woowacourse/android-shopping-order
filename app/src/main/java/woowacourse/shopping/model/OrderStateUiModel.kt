package woowacourse.shopping.model

enum class OrderStateUiModel(val value: String) {
    PENDING("결제완료"),
    PROCESSING("배송준비중"),
    SHIPPED("배송중"),
    DELIVERED("배송완료"),
    CANCELED("주문취소")
}

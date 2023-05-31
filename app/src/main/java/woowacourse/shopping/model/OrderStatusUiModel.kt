package woowacourse.shopping.model

import com.example.domain.model.OrderStatus

enum class OrderStatusUiModel(val value: String) {
    PENDING("결제완료"),
    PROCESSING("배송준비중"),
    SHIPPED("배송중"),
    DELIVERED("배송완료"),
    CANCELLED("주문취소")
}

fun OrderStatus.toPresentation(): String {
    return OrderStatusUiModel.valueOf(this.name).value
}

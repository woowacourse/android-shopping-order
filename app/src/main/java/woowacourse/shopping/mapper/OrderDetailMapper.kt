package woowacourse.shopping.mapper

import com.example.domain.model.OrderDetail
import woowacourse.shopping.model.OrderDetailUiModel

fun OrderDetail.toPresentation(): OrderDetailUiModel {
    return OrderDetailUiModel(
        priceBeforeDiscount,
        priceAfterDiscount,
        date,
        orderItems.map { it.toPresentation() },
    )
}

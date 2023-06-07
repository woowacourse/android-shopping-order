package woowacourse.shopping.mapper

import com.example.domain.model.OrderDetail
import com.example.domain.model.OrderProduct
import com.example.domain.model.Price
import woowacourse.shopping.model.OrderDetailUiModel
import woowacourse.shopping.model.OrderProductUiModel

fun OrderDetail.toPresentation(): OrderDetailUiModel {
    return OrderDetailUiModel(
        priceBeforeDiscount.value,
        priceAfterDiscount.value,
        dateTime,
        orderItems.map(OrderProduct::toPresentation)
    )
}

fun OrderDetailUiModel.toDomain(): OrderDetail {
    return OrderDetail(
        Price(priceBeforeDiscount),
        Price(priceAfterDiscount),
        dateTime,
        orderItems.map(OrderProductUiModel::toDomain)
    )
}

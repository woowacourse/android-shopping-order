package woowacourse.shopping.mapper

import com.example.domain.model.OrderProduct
import woowacourse.shopping.model.OrderProductUiModel

fun OrderProduct.toPresentation(): OrderProductUiModel {
    return OrderProductUiModel(productId, name, price, count, imageUrl)
}

package woowacourse.shopping.mapper

import com.example.domain.model.OrderProduct
import woowacourse.shopping.model.OrderProductUiModel

fun OrderProduct.toPresentation(): OrderProductUiModel {
    val productUiModel = product.toPresentation()
    productUiModel.count = count
    return OrderProductUiModel(productUiModel)
}

fun OrderProductUiModel.toDomain(): OrderProduct =
    OrderProduct(productUiModel.toDomain(), productUiModel.count)

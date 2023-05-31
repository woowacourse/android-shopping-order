package woowacourse.shopping.mapper

import com.example.domain.model.OrderProduct
import woowacourse.shopping.model.OrderProductUiModel

fun OrderProduct.toPresentation(): OrderProductUiModel =
    OrderProductUiModel(product.toPresentation())

fun OrderProductUiModel.toDomain(): OrderProduct =
    OrderProduct(productUiModel.toDomain(), productUiModel.count)

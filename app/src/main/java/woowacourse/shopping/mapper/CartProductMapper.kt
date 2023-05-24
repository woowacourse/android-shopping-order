package woowacourse.shopping.mapper

import com.example.domain.model.CartProduct
import woowacourse.shopping.model.CartProductUiModel

fun CartProduct.toPresentation(): CartProductUiModel =
    CartProductUiModel(cartProductId, product.toPresentation(count), count, isSelected)

fun CartProductUiModel.toDomain(): CartProduct =
    CartProduct(cartProductId, productUiModel.toDomain(), count, isSelected)

package woowacourse.shopping.domain.mapper

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.presentation.ui.model.ProductModel

fun Product.toUiModel(quantity: Int = 0) = ProductModel(id, name, imgUrl, price, category, quantity)

package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.ui.model.RecentProductUiModel

fun RecentProductUiModel.toDomain(): RecentProduct =
    RecentProduct(id = id, product = product.toDomain())

fun RecentProduct.toUi(): RecentProductUiModel =
    RecentProductUiModel(id = id, product = product.toUi())

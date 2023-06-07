package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.ui.model.RecentProductUiModel

fun RecentProductUiModel.toDomainModel(): RecentProduct =
    RecentProduct(id = id, product = product.toDomainModel())

fun RecentProduct.toUiModel(): RecentProductUiModel =
    RecentProductUiModel(id = id, product = product.toUiModel())

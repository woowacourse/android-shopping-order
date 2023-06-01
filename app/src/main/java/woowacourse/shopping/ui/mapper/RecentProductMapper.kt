package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.ui.model.RecentProductUiModel

fun RecentProductUiModel.toRecentProductDomainModel(): RecentProduct =
    RecentProduct(id = id, product = product.toProductDomainModel())

fun RecentProduct.toRecentProductUiModel(): RecentProductUiModel =
    RecentProductUiModel(id = id, product = product.toProductUiModel())

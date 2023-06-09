package woowacourse.shopping.presentation.mapper

import woowacourse.shopping.presentation.model.RecentProductsModel
import woowacouse.shopping.model.recentproduct.RecentProducts

fun RecentProducts.toUIModel(): RecentProductsModel = RecentProductsModel(getAll().map { it.toUIModel() })

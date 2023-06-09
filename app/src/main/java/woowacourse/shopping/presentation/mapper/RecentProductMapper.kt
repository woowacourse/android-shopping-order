package woowacourse.shopping.presentation.mapper

import woowacourse.shopping.presentation.model.RecentProductModel
import woowacouse.shopping.model.recentproduct.RecentProduct

fun RecentProduct.toUIModel(): RecentProductModel = RecentProductModel(id, product.toUIModel())

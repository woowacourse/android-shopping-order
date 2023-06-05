package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.model.RecentProductModel

fun RecentProductModel.toDomain(): RecentProduct =
    RecentProduct(id = id, product = product.toDomain())

fun RecentProduct.toUi(): RecentProductModel =
    RecentProductModel(id = id, product = product.toUi())

fun List<RecentProduct>.toUi(): List<RecentProductModel> =
    map { recentProduct -> recentProduct.toUi() }

package woowacourse.shopping.mapper

import woowacourse.shopping.model.RecentProduct
import woowacourse.shopping.model.RecentProductUIModel

fun List<RecentProduct>.toUIModel(): List<RecentProductUIModel> {
    return this.map { it.toUIModel() }
}

fun RecentProduct.toUIModel(): RecentProductUIModel {
    return RecentProductUIModel(
        id = this.id,
        name = this.name,
        price = this.price,
        imageUrl = this.imageUrl
    )
}

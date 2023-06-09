package woowacourse.shopping.mapper

import woowacourse.shopping.model.RecentProduct
import woowacourse.shopping.uimodel.RecentProductUIModel

fun RecentProduct.toUIModel(): RecentProductUIModel {
    return RecentProductUIModel(
        id = this.id,
        name = this.name,
        price = this.price,
        imageUrl = this.imageUrl,
    )
}

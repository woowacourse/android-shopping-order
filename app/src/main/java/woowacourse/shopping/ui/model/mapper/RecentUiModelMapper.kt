package woowacourse.shopping.ui.model.mapper

import woowacourse.shopping.model.RecentItem
import woowacourse.shopping.ui.model.RecentUiModel

fun RecentItem.toUiModel(): RecentUiModel =
    RecentUiModel(
        id = this.productId,
        name = this.name,
        imageUrl = this.imageUrl,
    )

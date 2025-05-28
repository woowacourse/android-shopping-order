package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.entity.HistoryProductEntity
import woowacourse.shopping.domain.model.HistoryProduct

fun HistoryProductEntity.toDomain(): HistoryProduct =
    HistoryProduct(
        productId = productId,
        name = name,
        imageUrl = imageUrl,
        category = category,
    )

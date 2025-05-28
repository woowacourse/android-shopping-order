package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.entity.HistoryProductEntity
import woowacourse.shopping.domain.model.HistoryProduct
import woowacourse.shopping.ui.catalog.adapter.product.CatalogItem.LoadMoreItem.id

fun HistoryProductEntity.toDomain(): HistoryProduct =
    HistoryProduct(
        productId = id,
        name = name,
        imageUrl = imageUrl,
        category = category,
    )

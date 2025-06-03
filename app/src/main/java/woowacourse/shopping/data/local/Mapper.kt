package woowacourse.shopping.data.local

import woowacourse.shopping.data.local.history.HistoryEntity
import woowacourse.shopping.domain.model.History

fun HistoryEntity.toDomain(): History =
    History(
        id = id,
        name = name,
        thumbnailUrl = imageUrl,
    )

fun History.toEntity(): HistoryEntity = HistoryEntity(id = id, name = name, imageUrl = thumbnailUrl)

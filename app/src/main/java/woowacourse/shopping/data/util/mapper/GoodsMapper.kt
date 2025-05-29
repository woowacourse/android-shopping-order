package woowacourse.shopping.data.util.mapper

import woowacourse.shopping.data.goods.GoodsDto
import woowacourse.shopping.domain.model.Goods

fun GoodsDto.toDomain(): Goods =
    Goods(
        id = id,
        name = name,
        price = price,
        thumbnailUrl = imageUrl,
        category = "",
    )

fun List<GoodsDto>.toDomain(): List<Goods> = map { it.toDomain() }

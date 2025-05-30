package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.entity.CartProductEntity
import woowacourse.shopping.product.catalog.ProductUiModel

fun CartProductEntity.toUiModel(): ProductUiModel =
    ProductUiModel(
        uid,
        imageUrl,
        name,
        price,
        quantity,
        0,
    )

fun ProductUiModel.toEntity(): CartProductEntity =
    CartProductEntity(
        id,
        imageUrl,
        name,
        price,
        quantity,
    )

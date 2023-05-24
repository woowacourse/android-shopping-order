package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.ProductEntity
import woowacourse.shopping.presentation.model.ProductModel

fun ProductEntity.toUIModel(): ProductModel = ProductModel(id, name, price, imageUrl)

fun ProductModel.toEntity(): ProductEntity = ProductEntity(id, title, price, imageUrl)

package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.CartRemoteEntity
import woowacourse.shopping.presentation.model.CartModel

fun CartRemoteEntity.toUIModel(): CartModel =
    CartModel(
        id,
        product.toUIModel().apply { count = quantity },
        checked = true,
    )

fun CartModel.toEntity(): CartRemoteEntity = CartRemoteEntity(id, product.count, product.toEntity())

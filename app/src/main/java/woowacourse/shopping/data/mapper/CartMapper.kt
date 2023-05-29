package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.CartRemoteEntity
import woowacourse.shopping.presentation.mapper.toUIModel
import woowacourse.shopping.presentation.model.CartModel
import woowacouse.shopping.model.cart.CartProduct

fun CartRemoteEntity.toUIModel(): CartModel =
    CartModel(
        id,
        product.toUIModel(),
        quantity,
        checked = true,
    )

fun CartProduct.toEntity(): CartRemoteEntity = CartRemoteEntity(id, count, product.toUIModel().toEntity())

fun CartModel.toEntity(): CartRemoteEntity = CartRemoteEntity(id, count, product.toEntity())

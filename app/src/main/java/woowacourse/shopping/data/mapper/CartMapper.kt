package woowacourse.shopping.data.mapper

import com.example.domain.cart.CartProduct
import woowacourse.shopping.data.model.CartRemoteEntity
import woowacourse.shopping.presentation.model.CartModel

fun CartRemoteEntity.toUIModel(): CartModel =
    CartModel(
        id = id,
        product = productEntity.toUIModel(),
        count = quantity,
        checked = true,
    )

fun CartRemoteEntity.toDomain(): CartProduct =
    CartProduct(id, productEntity.toDomain(), quantity, checked = false)

fun CartModel.toEntity(): CartRemoteEntity = CartRemoteEntity(id, count, product.toEntity())

fun CartModel.toDomain(): CartProduct = CartProduct(id, product.toDomain(), count, checked)

fun CartProduct.toUiModel(): CartModel = CartModel(id, product.toUiModel(), count, checked)

fun CartProduct.toEntity(): CartRemoteEntity = CartRemoteEntity(id, count, product.toEntity())

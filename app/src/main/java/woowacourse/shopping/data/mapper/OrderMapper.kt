package woowacourse.shopping.data.mapper

import com.example.domain.cart.CartProducts
import com.example.domain.order.OrderDetail
import woowacourse.shopping.data.model.OrderDetailEntity
import woowacourse.shopping.presentation.model.OrderDetailModel

fun OrderDetailEntity.toUiModel(): OrderDetailModel =
    OrderDetailModel(id, usedPoint, savedPoint, orderedAt, products.map { it.toUIModel() })

fun OrderDetail.toUiModel(): OrderDetailModel =
    OrderDetailModel(id, usedPoint, savedPoint, orderDateTime, products.all.map { it.toUiModel() })

fun OrderDetailEntity.toDomain(): OrderDetail =
    OrderDetail(id, usedPoint, savedPoint, orderedAt, CartProducts(products.map { it.toDomain() }))

fun OrderDetail.toEntity(): OrderDetailEntity =
    OrderDetailEntity(id, usedPoint, savedPoint, orderDateTime, products.all.map { it.toEntity() })

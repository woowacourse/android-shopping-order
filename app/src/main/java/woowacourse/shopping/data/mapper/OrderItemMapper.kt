package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.DataOrderItem
import woowacourse.shopping.domain.OrderItem

fun DataOrderItem.toDomain(): OrderItem =
    OrderItem(count = count.toDomain(), product = product.toDomain())

fun OrderItem.toData(): DataOrderItem =
    DataOrderItem(count = count.toData(), product = product.toData())

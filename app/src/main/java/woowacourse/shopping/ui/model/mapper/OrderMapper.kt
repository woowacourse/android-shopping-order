package woowacourse.shopping.ui.model.mapper

import woowacourse.shopping.domain.Order
import woowacourse.shopping.ui.model.OrderModel
import woowacourse.shopping.ui.model.mapper.CartProductMapper.toDomain
import woowacourse.shopping.ui.model.mapper.CartProductMapper.toView

object OrderMapper: Mapper<Order, OrderModel> {
    override fun Order.toView(): OrderModel {
        return OrderModel(products.map { it.toView() }, originalPrice, usedPoints, finalPrice)
    }

    override fun OrderModel.toDomain(): Order {
        return Order(products.map { it.toDomain() }, originalPrice, usedPoints, finalPrice)
    }
}
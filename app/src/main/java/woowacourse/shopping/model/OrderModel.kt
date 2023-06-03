package woowacourse.shopping.model

data class OrderModel(val orderProducts: OrderCartProductsModel, val usableCash: Int, val afterUseCash: Int, val paymentPrice: Int)

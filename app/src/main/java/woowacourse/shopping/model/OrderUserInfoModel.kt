package woowacourse.shopping.model

data class OrderUserInfoModel(val orderProducts: OrderCartProductsModel, val usableCash: Int, val afterUseCash: Int, val paymentPrice: Int)

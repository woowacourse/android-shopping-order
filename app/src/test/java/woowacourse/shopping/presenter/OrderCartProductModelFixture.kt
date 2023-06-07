package woowacourse.shopping.presenter

import woowacourse.shopping.model.OrderCartProductsModel

object OrderCartProductModelFixture {
    val orderCartProductsModel = OrderCartProductsModel(
        listOf(
            OrderCartProductsModel.OrderCartProductModel(1, "락토핏", 10000, "", 1),
            OrderCartProductsModel.OrderCartProductModel(2, "락토핏", 10000, "", 1),
            OrderCartProductsModel.OrderCartProductModel(3, "락토핏", 10000, "", 1),
        ),
    )
}

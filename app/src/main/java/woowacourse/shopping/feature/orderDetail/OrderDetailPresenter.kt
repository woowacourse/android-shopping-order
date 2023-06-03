package woowacourse.shopping.feature.orderDetail

import woowacourse.shopping.model.OrderDetailProductUiModel
import woowacourse.shopping.model.OrderInfoUiModel
import woowacourse.shopping.model.OrderStateUiModel
import woowacourse.shopping.model.ProductUiModel

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val id: Int
) : OrderDetailContract.Presenter {

    private val orderInfo: OrderInfoUiModel = OrderInfoUiModel(
        0, "2023-03-04", OrderStateUiModel.DELIVERED, 6000, 1000, 100,
        List(5) {
            OrderDetailProductUiModel(
                2,
                ProductUiModel(
                    0,
                    "이름",
                    "https://img.danawa.com/prod_img/500000/711/196/img/5196711_1.jpg?_v=20200724173034",
                    2000, 0
                )
            )
        }
    )

    override fun loadProducts() {
        view.initAdapter(orderInfo.products)
        view.setUpView(orderInfo)
    }
}

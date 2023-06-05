package woowacourse.shopping.view.orderdetail

import woowacourse.shopping.model.data.dto.OrderDetailDTO
import woowacourse.shopping.model.uimodel.OrderDetailUIModel
import woowacourse.shopping.model.uimodel.OrderProductUIModel
import woowacourse.shopping.server.retrofit.MembersService
import woowacourse.shopping.server.retrofit.createResponseCallback

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val service: MembersService
) : OrderDetailContract.Presenter {

    override fun setOrderProducts(id: Long) {
        service.getOrder(id).enqueue(
            createResponseCallback(
                onSuccess = { it ->
                    val orderDetail = it.toUIModel()
                    view.setOrderDetail(orderDetail)
                    view.updateOrderProducts(orderDetail.orderItems)
                },
                onFailure = {
                    throw IllegalStateException("상품 상세를 불러오는데 실패했습니다.")
                }
            )
        )
    }

    private fun OrderDetailDTO.toUIModel() =
        OrderDetailUIModel(
            orderItems.map { OrderProductUIModel(it.name, it.imageUrl, it.count, it.price) },
            originalPrice,
            usedPoints,
            orderPrice
        )
}

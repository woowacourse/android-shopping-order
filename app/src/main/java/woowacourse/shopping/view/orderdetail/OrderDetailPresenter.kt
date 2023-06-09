package woowacourse.shopping.view.orderdetail

import com.shopping.repository.MemberRepository
import woowacourse.shopping.model.uimodel.mapper.toUIModel

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val memberRepository: MemberRepository
) : OrderDetailContract.Presenter {

    override fun setOrderProducts(id: Long) {
        memberRepository.getOrderDetail(
            id,
            onSuccess = { orderDetail ->
                view.updateOrderProducts(orderDetail.toUIModel().orderItems)
                view.setOrderDetail(orderDetail.toUIModel())
            }
        )
    }
}

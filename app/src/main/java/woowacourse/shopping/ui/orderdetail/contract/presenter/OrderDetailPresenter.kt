package woowacourse.shopping.ui.orderdetail.contract.presenter

import com.example.domain.repository.OrderDetailRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.ui.orderdetail.contract.OrderDetailContract

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderDetailRepository: OrderDetailRepository,
) :
    OrderDetailContract.Presenter {
    override fun getOrderDetail(id: Long) {
        orderDetailRepository.getById(id) {
            view.setOrderDetail(it.toUIModel())
        }
    }
}

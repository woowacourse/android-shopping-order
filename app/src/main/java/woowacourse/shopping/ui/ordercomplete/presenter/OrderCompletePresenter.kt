package woowacourse.shopping.ui.ordercomplete.presenter

import android.util.Log
import com.example.domain.model.OrderProduct
import com.example.domain.model.Receipt
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.CartProductUIModel
import woowacourse.shopping.ui.ordercomplete.uimodel.Bill

class OrderCompletePresenter(
    private val view: OrderCompleteContract.View,
    private val orderRepository: OrderRepository,
) : OrderCompleteContract.Presenter {
    override fun getReceipt(orderId: Int) {
        orderRepository.getReceipt(
            orderId,
            onSuccess = { view.setReceipt(it.toUiState()) },
            onFailure = { Log.d("ERROR_OrderCompletePresenter", it.toString()) },
        )
    }

    private fun Receipt.toUiState(): Bill = Bill(
        orderProducts = this.orderProducts.map {
            it.toUiState()
        },
        originPrice = this.originPrice,
        couponName = this.couponName,
        totalPrice = this.totalPrice,
    )

    private fun OrderProduct.toUiState(): CartProductUIModel = CartProductUIModel(
        id = this.product.id,
        quantity = this.quantity,
        product = this.product.toUIModel(),
    )
}

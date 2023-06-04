package woowacourse.shopping.ui.order.presenter

import android.util.Log
import com.example.domain.repository.OrderRepository

class OrderPresenter(
    private val view: OrderContract.View,
    private val orderRepository: OrderRepository,
) : OrderContract.Presenter {
    override fun fetchCoupons() {
        orderRepository.getCoupons(
            onSuccess = {
                view.setCoupons(it)
            },
            onFailure = {
                Log.d("ERROR_OrderPresenter", it.toString())
            },
        )
    }

    override fun postOrder() {
        TODO("Not yet implemented")
    }
}

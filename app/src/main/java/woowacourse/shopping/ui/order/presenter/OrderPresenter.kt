package woowacourse.shopping.ui.order.presenter

import android.util.Log
import com.example.domain.model.Coupon
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.model.CartItemsUIModel

class OrderPresenter(
    private val view: OrderContract.View,
    private val orderRepository: OrderRepository,
) : OrderContract.Presenter {
    override fun fetchCoupons() {
        orderRepository.getCoupons(
            onSuccess = { view.setCoupons(it) },
            onFailure = { Log.d("ERROR_OrderPresenter", it.toString()) },
        )
    }

    override fun postOrder() {
        TODO("Not yet implemented")
    }

    override fun calculateTotal(
        selectedCoupon: Int,
        coupons: List<Coupon>,
        cartItems: CartItemsUIModel,
    ) {
        if (selectedCoupon == 0) {
            view.setTotal(cartItems.totalPrice)
            return
        }

        orderRepository.getAppliedPrice(
            totalPrice = cartItems.totalPrice,
            couponId = selectedCoupon,
            onSuccess = { view.setTotal(it.finalPrice) },
            onFailure = { Log.d("ERROR_OrderPresenter", it.toString()) },
        )
    }
}

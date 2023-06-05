package woowacourse.shopping.ui.order.presenter

import android.util.Log
import com.example.domain.model.Coupon
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.model.CartItemsUIModel

class OrderPresenter(
    private val view: OrderContract.View,
    private val orderRepository: OrderRepository,
) : OrderContract.Presenter {
    private var coupon: Int = NOTHING
    override fun fetchCoupons() {
        orderRepository.getCoupons(
            onSuccess = { view.setCoupons(it) },
            onFailure = { Log.d("ERROR_OrderPresenter", it.toString()) },
        )
    }

    override fun postOrder(orderItems: List<Int>) {
        if (coupon == NOTHING) {
            orderRepository.postOrderWithoutCoupon(
                orderItems,
                onSuccess = { view.fetchOrderId(it.id) },
                onFailure = { Log.d("ERROR_OrderPresenter", it.toString()) },
            )
            return
        }

        orderRepository.postOrderWithCoupon(
            orderItems,
            coupon,
            onSuccess = { view.fetchOrderId(it.id) },
            onFailure = { Log.d("ERROR_OrderPresenter", it.toString()) },
        )
    }

    override fun calculateTotal(
        selectedCoupon: Int,
        coupons: List<Coupon>,
        cartItems: CartItemsUIModel,
    ) {
        if (selectedCoupon == NOTHING) {
            view.setTotal(cartItems.totalPrice)
            return
        }

        coupon = selectedCoupon

        orderRepository.getAppliedPrice(
            totalPrice = cartItems.totalPrice,
            couponId = selectedCoupon,
            onSuccess = { view.setTotal(it.finalPrice) },
            onFailure = { Log.d("ERROR_OrderPresenter", it.toString()) },
        )
    }

    companion object {
        private const val NOTHING = 0
    }
}

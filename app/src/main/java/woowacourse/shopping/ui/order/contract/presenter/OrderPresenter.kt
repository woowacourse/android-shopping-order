package woowacourse.shopping.ui.order.contract.presenter

import android.util.Log
import com.example.domain.repository.CouponRepository
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.CartItemsUIModel
import woowacourse.shopping.model.OrderUIModel
import woowacourse.shopping.ui.order.contract.OrderContract

class OrderPresenter(
    private val cartItems: CartItemsUIModel,
    val view: OrderContract.View,
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) :
    OrderContract.Presenter {

    init {
        getOrder()
        getOriginalPrice()
        getCoupons()
    }

    private lateinit var order: OrderUIModel

    override fun getOrder() {
        view.setUpOrder(cartItems.cartProducts)
    }

    override fun getOriginalPrice() {
        view.setPrice(cartItems.totalPrice)
    }

    override fun getCoupons() {
        val list = mutableListOf("사용 안함")
        couponRepository.getCoupons().getOrNull()?.let {
            Log.d("getCoupons", it.toString())
            view.setCoupons(list.plus(it.map { coupon -> coupon.name }))
        }
    }

    override fun getTotalPrice(couponName: String) {
        order = applyCoupon(couponName)

        view.setPrice(order.totalPrice)
    }

    override fun navigateToOrderDetail() {
        order = applyCoupon("")

        view.navigateToOrderDetail(order.id)
    }

    private fun applyCoupon(couponName: String): OrderUIModel {
        val coupon =
            couponRepository.getCoupons().getOrNull()?.find { coupon -> coupon.name == couponName }
        Log.d("applyCoupon", coupon.toString())
        val cartItemsIds = cartItems.cartProducts.map { cartProduct -> cartProduct.id }
        Log.d("applyCoupon", cartItemsIds.toString())
        return if (coupon != null) {
            orderRepository.insertOrderWithCoupon(cartItemsIds, coupon.id).getOrNull()?.toUIModel()
                ?: throw IllegalArgumentException("쿠폰 적용 실패")
        } else {
            orderRepository.insertOrderWithoutCoupon(cartItemsIds).getOrNull()?.toUIModel()
                ?: throw IllegalArgumentException("쿠폰 적용 실패")
        }
    }
}

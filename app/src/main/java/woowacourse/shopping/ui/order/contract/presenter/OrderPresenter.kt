package woowacourse.shopping.ui.order.contract.presenter

import com.example.domain.repository.CouponRepository
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.model.CartItemsUIModel
import woowacourse.shopping.ui.order.contract.OrderContract

class OrderPresenter(
    private val cartItems: CartItemsUIModel,
    val view: OrderContract.View,
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) :
    OrderContract.Presenter {

    private var coupon: Int = NOTHING

    init {
        getOrder()
        getOriginalPrice()
        getCoupons()
    }

    override fun getOrder() {
        view.setUpOrder(cartItems.cartProducts)
    }

    override fun getOriginalPrice() {
        view.setPrice(cartItems.totalPrice)
    }

    override fun getCoupons() {
        val list = mutableListOf("사용 안함")
        couponRepository.getCoupons {
            view.setCoupons(list.plus(it.map { coupon -> coupon.name }))
        }
    }

    override fun getTotalPrice(couponId: Int) {
        if (coupon == NOTHING) {
            view.setPrice(cartItems.totalPrice)
            return
        }
        coupon = couponId

        couponRepository.getPriceWithCoupon(cartItems.totalPrice, coupon.toLong()) {
            view.setPrice(it.totalPrice)
        }
    }

    override fun navigateToOrderDetail(couponId: Int) {
        val cartItemsIds = cartItems.cartProducts.map { cartProduct -> cartProduct.id }
        if (coupon == NOTHING) {
            orderRepository.insertOrderWithoutCoupon(cartItemsIds) { order ->
                view.navigateToOrderDetail(order.id)
            }
            return
        }
        orderRepository.insertOrderWithCoupon(cartItemsIds, coupon.toLong()) {
            view.navigateToOrderDetail(it.id)
        }
    }

    companion object {
        private const val NOTHING = 0
    }
}

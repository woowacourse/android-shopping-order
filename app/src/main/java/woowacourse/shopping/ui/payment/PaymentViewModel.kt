package woowacourse.shopping.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.common.MutableSingleLiveData
import woowacourse.shopping.common.SingleLiveData
import woowacourse.shopping.common.UniversalViewModelFactory
import woowacourse.shopping.data.cart.remote.DefaultCartItemRepository
import woowacourse.shopping.data.coupon.remote.CouponRemoteRepository
import woowacourse.shopping.data.order.remote.OrderRemoteRepository
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.coupon.Order
import woowacourse.shopping.domain.model.coupon.Order.Companion.SHIPPING_FEE
import woowacourse.shopping.domain.repository.cart.CartItemRepository
import woowacourse.shopping.domain.repository.coupon.CouponRepository
import woowacourse.shopping.domain.repository.order.OrderRepository
import woowacourse.shopping.ui.ResponseHandler.handleResponseResult
import woowacourse.shopping.ui.model.CouponUiModel
import woowacourse.shopping.ui.model.OrderInformation

class PaymentViewModel(
    private val orderInformation: OrderInformation,
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
    private val cartItemRepository: CartItemRepository,
): ViewModel(), OnCouponClickListener {
    private var order: Order = Order(emptyList())
    private var selectedCartItems = listOf<CartItem>()

    private val _couponsUiModel = MutableLiveData<List<CouponUiModel>>(emptyList())
    val couponsUiModel: LiveData<List<CouponUiModel>> get() = _couponsUiModel

    private val _isPaymentSuccess: MutableSingleLiveData<Boolean> = MutableSingleLiveData(false)
    val isPaymentSuccess: SingleLiveData<Boolean> get() = _isPaymentSuccess

    private val _orderAmount = MutableLiveData(orderInformation.orderAmount)
    val orderAmount: LiveData<Int> get() = _orderAmount

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _discountAmount = MutableLiveData<Int>(0)
    val discountAmount: LiveData<Int> get() = _discountAmount

    private val _shippingFee = MutableLiveData<Int>(SHIPPING_FEE)
    val shippingFee: LiveData<Int> get() = _shippingFee

    private val _totalPaymentAmount = MutableLiveData(orderInformation.orderAmount)
    val totalPaymentAmount: LiveData<Int> get() = _totalPaymentAmount

    fun createOrder() {
        viewModelScope.launch {
            orderRepository.orderCartItems(orderInformation.cartItemIds)
        }
        _isPaymentSuccess.setValue(true)
    }

    fun loadCoupons() {
        viewModelScope.launch {
            handleResponseResult(cartItemRepository.loadCartItems(), _errorMessage) { cartItems ->
                selectedCartItems = cartItems.filter { it.id in orderInformation.cartItemIds }
            }
            handleResponseResult(couponRepository.loadCoupons(), _errorMessage) { coupons ->
                order = Order(coupons)
                val applicableCoupon = order.findAvailableCoupons(selectedCartItems)
                _couponsUiModel.value = applicableCoupon.map { CouponUiModel.toUiModel(it) }
            }
            _totalPaymentAmount.value = orderInformation.orderAmount + SHIPPING_FEE
        }
    }

    override fun onCouponSelected(couponId: Long) {
        val couponsUiModel = couponsUiModel.value ?: return
        _couponsUiModel.value = couponsUiModel.map {
            if (it.id == couponId) { it.copy(checked = !it.checked) }
            else it.copy(checked = false)
        }

        val isChecked = !couponsUiModel.first { it.id == couponId }.checked
        _discountAmount.value = order.calculateDiscountAmount(couponId, selectedCartItems, isChecked)
        _shippingFee.value = order.calculateDeliveryFee(couponId, isChecked)

        _totalPaymentAmount.value = order.calculateTotalAmount(couponId, selectedCartItems, isChecked)
    }

    companion object {
        fun factory(
           orderInformation: OrderInformation,
       ): UniversalViewModelFactory {
           return UniversalViewModelFactory {
               PaymentViewModel(
                   orderInformation,
                   orderRepository = OrderRemoteRepository(
                       ShoppingApp.orderSource,
                       ShoppingApp.productSource,
                       ShoppingApp.historySource,
                       ShoppingApp.cartSource,
                   ),
                   couponRepository = CouponRemoteRepository(
                       ShoppingApp.couponSource,
                   ),
                   cartItemRepository = DefaultCartItemRepository(
                       ShoppingApp.cartSource
                   ),
               )
           }
       }
    }
}
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
import woowacourse.shopping.data.coupon.remote.CouponRemoteRepository
import woowacourse.shopping.data.order.remote.OrderRemoteRepository
import woowacourse.shopping.domain.repository.coupon.CouponRepository
import woowacourse.shopping.domain.repository.order.OrderRepository
import woowacourse.shopping.ui.ResponseHandler.handleResponseResult
import woowacourse.shopping.ui.mapper.CouponMapper
import woowacourse.shopping.ui.model.CouponUiModel
import woowacourse.shopping.ui.model.OrderInformation

class PaymentViewModel(
    private val orderInformation: OrderInformation,
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
): ViewModel(), OnCouponClickListener {
    private val _couponsUiModel = MutableLiveData<List<CouponUiModel>>(emptyList())
    val couponsUiModel: LiveData<List<CouponUiModel>> get() = _couponsUiModel

    private val _isPaymentSuccess: MutableSingleLiveData<Boolean> = MutableSingleLiveData(false)
    val isPaymentSuccess: SingleLiveData<Boolean> get() = _isPaymentSuccess

    private val _orderAmount = MutableLiveData(0)
    val orderAmount: LiveData<Int> get() = _orderAmount

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _discountAmount = MutableLiveData<Int>(0)
    val discountAmount: LiveData<Int> get() = _discountAmount

    private val _shippingFee = MutableLiveData<Int>(0)
    val shippingFee: LiveData<Int> get() = _shippingFee

    private val _totalPaymentAmount = MutableLiveData<Int>(0)
    val totalPaymentAmount: LiveData<Int> get() = _totalPaymentAmount

    fun createOrder() {
        viewModelScope.launch {
            handleResponseResult(
                responseResult = orderRepository.orderCartItems(orderInformation.getCartItemIds()),
                onSuccess = { _isPaymentSuccess.setValue(true) },
                onError = { message -> _errorMessage.value = message },
            )
        }
    }

    fun loadCoupons() {
        viewModelScope.launch {
            handleResponseResult(
                responseResult = couponRepository.loadCoupons(orderInformation),
                onSuccess = { coupons ->
                    _couponsUiModel.value = coupons.map { CouponMapper.toUiModel(it) }
                },
                onError = { message ->
                    _errorMessage.value = message
                },
            )
        }
    }

    fun loadInitialPaymentInformation() {
        _orderAmount.value = orderInformation.calculateOrderAmount()
        _shippingFee.value = orderInformation.determineShippingFee(isSelected = false)
        _totalPaymentAmount.value = orderInformation.calculateDefaultTotalAmount()
    }

    override fun onCouponSelected(couponId: Long) {
        val couponsUiModel = couponsUiModel.value ?: return
        _couponsUiModel.value = couponsUiModel.map {
            if (it.id == couponId) { it.copy(checked = !it.checked) }
            else it.copy(checked = false)
        }

        val isChecked = !couponsUiModel.first { it.id == couponId }.checked
        _discountAmount.value = couponRepository.calculateDiscountAmount(orderInformation, couponId, isChecked)
        _shippingFee.value = couponRepository.calculateShippingFee(orderInformation, couponId, isChecked)
        _totalPaymentAmount.value = couponRepository.calculateTotalAmount(orderInformation, couponId, isChecked)
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
               )
           }
       }
    }
}
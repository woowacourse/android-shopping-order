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
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.coupon.CouponRepository
import woowacourse.shopping.domain.repository.order.OrderRepository
import woowacourse.shopping.ui.ResponseHandler.handleResponseResult
import woowacourse.shopping.ui.model.CouponUiModel
import woowacourse.shopping.ui.model.OrderInformation

class PaymentViewModel(
    private val orderInformation: OrderInformation,
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
): ViewModel() {
    private val _coupons = MutableLiveData<List<Coupon>>()

    private val _couponsUiModel = MutableLiveData<List<CouponUiModel>>(emptyList())
    val couponsUiModel: LiveData<List<CouponUiModel>> get() = _couponsUiModel

    private val _isPaymentSuccess: MutableSingleLiveData<Boolean> = MutableSingleLiveData(false)

    val isPaymentSuccess: SingleLiveData<Boolean> get() = _isPaymentSuccess
    private val _orderAmount = MutableLiveData(orderInformation.orderAmount)

    val orderAmount: LiveData<Int> get() = _orderAmount

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun createOrder() {
        viewModelScope.launch {
            orderRepository.orderCartItems(orderInformation.cartItemIds)
        }
        _isPaymentSuccess.setValue(true)
    }

    fun loadCoupons() {
        viewModelScope.launch {
            handleResponseResult(couponRepository.loadCoupons(), _errorMessage) { coupons ->
                _coupons.value = coupons
                _couponsUiModel.value = coupons.map { CouponUiModel.toUiModel(it) }
            }
        }
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
                   )
               )
           }
       }
    }
}
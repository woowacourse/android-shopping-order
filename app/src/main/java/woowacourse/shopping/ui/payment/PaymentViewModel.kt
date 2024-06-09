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
import woowacourse.shopping.data.order.remote.OrderRemoteRepository
import woowacourse.shopping.domain.repository.order.OrderRepository
import woowacourse.shopping.ui.model.OrderInformation

class PaymentViewModel(
    private val orderInformation: OrderInformation,
    private val orderRepository: OrderRepository,
): ViewModel() {
    private val _isPaymentSuccess: MutableSingleLiveData<Boolean> = MutableSingleLiveData(false)
    val isPaymentSuccess: SingleLiveData<Boolean> get() = _isPaymentSuccess

    private val _orderAmount = MutableLiveData(orderInformation.orderAmount)
    val orderAmount: LiveData<Int> get() = _orderAmount

    fun createOrder() {
        viewModelScope.launch {
            orderRepository.orderCartItems(orderInformation.cartItemIds)
        }
        _isPaymentSuccess.setValue(true)
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
                   )
               )
           }
       }
    }
}
package woowacourse.shopping.presentation.ui.shoppingcart.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.presentation.base.BaseViewModel
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.base.Event
import woowacourse.shopping.presentation.base.MessageProvider
import woowacourse.shopping.presentation.base.emit
import woowacourse.shopping.presentation.ui.shoppingcart.payment.PaymentFragment.Companion.PUT_EXTRA_CART_IDS_KEY

class PaymentViewModel(
    savedStateHandle: SavedStateHandle,
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : BaseViewModel() {
    private val _uiState: MutableLiveData<CouponUiState> = MutableLiveData(CouponUiState())
    val uiState: LiveData<CouponUiState> get() = _uiState

    private val _navigateAction: MutableLiveData<Event<PaymentNavigateAction>> =
        MutableLiveData(null)
    val navigateAction: LiveData<Event<PaymentNavigateAction>> get() = _navigateAction

    init {
        val orderCarts = requireNotNull(savedStateHandle.get<Array<Cart>>(PUT_EXTRA_CART_IDS_KEY))

        loadOrderCarats(orderCarts)
        getCoupons()
    }

    override fun retry() {}

    fun getCoupons() {
        viewModelScope.launch {
            couponRepository.getCoupons()
                .onSuccess {
                    hideError()

                    _uiState.value?.let { state ->
                        _uiState.value =
                            state.copy(
                                coupons = it,
                            )
                    }
                }.onFailure { e ->
                    showError(e)
                    showMessage(MessageProvider.DefaultErrorMessage)
                }

            hideLoading()
        }
    }

    fun loadOrderCarats(orderCarts: Array<Cart>) {
        _uiState.value?.let { state ->
            _uiState.value =
                state.copy(orderCarts = orderCarts.associateBy { cart -> cart.id }.toMutableMap())
        }
    }

    fun makeAPayment() {
        viewModelScope.launch {
            uiState.value?.let { state ->
                orderRepository.insertOrderByIds(state.orderCarts.keys.toList())
                    .onSuccess {
                        hideError()
                        _navigateAction.emit(PaymentNavigateAction.NavigateToProductList)
                    }.onFailure { e ->
                        showError(e)
                    }
            }
        }
    }

    companion object {
        fun factory(
            couponRepository: CouponRepository,
            orderRepository: OrderRepository,
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory { extras ->
                PaymentViewModel(
                    savedStateHandle = extras.createSavedStateHandle(),
                    couponRepository,
                    orderRepository,
                )
            }
        }
    }
}

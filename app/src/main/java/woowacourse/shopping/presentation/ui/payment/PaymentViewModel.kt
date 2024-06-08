package woowacourse.shopping.presentation.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.presentation.base.BaseViewModel
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.base.Event
import woowacourse.shopping.presentation.base.emit
import woowacourse.shopping.presentation.model.CartsWrapper
import woowacourse.shopping.presentation.model.toDomain

class PaymentViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : BaseViewModel(), PaymentActionHandler {
    private val _uiState: MutableLiveData<PaymentUiState> = MutableLiveData(PaymentUiState())
    val uiState: LiveData<PaymentUiState> get() = _uiState

    private val _navigateAction: MutableLiveData<Event<PaymentNavigateAction>> =
        MutableLiveData(null)
    val navigateAction: LiveData<Event<PaymentNavigateAction>> get() = _navigateAction

    init {
        initOrderCarts()
        loadCoupons()
    }

    override fun retry() {
        initOrderCarts()
        loadCoupons()
    }

    private fun initOrderCarts() {
        val cartsWrapper =
            requireNotNull(savedStateHandle.get<CartsWrapper>(PaymentActivity.PUT_EXTRA_CARTS_WRAPPER_KEY))

        val state = uiState.value ?: return
        _uiState.value = state.copy(orderCarts = cartsWrapper.cartUiModels)
    }

    private fun loadCoupons() {
        launch {
            couponRepository.getCoupons().onSuccess { coupons ->
                hideError()
                val state = uiState.value ?: return@launch
                val couponsState =
                    coupons.filter { it.isValidCoupon(state.orderCarts.map { cartUiModel -> cartUiModel.toDomain() }) }
                _uiState.postValue(state.copy(couponsState = couponsState))
            }.onFailure { e ->
                showError(e)
            }
        }
    }

    override fun toggleCoupon(couponState: CouponState) {
        val state = uiState.value ?: return
        val updateCouponState =
            state.couponsState.map { couponItem ->
                if (couponItem.coupon == couponState.coupon) {
                    couponItem.copy(checked = !couponItem.coupon.checked)
                } else {
                    couponItem.copy(checked = false)
                }
            }

        _uiState.value = state.copy(couponsState = updateCouponState)
    }

    fun payment() {
        launch {
            val state = uiState.value ?: return@launch
            orderRepository.insertOrder(state.orderCarts.map { it.id }).onSuccess {
                hideError()
                showMessage(PaymentMessage.PaymentSuccessMessage)
                _navigateAction.emit(PaymentNavigateAction.NavigateToProductList)
            }.onFailure { e ->
                showError(e)
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
                    couponRepository = couponRepository,
                    orderRepository = orderRepository,
                )
            }
        }
    }
}

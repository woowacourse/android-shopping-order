package woowacourse.shopping.presentation.ui.shoppingcart.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.coupons.BOGO
import woowacourse.shopping.domain.model.coupons.Coupon
import woowacourse.shopping.domain.model.coupons.FIXED5000
import woowacourse.shopping.domain.model.coupons.FREESHIPPING
import woowacourse.shopping.domain.model.coupons.MIRACLESALE
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
) : BaseViewModel(), CouponListActionHandler {
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
                state.copy(orderCarts = orderCarts.toList())
        }
    }

    fun makeAPayment() {
        viewModelScope.launch {
            uiState.value?.let { state ->
                orderRepository.insertOrderByIds(state.orderCarts.map { it.id })
                    .onSuccess {
                        hideError()
                        _navigateAction.emit(PaymentNavigateAction.NavigateToProductList)
                    }.onFailure { e ->
                        showError(e)
                    }
            }
        }
    }

    override fun selectCoupon(coupon: Coupon) {
        val carts = uiState.value?.orderCarts?.toList() ?: throw IllegalArgumentException()

        _uiState.value =
            _uiState.value?.copy(
                discountPrice =
                    -when (coupon) {
                        is FIXED5000 -> coupon.calculateDiscountRate(carts)
                        is BOGO -> coupon.calculateDiscountRate(carts)
                        is FREESHIPPING -> coupon.calculateDiscountRate(carts)
                        is MIRACLESALE -> coupon.calculateDiscountRate(carts)
                    },
            )
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

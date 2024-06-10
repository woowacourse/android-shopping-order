package woowacourse.shopping.presentation.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import kotlinx.coroutines.delay
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.presentation.base.BaseViewModel
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.base.Event
import woowacourse.shopping.presentation.base.LoadingProvider
import woowacourse.shopping.presentation.base.emit
import woowacourse.shopping.presentation.model.CartsWrapper
import woowacourse.shopping.presentation.model.CouponUiModel
import woowacourse.shopping.presentation.model.toDomain
import woowacourse.shopping.presentation.model.toPresentation

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
            showLoading(loadingProvider = LoadingProvider.SKELETON_LOADING)
            delay(1000) // TODO 스켈레톤 UI를 보여주기 위한 sleep..zzz
            val coupons = couponRepository.getCoupons().getOrThrow()
            throw IllegalArgumentException()

            hideError()
            val state = uiState.value ?: return@launch
            val couponUiModel = coupons.map { it.toPresentation() }
            val validCoupons =
                couponUiModel.filter { it.couponCondition.isValid(state.orderCarts.map { cartUiModel -> cartUiModel.toDomain() }) }
            _uiState.postValue(state.copy(coupons = validCoupons))
            delay(1000) // TODO 스켈레톤 UI를 보여주기 위한 sleep..zzz
            hideLoading()
        }
    }

    override fun toggleCoupon(couponUiModel: CouponUiModel) {
        val state = uiState.value ?: return
        val updateCoupon =
            state.coupons.map { couponItem ->
                if (couponItem == couponUiModel) {
                    couponItem.copy(checked = !couponItem.checked)
                } else {
                    couponItem.copy(checked = false)
                }
            }

        _uiState.value = state.copy(coupons = updateCoupon)
    }

    fun payment() {
        launch {
            val state = uiState.value ?: return@launch
            orderRepository.insertOrder(state.orderCarts.map { it.id }).getOrThrow()
            hideError()
            showMessage(PaymentMessage.PaymentSuccessMessage)
            _navigateAction.emit(PaymentNavigateAction.NavigateToProductList)
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

package woowacourse.shopping.presentation.order.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber
import woowacourse.shopping.domain.usecase.order.LoadAvailableDiscountCouponsUseCase
import woowacourse.shopping.domain.usecase.order.LoadShippingFeeUseCase
import woowacourse.shopping.domain.usecase.order.OrderCartProductsUseCase
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.cart.CartProductUi
import woowacourse.shopping.presentation.cart.toCart
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData

class PaymentViewModel(
    orders: List<CartProductUi>,
    private val loadShippingFeeUseCase: LoadShippingFeeUseCase,
    private val orderCartProductsUseCase: OrderCartProductsUseCase,
    private val loadAvailableDiscountCouponsUseCase: LoadAvailableDiscountCouponsUseCase
) : ViewModel(), CouponClickListener {

    private val _uiState = MutableLiveData<PaymentUiState>(PaymentUiState())
    val uiState: LiveData<PaymentUiState> get() = _uiState
    private val _updateCartEvent = MutableSingleLiveData<Unit>()
    val updateCartEvent: SingleLiveData<Unit> get() = _updateCartEvent
    private val _showOrderDialogEvent = MutableSingleLiveData<Unit>()
    val showOrderDialogEvent: SingleLiveData<Unit> get() = _showOrderDialogEvent
    private val _finishOrderEvent = MutableSingleLiveData<Unit>()
    val finishOrderEvent: SingleLiveData<Unit> get() = _finishOrderEvent
    private val _errorEvent = MutableSingleLiveData<PaymentErrorEvent>()
    val errorEvent: SingleLiveData<PaymentErrorEvent> get() = _errorEvent

    init {
        val cart = orders.toCart()
        _uiState.value =
            _uiState.value?.copy(cart = cart, paymentPrice = cart.totalPrice())
        loadCoupons(orders.map { it.product.id })
    }

    fun startOrder() {
        _showOrderDialogEvent.setValue(Unit)
    }

    override fun toggleCoupon(couponId: Long) {
        val uiState = _uiState.value ?: return
        val selectedCoupon = uiState.selectedCoupon
        if (selectedCoupon?.id == couponId) {
            unSelectAllCoupons()
        } else {
            unSelectAllCoupons()
            selectCoupon(couponId)
        }
    }

    fun orderProducts() {
        viewModelScope.launch {
            val uiState = _uiState.value ?: return@launch
            orderCartProductsUseCase(uiState.totalOrderIds)
                .onSuccess {
                    _updateCartEvent.setValue(Unit)
                    _finishOrderEvent.setValue(Unit)
                }.onFailure {
                    Timber.e(it)
                    _errorEvent.setValue(PaymentErrorEvent.OrderProducts)
                }
        }
    }

    private fun loadCoupons(productIds: List<Long>) {
        viewModelScope.launch {
            loadAvailableDiscountCouponsUseCase(productIds).onSuccess { coupons ->
                _uiState.value =
                    _uiState.value?.copy(coupons = coupons, couponUis = coupons.toUiModel())
            }.onFailure {
                Timber.e(it)
                _errorEvent.setValue(PaymentErrorEvent.LoadCoupons)
            }
        }
    }

    private fun selectCoupon(couponId: Long) {
        val uiState = _uiState.value ?: return
        val discountResult =
            uiState.coupons.findCouponById(couponId)
                ?.discount(uiState.cart, loadShippingFeeUseCase())
                ?: return
        _uiState.value = uiState.copy(
            couponUis = uiState.couponUis.map {
                it.copy(isSelected = it.id == couponId)
            },
            shippingFee = discountResult.shippingFee,
            discountPrice = discountResult.discountPrice,
            paymentPrice = discountResult.paymentPrice
        )
    }

    private fun unSelectAllCoupons() {
        val uiState = _uiState.value ?: return
        _uiState.value =
            uiState.copy(
                couponUis = uiState.couponUis.map { it.copy(isSelected = false) },
                shippingFee = 0,
                discountPrice = 0,
                paymentPrice = uiState.totalPrice
            )
    }

    companion object {
        fun factory(
            orders: List<CartProductUi>,
            loadShippingFeeUseCase: LoadShippingFeeUseCase,
            orderCartProductsUseCase: OrderCartProductsUseCase,
            loadAvailableDiscountCouponsUseCase: LoadAvailableDiscountCouponsUseCase
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory {
                PaymentViewModel(
                    orders,
                    loadShippingFeeUseCase,
                    orderCartProductsUseCase,
                    loadAvailableDiscountCouponsUseCase
                )
            }
        }
    }
}

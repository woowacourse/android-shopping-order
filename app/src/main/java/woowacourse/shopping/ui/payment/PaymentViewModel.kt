package woowacourse.shopping.ui.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.model.Products.Companion.EMPTY_PRODUCTS
import woowacourse.shopping.domain.usecase.CalculateCouponDiscountUseCase
import woowacourse.shopping.domain.usecase.CalculatePaymentAmountByCouponUseCase
import woowacourse.shopping.domain.usecase.CalculatePaymentAmountByCouponUseCase.Companion.DEFAULT_SHIPPING_FEE
import woowacourse.shopping.domain.usecase.GetCouponsUseCase
import woowacourse.shopping.domain.usecase.IsFreeShippingCouponUseCase
import woowacourse.shopping.ui.payment.adapter.CouponUiModel.Companion.toUiModel
import woowacourse.shopping.util.Event

class PaymentViewModel(
    private val getCouponsUseCase: GetCouponsUseCase,
    private val calculatePaymentAmountByCouponUseCase: CalculatePaymentAmountByCouponUseCase,
    private val isFreeShippingCouponUseCase: IsFreeShippingCouponUseCase,
    private val calculateCouponDiscountUseCase: CalculateCouponDiscountUseCase,
) : ViewModel() {
    private val _uiState: MutableLiveData<PaymentUiState> =
        MutableLiveData<PaymentUiState>(PaymentUiState())
    val uiState: LiveData<PaymentUiState> get() = _uiState

    private val _onPayClick: MutableLiveData<Event<Unit>> = MutableLiveData<Event<Unit>>()
    val onPayClick: LiveData<Event<Unit>> get() = _onPayClick

    fun loadProductsInfo(products: Products) {
        updateUiState {
            it.copy(
                selectedProducts = products.getOrderedProducts(),
                totalPaymentAmount = products.getSelectedCartProductsPrice() + DEFAULT_SHIPPING_FEE,
            )
        }
        loadCoupons()
        val orderPrice = products.getSelectedCartProductsPrice()
        loadInitOrderPrice(orderPrice)
    }

    fun selectCoupon(selectedCouponId: Long) {
        val currentSelectedCouponId =
            _uiState.value
                ?.coupons
                ?.find { it.isSelected }
                ?.id

        val newSelectedCouponId =
            if (currentSelectedCouponId == selectedCouponId) {
                NO_SELECTED_COUPON_ID
            } else {
                selectedCouponId
            }

        val updatedCoupons =
            _uiState.value?.coupons?.map {
                it.copy(isSelected = newSelectedCouponId != NO_SELECTED_COUPON_ID && it.id == newSelectedCouponId)
            } ?: return
        updateUiState { it.copy(coupons = updatedCoupons) }

        val selectedProducts = _uiState.value?.selectedProducts ?: EMPTY_PRODUCTS
        if (newSelectedCouponId == NO_SELECTED_COUPON_ID) {
            updateUiState {
                it.copy(
                    totalPaymentAmount = selectedProducts.getSelectedCartProductsPrice() + DEFAULT_SHIPPING_FEE,
                    deliveryPrice = DEFAULT_SHIPPING_FEE,
                    couponDiscount = NO_FIXED_DISCOUNT,
                )
            }
        } else {
            loadTotalPaymentAmount(newSelectedCouponId, selectedProducts)
            loadDeliveryPrice(newSelectedCouponId)
            loadCouponDiscount(newSelectedCouponId, selectedProducts)
        }
    }

    fun onPayClick() {
        _onPayClick.value = Event(Unit)
    }

    private fun loadTotalPaymentAmount(
        selectedCouponId: Long,
        selectedProducts: Products,
    ) {
        val totalPaymentAmount =
            calculatePaymentAmountByCouponUseCase(
                selectedCouponId,
                selectedProducts,
            )
        updateUiState { it.copy(totalPaymentAmount = totalPaymentAmount) }
    }

    private fun loadCouponDiscount(
        selectedCouponId: Long,
        products: Products,
    ) {
        val newCouponDiscount =
            calculateCouponDiscountUseCase(selectedCouponId, products)
        updateUiState { it.copy(couponDiscount = newCouponDiscount) }
    }

    private fun loadDeliveryPrice(selectedCouponId: Long) {
        val newDeliveryPrice =
            if (isFreeShippingCouponUseCase(selectedCouponId)) FREE_SHIPPING_FEE else DEFAULT_SHIPPING_FEE
        updateUiState { it.copy(deliveryPrice = newDeliveryPrice) }
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            getCouponsUseCase(uiState.value?.selectedProducts ?: EMPTY_PRODUCTS)
                .onSuccess { coupons ->
                    updateUiState { it.copy(coupons = coupons.map { coupon -> coupon.toUiModel() }) }
                }.onFailure {
                    Log.e("PaymentViewModel", it.message.toString())
                }
        }
    }

    private fun loadInitOrderPrice(orderPrice: Int) {
        updateUiState { it.copy(orderPrice = orderPrice) }
    }

    private fun updateUiState(update: (PaymentUiState) -> PaymentUiState) {
        val current = _uiState.value ?: return
        _uiState.value = update(current)
    }

    companion object {
        private const val FREE_SHIPPING_FEE: Int = 0
        private const val NO_FIXED_DISCOUNT = 0
        private const val NO_SELECTED_COUPON_ID = -1L

        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val application = checkNotNull(extras[APPLICATION_KEY]) as ShoppingApp

                    return PaymentViewModel(
                        getCouponsUseCase = application.getCouponsUseCase,
                        calculatePaymentAmountByCouponUseCase = application.calculatePaymentAmountByCouponUseCase,
                        isFreeShippingCouponUseCase = application.isFreeShippingCouponUseCase,
                        calculateCouponDiscountUseCase = application.calculateCouponDiscountUseCase,
                    ) as T
                }
            }
    }
}

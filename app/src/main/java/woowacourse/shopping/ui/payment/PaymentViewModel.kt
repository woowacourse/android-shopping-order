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
import woowacourse.shopping.domain.usecase.CalculatePaymentAmountByCouponUseCase
import woowacourse.shopping.domain.usecase.CalculatePaymentAmountByCouponUseCase.Companion.DEFAULT_SHIPPING_FEE
import woowacourse.shopping.domain.usecase.GetCouponsUseCase
import woowacourse.shopping.domain.usecase.IsFixedDiscountUseCase
import woowacourse.shopping.domain.usecase.IsFreeShippingCouponUseCase
import woowacourse.shopping.ui.payment.adapter.CouponUiModel.Companion.toUiModel

class PaymentViewModel(
    private val getCouponsUseCase: GetCouponsUseCase,
    private val calculatePaymentAmountByCouponUseCase: CalculatePaymentAmountByCouponUseCase,
    private val isFreeShippingCouponUseCase: IsFreeShippingCouponUseCase,
    private val isFixedDiscountUseCase: IsFixedDiscountUseCase,
) : ViewModel() {
    private val _uiState: MutableLiveData<PaymentUiState> =
        MutableLiveData<PaymentUiState>(PaymentUiState())
    val uiState: LiveData<PaymentUiState> get() = _uiState

    fun loadProductsInfo(products: Products) {
        updateUiState {
            it.copy(
                selectedProducts = products.getOrderedProducts(),
                totalPaymentAmount = products.getSelectedCartProductsPrice() + products.getSelectedCartRecommendProductsPrice(),
            )
        }
        loadCoupons()
        val orderPrice = products.getSelectedCartProductsPrice()
        loadInitOrderPrice(orderPrice)
    }

    fun selectCoupon(selectedCouponId: Long) {
        val updatedCoupons =
            _uiState.value?.coupons?.map { it.copy(isSelected = it.id == selectedCouponId) }
                ?: return
        updateUiState { it.copy(coupons = updatedCoupons) }
        val totalPaymentAmount =
            calculatePaymentAmountByCouponUseCase(
                selectedCouponId,
                _uiState.value?.selectedProducts ?: EMPTY_PRODUCTS,
            )
                ?: return
        updateUiState { it.copy(totalPaymentAmount = totalPaymentAmount) }

        loadDeliveryPrice(selectedCouponId)
        loadCouponDiscount(selectedCouponId)
    }

    private fun loadCouponDiscount(selectedCouponId: Long) {
        val newCouponDiscount =
            if (isFixedDiscountUseCase(selectedCouponId)) FIXED_DISCOUNT_AMOUNT else NO_FIXED_DISCOUNT
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
        private const val FIXED_DISCOUNT_AMOUNT = -5000
        private const val NO_FIXED_DISCOUNT = 0

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
                        isFixedDiscountUseCase = application.isFixedDiscountUseCase,
                    ) as T
                }
            }
    }
}

package woowacourse.shopping.ui.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.di.UseCaseInjection.getCatalogProductsByProductIdsUseCase
import woowacourse.shopping.di.UseCaseInjection.getCouponsUseCase
import woowacourse.shopping.di.UseCaseInjection.orderProductsUseCase
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.usecase.GetCatalogProductsByProductIdsUseCase
import woowacourse.shopping.domain.usecase.GetCouponsUseCase
import woowacourse.shopping.domain.usecase.OrderProductsUseCase
import woowacourse.shopping.ui.model.PaymentUiState

class PaymentViewModel(
    private val getCatalogProductsByProductIdsUseCase: GetCatalogProductsByProductIdsUseCase,
    private val getCouponsUseCase: GetCouponsUseCase,
    private val orderProductsUseCase: OrderProductsUseCase,
) : ViewModel() {
    private val _uiState: MutableLiveData<PaymentUiState> = MutableLiveData(PaymentUiState())
    val uiState: LiveData<PaymentUiState> get() = _uiState

    fun loadProducts(productIds: List<Long>) {
        viewModelScope.launch {
            getCatalogProductsByProductIdsUseCase(productIds)
                .onSuccess { newProducts ->
                    val products =
                        Products(
                            newProducts
                                .filterNot { it.cartId == null }
                                .map { it.copy(isSelected = true) },
                        )
                    updateUiState { current ->
                        current.copy(
                            products = products,
                            price = current.coupons.applyCoupon(products),
                        )
                    }
                    loadCoupons(products)
                }.onFailure {
                    updateUiState { current -> current.copy(connectionErrorMessage = it.message.toString()) }
                    Log.e("PaymentViewModel", it.toString())
                }
        }
    }

    private fun loadCoupons(products: Products) {
        viewModelScope.launch {
            getCouponsUseCase(products)
                .onSuccess {
                    updateUiState { current -> current.copy(coupons = it) }
                }.onFailure {
                    updateUiState { current -> current.copy(connectionErrorMessage = it.message.toString()) }
                    Log.e("PaymentViewModel", it.toString())
                }
        }
    }

    fun selectCoupon(couponId: Int) {
        updateUiState { current ->
            current.copy(
                coupons = current.coupons.selectCoupon(couponId),
            )
        }
        updateUiState { current ->
            current.copy(
                price = current.coupons.applyCoupon(current.products),
            )
        }
    }

    fun orderProducts() {
        viewModelScope.launch {
            val cartIds: Set<Long> = uiState.value?.products?.getSelectedCartIds() ?: return@launch

            orderProductsUseCase(cartIds)
                .onSuccess {
                    updateUiState { current ->
                        current.copy(isOrderSuccess = true)
                    }
                }.onFailure {
                    updateUiState { current ->
                        current.copy(
                            isOrderSuccess = false,
                            connectionErrorMessage = it.message.toString(),
                        )
                    }
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    private fun updateUiState(update: (PaymentUiState) -> PaymentUiState) {
        val current = _uiState.value ?: return
        _uiState.value = update(current)
    }

    companion object {
        const val MAX_USABLE_COUPON_COUNT = 1

        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T =
                    PaymentViewModel(
                        getCatalogProductsByProductIdsUseCase = getCatalogProductsByProductIdsUseCase,
                        getCouponsUseCase = getCouponsUseCase,
                        orderProductsUseCase = orderProductsUseCase,
                    ) as T
            }
    }
}

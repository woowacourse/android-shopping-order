package woowacourse.shopping.ui.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.di.UseCaseModule.getCatalogProductsByProductIdsUseCase
import woowacourse.shopping.di.UseCaseModule.getCouponsUseCase
import woowacourse.shopping.di.UseCaseModule.orderProductsUseCase
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.usecase.GetCatalogProductsByProductIdsUseCase
import woowacourse.shopping.domain.usecase.GetCouponsUseCase
import woowacourse.shopping.ui.model.PaymentUiState

class PaymentViewModel(
    private val getCatalogProductsByProductIdsUseCase: GetCatalogProductsByProductIdsUseCase,
    private val getCouponsUseCase: GetCouponsUseCase,
) : ViewModel() {
    private val _uiState: MutableLiveData<PaymentUiState> = MutableLiveData(PaymentUiState())
    val uiState: LiveData<PaymentUiState> get() = _uiState

    init {
        loadCoupons()
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            val uiState = uiState.value ?: return@launch

            getCouponsUseCase()
                .onSuccess {
                    _uiState.value = uiState.copy(coupons = it)
                }.onFailure {
                    _uiState.value = uiState.copy(connectionErrorMessage = it.message.toString())
                    Log.e("PaymentViewModel", it.toString())
                }
        }
    }

    fun loadProducts(productIds: List<Long>) {
        viewModelScope.launch {
            val uiState = uiState.value ?: return@launch

            getCatalogProductsByProductIdsUseCase(productIds)
                .onSuccess { newProducts ->
                    val products =
                        Products(
                            newProducts
                                .filterNot { it.cartId == null }
                                .map { it.copy(isSelected = true) },
                        )
                    _uiState.value =
                        uiState.copy(
                            products = products,
                            price = uiState.coupons.applyCoupon(products),
                        )
                }.onFailure {
                    _uiState.value = uiState.copy(connectionErrorMessage = it.message.toString())
                    Log.e("PaymentViewModel", it.toString())
                }
        }
    }

    fun selectCoupon(couponId: Int) {
        val uiState = uiState.value ?: return

        _uiState.value =
            uiState.copy(
                coupons = uiState.coupons.selectCoupon(couponId),
                price = uiState.coupons.applyCoupon(uiState.products),
            )
    }

    fun orderProducts() {
        viewModelScope.launch {
            val uiState = uiState.value ?: return@launch
            val cartIds: Set<Long> = uiState.products.getSelectedCartIds()

            orderProductsUseCase(cartIds)
                .onSuccess {
                    _uiState.value = uiState.copy(isOrderSuccess = true)
                }.onFailure {
                    _uiState.value =
                        uiState.copy(
                            isOrderSuccess = false,
                            connectionErrorMessage = it.message.toString(),
                        )
                    Log.e("CartViewModel", it.message.toString())
                }
        }
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
                    ) as T
            }
    }
}

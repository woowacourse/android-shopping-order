package woowacourse.shopping.ui.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import woowacourse.shopping.di.UseCaseInjection.getCatalogProductsByProductIdsUseCase
import woowacourse.shopping.di.UseCaseInjection.getCouponsUseCase
import woowacourse.shopping.di.UseCaseInjection.orderProductsUseCase
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.usecase.GetCatalogProductsByProductIdsUseCase
import woowacourse.shopping.domain.usecase.GetCouponsUseCase
import woowacourse.shopping.domain.usecase.OrderProductsUseCase
import woowacourse.shopping.ui.model.PaymentUiModel
import java.time.LocalDateTime

class PaymentViewModel(
    private val getCatalogProductsByProductIdsUseCase: GetCatalogProductsByProductIdsUseCase,
    private val getCouponsUseCase: GetCouponsUseCase,
    private val orderProductsUseCase: OrderProductsUseCase,
) : ViewModel() {
    private val _uiModel: MutableLiveData<PaymentUiModel> = MutableLiveData(PaymentUiModel())
    val uiModel: LiveData<PaymentUiModel> get() = _uiModel

    fun loadProducts(productIds: List<Long>) {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, e ->
                updateUiModel { current -> current.copy(connectionErrorMessage = e.message.toString()) }
                Log.e(TAG, e.message.toString())
            },
        ) {
            val products =
                Products(
                    getCatalogProductsByProductIdsUseCase(productIds)
                        .filterNot { it.cartId == null }
                        .map { it.copy(isSelected = true) },
                )
            updateUiModel { current ->
                current.copy(
                    products = products,
                )
            }
        }
    }

    fun loadCoupons(
        products: Products,
        nowDateTime: LocalDateTime = LocalDateTime.now(),
    ) {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, e ->
                updateUiModel { current -> current.copy(connectionErrorMessage = e.message.toString()) }
                Log.e(TAG, e.message.toString())
            },
        ) {
            val coupons = getCouponsUseCase()
            updateUiModel { current ->
                current.copy(
                    coupons = coupons.filterAvailableCoupons(products, nowDateTime),
                    price = current.coupons.applyCoupon(products, nowDateTime),
                )
            }
        }
    }

    fun selectCoupon(
        couponId: Int,
        nowDateTime: LocalDateTime = LocalDateTime.now(),
    ) {
        updateUiModel { current ->
            current.copy(
                coupons = current.coupons.selectCoupon(couponId),
            )
        }
        updateUiModel { current ->
            current.copy(
                price = current.coupons.applyCoupon(current.products, nowDateTime),
            )
        }
    }

    fun orderProducts() {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, e ->
                updateUiModel { current ->
                    current.copy(
                        isOrderSuccess = false,
                        connectionErrorMessage = e.message.toString(),
                    )
                }
                Log.e(TAG, e.message.toString())
            },
        ) {
            val cartIds: Set<Long> = uiModel.value?.products?.getSelectedCartIds() ?: return@launch
            orderProductsUseCase(cartIds)

            updateUiModel { current ->
                current.copy(isOrderSuccess = true)
            }
        }
    }

    private fun updateUiModel(update: (PaymentUiModel) -> PaymentUiModel) {
        val current = _uiModel.value ?: return
        _uiModel.value = update(current)
    }

    companion object {
        const val MAX_USABLE_COUPON_COUNT: Int = 1
        private const val TAG: String = "PaymentViewModel"

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

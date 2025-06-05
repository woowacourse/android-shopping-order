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
import woowacourse.shopping.domain.model.Coupons
import woowacourse.shopping.domain.model.Coupons.Companion.EMPTY_COUPONS
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.model.Products.Companion.EMPTY_PRODUCTS
import woowacourse.shopping.domain.usecase.GetCatalogProductsByProductIdsUseCase
import woowacourse.shopping.domain.usecase.GetCouponsUseCase
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData

class PaymentViewModel(
    private val getCatalogProductsByProductIdsUseCase: GetCatalogProductsByProductIdsUseCase,
    private val getCouponsUseCase: GetCouponsUseCase,
) : ViewModel() {
    private val _products: MutableLiveData<Products> = MutableLiveData(EMPTY_PRODUCTS)
    val products: LiveData<Products> get() = _products

    private val _coupons: MutableLiveData<Coupons> = MutableLiveData(EMPTY_COUPONS)
    val coupons: LiveData<Coupons> get() = _coupons

    private val _isCouponsLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isCouponsLoading: LiveData<Boolean> get() = _isCouponsLoading

    private val _isNetworkError: MutableLiveData<String> = MutableLiveData()
    val isNetworkError: LiveData<String> get() = _isNetworkError

    private val _isOrderSuccess: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val isOrderSuccess: SingleLiveData<Unit> get() = _isOrderSuccess

    init {
        loadCoupons()
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            if (isCouponsLoading.value == true) return@launch

            _isCouponsLoading.value = true
            getCouponsUseCase()
                .onSuccess {
                    _isCouponsLoading.value = false
                    _coupons.value = it
                }.onFailure {
                    _isCouponsLoading.value = false
                    _isNetworkError.value = it.message.toString()
                    Log.e("PaymentViewModel", it.toString())
                }
        }
    }

    fun loadProducts(productIds: List<Long>) {
        viewModelScope.launch {
            getCatalogProductsByProductIdsUseCase(productIds)
                .onSuccess { products ->
                    _products.value = Products(products.filterNot { it.cartId == null })
                }.onFailure {
                    _isNetworkError.value = it.message.toString()
                    Log.e("PaymentViewModel", it.toString())
                }
        }
    }

    fun toggleCoupon(couponId: Int) {
        _coupons.value = coupons.value?.toggleCoupon(couponId)
    }

    fun orderProducts() {
        viewModelScope.launch {
            val cartIds: Set<Long> = products.value?.getSelectedCartProductIds() ?: emptySet()

            orderProductsUseCase(cartIds)
                .onSuccess {
                    _isOrderSuccess.postValue(Unit)
                }.onFailure {
                    _isNetworkError.value = it.message
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    companion object {
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

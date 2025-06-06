package woowacourse.shopping.ui.productdetail

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
import woowacourse.shopping.domain.model.HistoryProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Product.Companion.EMPTY_PRODUCT
import woowacourse.shopping.domain.model.ProductDetail
import woowacourse.shopping.domain.usecase.AddSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductUseCase
import woowacourse.shopping.domain.usecase.GetRecentSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.UpdateCartProductUseCase
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData

class ProductDetailViewModel(
    private val getCatalogProductUseCase: GetCatalogProductUseCase,
    private val getRecentSearchHistoryUseCase: GetRecentSearchHistoryUseCase,
    private val addSearchHistoryUseCase: AddSearchHistoryUseCase,
    private val updateCartProductUseCase: UpdateCartProductUseCase,
) : ViewModel() {
    private val _product: MutableLiveData<Product> =
        MutableLiveData(EMPTY_PRODUCT)
    val product: LiveData<Product> get() = _product

    private val _lastHistoryProduct: MutableLiveData<HistoryProduct?> = MutableLiveData(null)
    val lastHistoryProduct: LiveData<HistoryProduct?> get() = _lastHistoryProduct

    private val _onCartProductAddSuccess: MutableSingleLiveData<Boolean?> =
        MutableSingleLiveData(null)
    val onCartProductAddSuccess: SingleLiveData<Boolean?> get() = _onCartProductAddSuccess

    fun loadProductDetail(id: Long) {
        viewModelScope.launch {
            val result = getCatalogProductUseCase(id)
            result
                .onSuccess { catalogProduct ->
                    _product.value = catalogProduct ?: EMPTY_PRODUCT
                }.onFailure {
                    Log.e("ProductDetailViewModel", it.message.toString())
                }
        }
    }

    fun loadLastHistoryProduct() {
        viewModelScope.launch {
            val historyProduct = getRecentSearchHistoryUseCase()
            _lastHistoryProduct.value = historyProduct
        }
    }

    fun addHistoryProduct(productDetail: ProductDetail) {
        viewModelScope.launch {
            addSearchHistoryUseCase(productDetail)
        }
    }

    fun decreaseCartProductQuantity() {
        _product.value = product.value?.decreaseQuantity()
    }

    fun increaseCartProductQuantity() {
        _product.value = product.value?.increaseQuantity()
    }

    fun updateCartProduct() {
        val product: Product = product.value ?: return
        viewModelScope.launch {
            val result =
                updateCartProductUseCase(
                    productId = product.productDetail.id,
                    cartId = product.cartId,
                    quantity = product.quantity,
                )
            result
                .onSuccess {
                    _onCartProductAddSuccess.setValue(true)
                }.onFailure {
                    Log.e("ProductDetailViewModel", it.message.toString())
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
                ): T {
                    val application = checkNotNull(extras[APPLICATION_KEY]) as ShoppingApp

                    return ProductDetailViewModel(
                        getCatalogProductUseCase = application.getCatalogProductUseCase,
                        getRecentSearchHistoryUseCase = application.getRecentSearchHistoryUseCase,
                        addSearchHistoryUseCase = application.addSearchHistoryUseCase,
                        updateCartProductUseCase = application.updateCartProductUseCase,
                    ) as T
                }
            }
    }
}

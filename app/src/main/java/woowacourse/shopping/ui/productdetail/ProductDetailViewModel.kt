package woowacourse.shopping.ui.productdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.di.UseCaseModule.addSearchHistoryUseCase
import woowacourse.shopping.di.UseCaseModule.getCatalogProductUseCase
import woowacourse.shopping.di.UseCaseModule.getRecentSearchHistoryUseCase
import woowacourse.shopping.di.UseCaseModule.updateCartProductUseCase
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

    private val _isError: MutableLiveData<String> = MutableLiveData()
    val isError: LiveData<String> get() = _isError

    fun loadProductDetail(productId: Long) {
        viewModelScope.launch {
            getCatalogProductUseCase(productId)
                .onSuccess { catalogProduct ->
                    _product.value = catalogProduct
                }.onFailure {
                    _isError.value = it.message
                }
        }
    }

    fun loadLastHistoryProduct() {
        viewModelScope.launch {
            runCatching {
                getRecentSearchHistoryUseCase()
            }.onSuccess {
                _lastHistoryProduct.value = it.getOrNull()
            }.onFailure {
            }
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
            updateCartProductUseCase(
                productId = product.productDetail.id,
                cartId = product.cartId,
                quantity = product.quantity,
            ).onSuccess {
                _onCartProductAddSuccess.postValue(true)
            }.onFailure {
                _isError.value = it.message
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
                    ProductDetailViewModel(
                        getCatalogProductUseCase = getCatalogProductUseCase,
                        getRecentSearchHistoryUseCase = getRecentSearchHistoryUseCase,
                        addSearchHistoryUseCase = addSearchHistoryUseCase,
                        updateCartProductUseCase = updateCartProductUseCase,
                    ) as T
            }
    }
}

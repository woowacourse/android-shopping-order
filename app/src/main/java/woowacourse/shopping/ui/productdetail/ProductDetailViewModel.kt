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
import woowacourse.shopping.domain.model.ProductDetail
import woowacourse.shopping.domain.usecase.AddSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductUseCase
import woowacourse.shopping.domain.usecase.GetRecentSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.UpdateCartProductUseCase
import woowacourse.shopping.ui.model.ProductDetailUiState

class ProductDetailViewModel(
    private val getCatalogProductUseCase: GetCatalogProductUseCase,
    private val getRecentSearchHistoryUseCase: GetRecentSearchHistoryUseCase,
    private val addSearchHistoryUseCase: AddSearchHistoryUseCase,
    private val updateCartProductUseCase: UpdateCartProductUseCase,
) : ViewModel() {
    private val _uiState: MutableLiveData<ProductDetailUiState> = MutableLiveData(ProductDetailUiState())
    val uiState: LiveData<ProductDetailUiState> get() = _uiState

    fun loadProductDetail(productId: Long) {
        viewModelScope.launch {
            val uiState = uiState.value ?: return@launch

            getCatalogProductUseCase(productId)
                .onSuccess { catalogProduct ->
                    _uiState.value = uiState.copy(product = catalogProduct)
                }.onFailure {
                    _uiState.value = uiState.copy(connectionErrorMessage = it.message.toString())
                }
        }
    }

    fun loadLastHistoryProduct() {
        viewModelScope.launch {
            getRecentSearchHistoryUseCase().onSuccess {
                _uiState.value = uiState.value?.copy(lastHistoryProduct = it)
            }
        }
    }

    fun addHistoryProduct(productDetail: ProductDetail) {
        viewModelScope.launch {
            addSearchHistoryUseCase(productDetail)
        }
    }

    fun decreaseCartProductQuantity() {
        val uiState = uiState.value ?: return
        _uiState.value = uiState.copy(product = uiState.product.decreaseQuantity())
    }

    fun increaseCartProductQuantity() {
        val uiState = uiState.value ?: return
        _uiState.value = uiState.copy(product = uiState.product.increaseQuantity())
    }

    fun updateCartProduct() {
        val uiState = uiState.value ?: return

        viewModelScope.launch {
            updateCartProductUseCase(
                productId = uiState.product.productDetail.id,
                cartId = uiState.product.cartId,
                quantity = uiState.product.quantity,
            ).onSuccess {
                _uiState.value = uiState.copy(isCartProductUpdateSuccess = true)
            }.onFailure {
                _uiState.value = uiState.copy(connectionErrorMessage = it.message.toString())
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

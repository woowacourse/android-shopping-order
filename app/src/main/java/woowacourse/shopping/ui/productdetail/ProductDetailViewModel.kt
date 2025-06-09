package woowacourse.shopping.ui.productdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.di.UseCaseInjection.addSearchHistoryUseCase
import woowacourse.shopping.di.UseCaseInjection.getCatalogProductUseCase
import woowacourse.shopping.di.UseCaseInjection.getRecentSearchHistoryUseCase
import woowacourse.shopping.di.UseCaseInjection.updateCartProductUseCase
import woowacourse.shopping.domain.model.ProductDetail
import woowacourse.shopping.domain.usecase.AddSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductUseCase
import woowacourse.shopping.domain.usecase.GetRecentSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.UpdateCartProductUseCase
import woowacourse.shopping.ui.model.ProductDetailUiModel

class ProductDetailViewModel(
    private val getCatalogProductUseCase: GetCatalogProductUseCase,
    private val getRecentSearchHistoryUseCase: GetRecentSearchHistoryUseCase,
    private val addSearchHistoryUseCase: AddSearchHistoryUseCase,
    private val updateCartProductUseCase: UpdateCartProductUseCase,
) : ViewModel() {
    private val _uiModel: MutableLiveData<ProductDetailUiModel> = MutableLiveData(ProductDetailUiModel())
    val uiModel: LiveData<ProductDetailUiModel> get() = _uiModel

    fun loadProductDetail(productId: Long) {
        viewModelScope.launch {
            getCatalogProductUseCase(productId)
                .onSuccess { catalogProduct ->
                    updateUiModel { current -> current.copy(product = catalogProduct) }
                }.onFailure {
                    updateUiModel { current -> current.copy(connectionErrorMessage = it.message.toString()) }
                }
        }
    }

    fun loadLastHistoryProduct() {
        viewModelScope.launch {
            getRecentSearchHistoryUseCase().onSuccess {
                updateUiModel { current -> current.copy(lastHistoryProduct = it) }
            }
        }
    }

    fun addHistoryProduct(productDetail: ProductDetail) {
        viewModelScope.launch {
            addSearchHistoryUseCase(productDetail)
        }
    }

    fun decreaseCartProductQuantity() {
        val uiModel = uiModel.value ?: return
        updateUiModel { current -> current.copy(product = uiModel.product.decreaseQuantity()) }
    }

    fun increaseCartProductQuantity() {
        val uiModel = uiModel.value ?: return
        updateUiModel { current -> current.copy(product = uiModel.product.increaseQuantity()) }
    }

    fun updateCartProduct() {
        val uiModel = uiModel.value ?: return
        viewModelScope.launch {
            updateCartProductUseCase(
                productId = uiModel.product.productDetail.id,
                cartId = uiModel.product.cartId,
                quantity = uiModel.product.quantity,
            ).onSuccess {
                updateUiModel { current -> current.copy(isCartProductUpdateSuccess = true) }
            }.onFailure {
                updateUiModel { current -> current.copy(connectionErrorMessage = it.message.toString()) }
            }
        }
    }

    private fun updateUiModel(update: (ProductDetailUiModel) -> ProductDetailUiModel) {
        val current = _uiModel.value ?: return
        _uiModel.value = update(current)
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

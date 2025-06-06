package woowacourse.shopping.ui.catalog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.di.UseCaseModule.decreaseCartProductQuantityUseCase
import woowacourse.shopping.di.UseCaseModule.getCartProductsQuantityUseCase
import woowacourse.shopping.di.UseCaseModule.getCatalogProductUseCase
import woowacourse.shopping.di.UseCaseModule.getCatalogProductsByProductIdsUseCase
import woowacourse.shopping.di.UseCaseModule.getCatalogProductsUseCase
import woowacourse.shopping.di.UseCaseModule.getSearchHistoryUseCase
import woowacourse.shopping.di.UseCaseModule.increaseCartProductQuantityUseCase
import woowacourse.shopping.domain.model.Page.Companion.UNINITIALIZED_PAGE
import woowacourse.shopping.domain.usecase.DecreaseCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.GetCartProductsQuantityUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductsByProductIdsUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductsUseCase
import woowacourse.shopping.domain.usecase.GetSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.IncreaseCartProductQuantityUseCase
import woowacourse.shopping.ui.model.CatalogUiState

class CatalogViewModel(
    private val getCatalogProductsUseCase: GetCatalogProductsUseCase,
    private val getCatalogProductUseCase: GetCatalogProductUseCase,
    private val getCatalogProductsByProductIdsUseCase: GetCatalogProductsByProductIdsUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val increaseCartProductQuantityUseCase: IncreaseCartProductQuantityUseCase,
    private val decreaseCartProductQuantityUseCase: DecreaseCartProductQuantityUseCase,
    private val getCartProductsQuantityUseCase: GetCartProductsQuantityUseCase,
) : ViewModel() {
    private val _uiState: MutableLiveData<CatalogUiState> = MutableLiveData(CatalogUiState())
    val uiState: LiveData<CatalogUiState> get() = _uiState

    init {
        loadCatalogProducts()
    }

    private fun loadCatalogProducts(
        page: Int =
            uiState.value
                ?.catalogProducts
                ?.page
                ?.current ?: UNINITIALIZED_PAGE,
        count: Int = SHOWN_PRODUCTS_COUNT,
    ) {
        viewModelScope.launch {
            updateUiState { current -> current.copy(isProductsLoading = true) }

            getCatalogProductsUseCase(page, count)
                .onSuccess { newProducts ->
                    updateUiState { current ->
                        current.copy(
                            catalogProducts = current.catalogProducts.plus(newProducts),
                            isProductsLoading = false,
                        )
                    }
                }.onFailure {
                    updateUiState { current -> current.copy(connectionErrorMessage = it.message.toString()) }
                    Log.e("CatalogViewModel", it.message.toString())
                }
        }
    }

    fun loadMoreCatalogProducts() {
        val currentPage =
            uiState.value
                ?.catalogProducts
                ?.page
                ?.current
                ?.plus(DEFAULT_PAGE_STEP) ?: UNINITIALIZED_PAGE
        loadCatalogProducts(page = currentPage)
    }

    fun loadHistoryProducts() {
        viewModelScope.launch {
            getSearchHistoryUseCase().onSuccess { historyProducts ->
                updateUiState { current ->
                    current.copy(
                        historyProducts = historyProducts,
                    )
                }
            }
        }
    }

    fun increaseCartProduct(productId: Long) {
        viewModelScope.launch {
            val product = uiState.value?.catalogProducts?.getProductByProductId(productId) ?: return@launch
            increaseCartProductQuantityUseCase(product).onSuccess {
                loadCartProduct(productId)
            }
        }
    }

    fun decreaseCartProduct(productId: Long) {
        viewModelScope.launch {
            val product = uiState.value?.catalogProducts?.getProductByProductId(productId) ?: return@launch
            decreaseCartProductQuantityUseCase(product).onSuccess {
                loadCartProduct(productId)
            }
        }
    }

    fun loadCartProduct(productId: Long) {
        viewModelScope.launch {
            getCatalogProductUseCase(productId)
                .onSuccess { cartProduct ->
                    updateUiState { current ->
                        current.copy(
                            catalogProducts = current.catalogProducts.updateProduct(cartProduct),
                        )
                    }
                }.onFailure {
                    updateUiState { current -> current.copy(connectionErrorMessage = it.message.toString()) }
                    Log.e("CatalogViewModel", it.message.toString())
                }
        }
    }

    fun loadCartProductsByProductIds(productIds: List<Long>) {
        viewModelScope.launch {
            getCatalogProductsByProductIdsUseCase(productIds)
                .onSuccess { cartProducts ->
                    updateUiState { current ->
                        current.copy(
                            catalogProducts = current.catalogProducts.updateProducts(cartProducts),
                        )
                    }
                }.onFailure {
                    updateUiState { current -> current.copy(connectionErrorMessage = it.message.toString()) }
                    Log.e("CatalogViewModel", it.message.toString())
                }
        }
    }

    fun loadCartProductsQuantity() {
        viewModelScope.launch {
            getCartProductsQuantityUseCase()
                .onSuccess { quantity ->
                    updateUiState { current -> current.copy(cartProductsQuantity = quantity) }
                }.onFailure {
                    Log.e("CatalogViewModel", it.message.toString())
                }
        }
    }

    private fun updateUiState(update: (CatalogUiState) -> CatalogUiState) {
        val current = _uiState.value ?: return
        _uiState.value = update(current)
    }

    companion object {
        private const val DEFAULT_PAGE_STEP: Int = 1
        private const val SHOWN_PRODUCTS_COUNT: Int = 20

        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T =
                    CatalogViewModel(
                        getCatalogProductsUseCase = getCatalogProductsUseCase,
                        getCatalogProductUseCase = getCatalogProductUseCase,
                        getCatalogProductsByProductIdsUseCase = getCatalogProductsByProductIdsUseCase,
                        getSearchHistoryUseCase = getSearchHistoryUseCase,
                        increaseCartProductQuantityUseCase = increaseCartProductQuantityUseCase,
                        decreaseCartProductQuantityUseCase = decreaseCartProductQuantityUseCase,
                        getCartProductsQuantityUseCase = getCartProductsQuantityUseCase,
                    ) as T
            }
    }
}

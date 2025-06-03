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
import woowacourse.shopping.domain.model.HistoryProduct
import woowacourse.shopping.domain.model.Page.Companion.UNINITIALIZED_PAGE
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.model.Products.Companion.EMPTY_PRODUCTS
import woowacourse.shopping.domain.usecase.DecreaseCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.GetCartProductsQuantityUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductsByProductIdsUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductsUseCase
import woowacourse.shopping.domain.usecase.GetSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.IncreaseCartProductQuantityUseCase

class CatalogViewModel(
    private val getCatalogProductsUseCase: GetCatalogProductsUseCase,
    private val getCatalogProductUseCase: GetCatalogProductUseCase,
    private val getCatalogProductsByProductIdsUseCase: GetCatalogProductsByProductIdsUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val increaseCartProductQuantityUseCase: IncreaseCartProductQuantityUseCase,
    private val decreaseCartProductQuantityUseCase: DecreaseCartProductQuantityUseCase,
    private val getCartProductsQuantityUseCase: GetCartProductsQuantityUseCase,
) : ViewModel() {
    private val _catalogProducts: MutableLiveData<Products> = MutableLiveData(EMPTY_PRODUCTS)
    val catalogProducts: LiveData<Products> get() = _catalogProducts

    private val _historyProducts: MutableLiveData<List<HistoryProduct>> = MutableLiveData(emptyList())
    val historyProducts: LiveData<List<HistoryProduct>> get() = _historyProducts

    private val _cartProductsQuantity: MutableLiveData<Int> = MutableLiveData(INITIAL_PRODUCT_QUANTITY)
    val cartProductsQuantity: LiveData<Int> get() = _cartProductsQuantity

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isError: MutableLiveData<String> = MutableLiveData()
    val isError: LiveData<String> get() = _isError

    init {
        loadCartProducts()
    }

    private fun loadCartProducts(
        page: Int = catalogProducts.value?.page?.current ?: UNINITIALIZED_PAGE,
        count: Int = SHOWN_PRODUCTS_COUNT,
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            getCatalogProductsUseCase(page, count)
                .onSuccess { newProducts ->
                    _catalogProducts.value = catalogProducts.value?.plus(newProducts)
                    _isLoading.value = false
                }.onFailure {
                    _isError.value = it.message
                }
        }
    }

    fun loadMoreCartProducts() {
        val currentPage =
            catalogProducts.value
                ?.page
                ?.current
                ?.plus(DEFAULT_PAGE_STEP) ?: UNINITIALIZED_PAGE
        loadCartProducts(page = currentPage)
    }

    fun loadHistoryProducts() {
        viewModelScope.launch {
            getSearchHistoryUseCase().onSuccess { historyProducts ->
                _historyProducts.value = historyProducts
            }
        }
    }

    fun increaseCartProduct(productId: Long) {
        viewModelScope.launch {
            val product = catalogProducts.value?.getProductByProductId(productId) ?: return@launch
            increaseCartProductQuantityUseCase(product).onSuccess {
                loadCartProduct(productId)
            }
        }
    }

    fun decreaseCartProduct(productId: Long) {
        viewModelScope.launch {
            val product = catalogProducts.value?.getProductByProductId(productId) ?: return@launch
            decreaseCartProductQuantityUseCase(product).onSuccess {
                loadCartProduct(productId)
            }
        }
    }

    fun loadCartProduct(productId: Long) {
        viewModelScope.launch {
            getCatalogProductUseCase(productId)
                .onSuccess { cartProduct ->
                    _catalogProducts.value = catalogProducts.value?.updateProduct(cartProduct)
                }.onFailure {
                    _isError.value = it.message
                }
        }
    }

    fun loadCartProductsByProductIds(productIds: List<Long>) {
        viewModelScope.launch {
            getCatalogProductsByProductIdsUseCase(productIds)
                .onSuccess { cartProducts ->
                    _catalogProducts.value = catalogProducts.value?.updateProducts(cartProducts)
                }.onFailure {
                    _isError.value = it.message
                }
        }
    }

    fun loadCartProductsQuantity() {
        viewModelScope.launch {
            getCartProductsQuantityUseCase()
                .onSuccess { quantity ->
                    _cartProductsQuantity.value = quantity
                }.onFailure {
                    Log.e("CatalogViewModel", it.message.toString())
                }
        }
    }

    companion object {
        private const val DEFAULT_PAGE_STEP: Int = 1
        private const val SHOWN_PRODUCTS_COUNT: Int = 20
        private const val INITIAL_PRODUCT_QUANTITY: Int = 0

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

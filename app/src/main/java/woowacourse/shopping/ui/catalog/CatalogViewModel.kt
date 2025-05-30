package woowacourse.shopping.ui.catalog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.di.UseCaseModule.decreaseCartProductQuantityUseCase
import woowacourse.shopping.di.UseCaseModule.getCartProductsQuantityUseCase
import woowacourse.shopping.di.UseCaseModule.getCatalogProductUseCase
import woowacourse.shopping.di.UseCaseModule.getCatalogProductsByIdsUseCase
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
import woowacourse.shopping.domain.usecase.GetCatalogProductsByIdsUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductsUseCase
import woowacourse.shopping.domain.usecase.GetSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.IncreaseCartProductQuantityUseCase

class CatalogViewModel(
    private val getCatalogProductsUseCase: GetCatalogProductsUseCase,
    private val getCatalogProductUseCase: GetCatalogProductUseCase,
    private val getCatalogProductsByIdsUseCase: GetCatalogProductsByIdsUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val increaseCartProductQuantityUseCase: IncreaseCartProductQuantityUseCase,
    private val decreaseCartProductQuantityUseCase: DecreaseCartProductQuantityUseCase,
    private val getCartProductsQuantityUseCase: GetCartProductsQuantityUseCase,
) : ViewModel() {
    private val _products: MutableLiveData<Products> = MutableLiveData(EMPTY_PRODUCTS)
    val products: LiveData<Products> get() = _products

    private val _historyProducts: MutableLiveData<List<HistoryProduct>> =
        MutableLiveData(emptyList())
    val historyProducts: LiveData<List<HistoryProduct>> get() = _historyProducts

    private val _cartProductsQuantity: MutableLiveData<Int> =
        MutableLiveData(INITIAL_PRODUCT_QUANTITY)
    val cartProductsQuantity: LiveData<Int> get() = _cartProductsQuantity

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        loadCartProducts()
    }

    private fun loadCartProducts(
        page: Int = products.value?.page?.current ?: UNINITIALIZED_PAGE,
        count: Int = SHOWN_PRODUCTS_COUNT,
    ) {
        _isLoading.value = true
        getCatalogProductsUseCase(
            page = page,
            size = count,
        ) { result ->
            result
                .onSuccess { newProducts ->
                    _products.postValue(products.value?.plus(newProducts))
                    _isLoading.value = false
                }.onFailure {
                    Log.e("CatalogViewModel", it.message.toString())
                }
        }
    }

    fun loadMoreCartProducts() {
        val currentPage =
            products.value
                ?.page
                ?.current
                ?.plus(DEFAULT_PAGE_STEP) ?: UNINITIALIZED_PAGE
        loadCartProducts(page = currentPage)
    }

    fun loadHistoryProducts() {
        getSearchHistoryUseCase { historyProducts ->
            _historyProducts.postValue(historyProducts)
        }
    }

    fun increaseCartProduct(productId: Long) {
        runCatching {
            increaseCartProductQuantityUseCase(
                product = products.value?.getProductByProductId(productId) ?: return,
            )
        }.onSuccess {
            loadCartProduct(productId)
        }
    }

    fun decreaseCartProduct(productId: Long) {
        runCatching {
            decreaseCartProductQuantityUseCase(
                product = products.value?.getProductByProductId(productId) ?: return,
            )
        }.onSuccess {
            loadCartProduct(productId)
        }
    }

    fun loadCartProduct(productId: Long) {
        getCatalogProductUseCase(productId) { result ->
            result
                .onSuccess { cartProduct ->
                    _products.postValue(
                        products.value?.updateProduct(
                            cartProduct ?: return@getCatalogProductUseCase,
                        ),
                    )
                }.onFailure {
                    Log.e("CatalogViewModel", it.message.toString())
                }
        }
    }

    fun loadCartProductsByIds(ids: List<Long>) {
        getCatalogProductsByIdsUseCase(ids) { result ->
            result
                .onSuccess { cartProducts ->
                    _products.postValue(products.value?.updateProducts(cartProducts))
                }.onFailure {
                    Log.e("CatalogViewModel", it.message.toString())
                }
        }
    }

    fun loadCartProductsQuantity() {
        getCartProductsQuantityUseCase { result ->
            result
                .onSuccess { quantity ->
                    _cartProductsQuantity.postValue(quantity)
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
                        getCatalogProductsByIdsUseCase = getCatalogProductsByIdsUseCase,
                        getSearchHistoryUseCase = getSearchHistoryUseCase,
                        increaseCartProductQuantityUseCase = increaseCartProductQuantityUseCase,
                        decreaseCartProductQuantityUseCase = decreaseCartProductQuantityUseCase,
                        getCartProductsQuantityUseCase = getCartProductsQuantityUseCase,
                    ) as T
            }
    }
}

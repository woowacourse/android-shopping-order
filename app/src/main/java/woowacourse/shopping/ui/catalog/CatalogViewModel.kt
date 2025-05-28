package woowacourse.shopping.ui.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.ShoppingApp
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
    private val _products: MutableLiveData<Products> =
        MutableLiveData(EMPTY_PRODUCTS)
    val products: LiveData<Products> get() = _products

    private val _historyProducts: MutableLiveData<List<HistoryProduct>> =
        MutableLiveData(emptyList())
    val historyProducts: LiveData<List<HistoryProduct>> get() = _historyProducts

    private val _cartProductsQuantity: MutableLiveData<Int> =
        MutableLiveData(0)
    val cartProductsQuantity: LiveData<Int> get() = _cartProductsQuantity

    init {
        loadCartProducts()
    }

    private fun loadCartProducts(
        page: Int = products.value?.page?.current ?: UNINITIALIZED_PAGE,
        count: Int = SHOWN_PRODUCTS_COUNT,
    ) {
        getCatalogProductsUseCase(
            page = page,
            size = count,
        ) { newProducts ->
            _products.postValue(products.value?.plus(newProducts))
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
        getCatalogProductUseCase(productId) { cartProduct ->
            _products.postValue(
                products.value?.updateProduct(
                    cartProduct ?: return@getCatalogProductUseCase,
                ),
            )
        }
    }

    fun loadCartProductsByIds(ids: List<Long>) {
        getCatalogProductsByIdsUseCase(ids) { cartProducts ->
            _products.postValue(products.value?.updateProducts(cartProducts))
        }
    }

    fun loadCartProductsQuantity() {
        getCartProductsQuantityUseCase { quantity ->
            _cartProductsQuantity.postValue(quantity)
        }
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
                ): T {
                    val application = checkNotNull(extras[APPLICATION_KEY]) as ShoppingApp

                    return CatalogViewModel(
                        getCatalogProductsUseCase = application.getCatalogProductsUseCase,
                        getCatalogProductUseCase = application.getCatalogProductUseCase,
                        getCatalogProductsByIdsUseCase = application.getCatalogProductsByIdsUseCase,
                        getSearchHistoryUseCase = application.getSearchHistoryUseCase,
                        increaseCartProductQuantityUseCase = application.increaseCartProductQuantityUseCase,
                        decreaseCartProductQuantityUseCase = application.decreaseCartProductQuantityUseCase,
                        getCartProductsQuantityUseCase = application.getCartProductsQuantityUseCase,
                    ) as T
                }
            }
    }
}

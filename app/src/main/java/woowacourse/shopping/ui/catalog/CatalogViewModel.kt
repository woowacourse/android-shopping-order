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
) : ViewModel() {
    private val _Products: MutableLiveData<Products> =
        MutableLiveData(EMPTY_PRODUCTS)
    val products: LiveData<Products> get() = _Products

    private val _historyProducts: MutableLiveData<List<HistoryProduct>> =
        MutableLiveData(emptyList())
    val historyProducts: LiveData<List<HistoryProduct>> get() = _historyProducts

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
            _Products.postValue(products.value?.plus(newProducts))
        }
    }

    fun loadMoreCartProducts() {
        val currentPage =
            products.value
                ?.page
                ?.current
                ?.plus(DEFAULT_PAGE_STEP) ?: UNINITIALIZED_PAGE
        loadCartProducts(page = currentPage + 1)
    }

    fun loadHistoryProducts() {
        getSearchHistoryUseCase { historyProducts ->
            _historyProducts.postValue(historyProducts)
        }
    }

    fun increaseCartProduct(id: Long) {
        increaseCartProductQuantityUseCase(id) { newQuantity ->
            _Products.postValue(
                products.value?.updateProductQuantity(
                    id,
                    newQuantity,
                ),
            )
        }
    }

    fun decreaseCartProduct(id: Long) {
        decreaseCartProductQuantityUseCase(id) { newQuantity ->
            _Products.postValue(
                products.value?.updateProductQuantity(
                    id,
                    newQuantity,
                ),
            )
        }
    }

    fun loadCartProduct(id: Long) {
        getCatalogProductUseCase(id) { cartProduct ->
            _Products.postValue(
                products.value?.updateProduct(
                    cartProduct ?: return@getCatalogProductUseCase,
                ),
            )
        }
    }

    fun loadCartProducts(ids: List<Int>) {
        getCatalogProductsByIdsUseCase(ids) { cartProducts ->
            _Products.postValue(products.value?.updateProducts(cartProducts))
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
                    ) as T
                }
            }
    }
}

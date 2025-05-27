package woowacourse.shopping.ui.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.domain.model.CatalogProducts
import woowacourse.shopping.domain.model.CatalogProducts.Companion.EMPTY_CATALOG_PRODUCTS
import woowacourse.shopping.domain.model.HistoryProduct
import woowacourse.shopping.domain.model.Page.Companion.UNINITIALIZED_PAGE
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
    private val _catalogProducts: MutableLiveData<CatalogProducts> =
        MutableLiveData(EMPTY_CATALOG_PRODUCTS)
    val catalogProducts: LiveData<CatalogProducts> get() = _catalogProducts

    private val _historyProducts: MutableLiveData<List<HistoryProduct>> =
        MutableLiveData(emptyList())
    val historyProducts: LiveData<List<HistoryProduct>> get() = _historyProducts

    init {
        loadCartProducts()
    }

    private fun loadCartProducts(
        page: Int = catalogProducts.value?.page?.current ?: UNINITIALIZED_PAGE,
        count: Int = SHOWN_PRODUCTS_COUNT,
    ) {
        getCatalogProductsUseCase(
            page = page,
            size = count,
        ) { newProducts ->
            _catalogProducts.postValue(catalogProducts.value?.plus(newProducts))
        }
    }

    fun loadMoreCartProducts() {
        val currentPage =
            catalogProducts.value
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
            _catalogProducts.postValue(
                catalogProducts.value?.updateCatalogProductQuantity(
                    id,
                    newQuantity,
                ),
            )
        }
    }

    fun decreaseCartProduct(id: Long) {
        decreaseCartProductQuantityUseCase(id) { newQuantity ->
            _catalogProducts.postValue(
                catalogProducts.value?.updateCatalogProductQuantity(
                    id,
                    newQuantity,
                ),
            )
        }
    }

    fun loadCartProduct(id: Long) {
        getCatalogProductUseCase(id) { cartProduct ->
            _catalogProducts.postValue(
                catalogProducts.value?.updateCatalogProduct(
                    cartProduct ?: return@getCatalogProductUseCase,
                ),
            )
        }
    }

    fun loadCartProducts(ids: List<Int>) {
        getCatalogProductsByIdsUseCase(ids) { cartProducts ->
            _catalogProducts.postValue(catalogProducts.value?.updateCatalogProducts(cartProducts))
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

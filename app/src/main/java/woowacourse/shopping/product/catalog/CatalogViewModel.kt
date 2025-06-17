package woowacourse.shopping.product.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.mapper.toUiModel
import woowacourse.shopping.data.repository.CartProductRepository
import woowacourse.shopping.data.repository.CatalogProductRepository
import woowacourse.shopping.data.repository.RecentlyViewedProductRepository
import woowacourse.shopping.domain.LoadingState
import woowacourse.shopping.product.catalog.CatalogItem.ProductItem

class CatalogViewModel(
    private val catalogProductRepository: CatalogProductRepository,
    private val cartProductRepository: CartProductRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
) : ViewModel() {
    private val _catalogItems =
        MutableLiveData<List<CatalogItem>>(emptyList<CatalogItem>())
    val catalogItems: LiveData<List<CatalogItem>> = _catalogItems

    private val _page = MutableLiveData<Int>(INITIAL_PAGE)
    val page: LiveData<Int> = _page

    private val _updatedItem = MutableLiveData<ProductUiModel>()
    val updatedItem: LiveData<ProductUiModel> = _updatedItem

    private val _recentlyViewedProducts =
        MutableLiveData<List<ProductUiModel>>(emptyList<ProductUiModel>())
    val recentlyViewedProducts: LiveData<List<ProductUiModel>> = _recentlyViewedProducts

    private val _loadingState: MutableLiveData<LoadingState> =
        MutableLiveData(LoadingState.loaded())
    val loadingState: LiveData<LoadingState> get() = _loadingState

    private val _cartItemCount = MutableLiveData<Int>(INITIAL_CART_ITEM_COUNT)
    val cartItemCount: LiveData<Int> get() = _cartItemCount

    init {
        fetchTotalCount()
        viewModelScope.launch {
            val allProductsSize = catalogProductRepository.getAllProductsSize()
            loadCatalog(PAGE_SIZE, 20, allProductsSize)
        }
    }

    fun fetchTotalCount() {
        viewModelScope.launch {
            runCatching {
                cartProductRepository.getTotalElements()
            }.onSuccess { count ->
                _cartItemCount.postValue(count)
            }
        }
    }

    fun increaseQuantity(product: ProductUiModel) {
        if (product.quantity == 0) {
            viewModelScope.launch {
                runCatching {
                    val product =
                        cartProductRepository.insertCartProduct(product.copy(quantity = 1))
                    _updatedItem.postValue(product)
                }
            }
        } else {
            viewModelScope.launch {
                runCatching {
                    cartProductRepository.updateProduct(product, product.quantity + 1)
                }.onSuccess {
                    _updatedItem.postValue(product.copy(quantity = product.quantity + 1))
                }
            }
        }
        fetchTotalCount()
    }

    fun decreaseQuantity(product: ProductUiModel) {
        if (product.quantity == 1) {
            viewModelScope.launch {
                runCatching {
                    cartProductRepository.deleteCartProduct(product)
                }.onSuccess {
                    _updatedItem.postValue(product.copy(quantity = 0))
                }
            }
        } else {
            viewModelScope.launch {
                runCatching {
                    cartProductRepository.updateProduct(product, product.quantity - 1)
                }.onSuccess {
                    _updatedItem.postValue(product.copy(quantity = product.quantity - 1))
                }
            }
        }
        fetchTotalCount()
    }

    fun loadNextCatalogProducts() {
        increasePage()
        val currentPage = page.value ?: 0

        viewModelScope.launch {
            val allProductSize = catalogProductRepository.getAllProductsSize()
            val startIndex = currentPage * PAGE_SIZE
            val endIndex = minOf(startIndex + PAGE_SIZE, allProductSize)
            loadCatalog(endIndex, 20, allProductSize)
        }
    }

    fun loadCatalogUntilCurrentPage() {
        _catalogItems.value = emptyList()
        val currentPage = page.value ?: 0

        viewModelScope.launch {
            val allProductSize = catalogProductRepository.getAllProductsSize()
            val endIndex = minOf((currentPage + 1) * PAGE_SIZE, allProductSize)
            loadCatalog(endIndex, 20, allProductSize)
        }
    }

    fun loadCatalog(
        endIndex: Int,
        size: Int = 20,
        allProductsSize: Int,
    ) {
        _loadingState.postValue(LoadingState.loading())

        viewModelScope.launch {
            val pagedProducts = catalogProductRepository.getProductsByPage(
                page.value ?: 0,
                size,
            )

            val totalElements = cartProductRepository.getTotalElements()
            val cartProducts = cartProductRepository.getCartProducts(totalElements)
            val cartProductMap: Map<Int, ProductUiModel> =
                cartProducts.associateBy { it.id }

            val mergedProducts =
                pagedProducts.map { product ->
                    val cartProduct = cartProductMap[product.id]
                    if (cartProduct != null) {
                        product.copy(
                            quantity = cartProduct.quantity,
                            cartItemId = cartProduct.cartItemId,
                        )
                    } else {
                        product
                    }
                }

            val items = mergedProducts.map { ProductItem(it) }
            val prevItems =
                _catalogItems.value
                    .orEmpty()
                    .filterNot { it is CatalogItem.LoadMoreButtonItem }
            val hasNextPage = endIndex < allProductsSize
            val updatedItems =
                if (hasNextPage) {
                    prevItems + items + CatalogItem.LoadMoreButtonItem
                } else {
                    prevItems + items
                }

            _catalogItems.postValue(updatedItems)
            _loadingState.postValue(LoadingState.loaded())
        }
    }

    fun loadRecentlyViewedProducts() {
        viewModelScope.launch {
            val products = recentlyViewedProductRepository.getRecentlyViewedProducts()
            _recentlyViewedProducts.postValue(products.map { it.toUiModel() })
        }
    }

    private fun increasePage() {
        _page.value = _page.value?.plus(1)
    }

    companion object {
        private const val PAGE_SIZE = 20
        private const val INITIAL_PAGE = 0
        private const val INITIAL_CART_ITEM_COUNT = 0
    }
}

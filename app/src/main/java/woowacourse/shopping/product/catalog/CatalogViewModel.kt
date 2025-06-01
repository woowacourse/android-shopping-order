package woowacourse.shopping.product.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    private val remoteCatalogProductRepository: CatalogProductRepository,
) : ViewModel() {
    private val catalogItems: MutableList<CatalogItem> = mutableListOf()

    private val _loadedCatalogItems =
        MutableLiveData<List<CatalogItem>>(emptyList<CatalogItem>())
    val loadedCatalogItems: LiveData<List<CatalogItem>> = _loadedCatalogItems

    private var page: Int = INITIAL_PAGE

    private val _updatedItem = MutableLiveData<ProductUiModel>()
    val updatedItem: LiveData<ProductUiModel> = _updatedItem

    private val _cartItemSize = MutableLiveData<Int>(0)
    val cartItemSize: LiveData<Int> = _cartItemSize

    private val _recentlyViewedProducts =
        MutableLiveData<List<ProductUiModel>>(emptyList<ProductUiModel>())
    val recentlyViewedProducts: LiveData<List<ProductUiModel>> = _recentlyViewedProducts

    private val _loadingState: MutableLiveData<LoadingState> =
        MutableLiveData(LoadingState.loaded())
    val loadingState: LiveData<LoadingState> get() = _loadingState

    init {
        catalogProductRepository.getAllProductsSize { allProductsSize ->
            loadCatalog(0, PAGE_SIZE, 20, allProductsSize)
        }
    }

    fun increaseQuantity(product: ProductUiModel) {
        if (product.quantity == 0) {
            cartProductRepository.insertCartProduct(product.copy(quantity = 1)) { product ->
                _updatedItem.postValue(product)
            }
        } else {
            cartProductRepository.updateProduct(product.id, product.quantity + 1) { result ->
                if (result == true) {
                    _updatedItem.postValue(product.copy(quantity = product.quantity + 1))
                }
            }
        }

        loadCartItemSize()
    }

    fun decreaseQuantity(product: ProductUiModel) {
        if (product.quantity == 1) {
            cartProductRepository.deleteCartProduct(product.id) { result ->
                if (result == true) {
                    _updatedItem.postValue(product.copy(quantity = 0))
                }
            }
        } else {
            cartProductRepository.updateProduct(product.id, product.quantity - 1) { result ->
                if (result == true) {
                    _updatedItem.postValue(product.copy(quantity = product.quantity - 1))
                }
            }
        }

        loadCartItemSize()
    }

    fun loadNextCatalogProducts() {
        page++
        remoteCatalogProductRepository.getAllProductsSize { allProductSize ->
            val startIndex = page * PAGE_SIZE
            val endIndex = minOf(startIndex + PAGE_SIZE, allProductSize)

            loadCatalog(startIndex, endIndex, 20, allProductSize)
        }
    }

    fun loadCatalogUntilCurrentPage() {
        catalogItems.clear()
        remoteCatalogProductRepository.getAllProductsSize { allProductSize ->
            val startIndex = 0
            val endIndex = minOf((page + 1) * PAGE_SIZE, allProductSize)

            loadCatalog(startIndex, endIndex, 20, allProductSize)
        }
    }

    fun loadCatalog(
        startIndex: Int,
        endIndex: Int,
        size: Int = 20,
        allProductsSize: Int,
    ) {
        _loadingState.postValue(LoadingState.loading())

        remoteCatalogProductRepository.getProductsByPage(
            page,
            size,
        ) { pagedProducts ->
            cartProductRepository.getTotalElements { totalElements ->
                cartProductRepository.getCartProducts(totalElements) { cartProducts ->
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
                    val hasNextPage = endIndex < allProductsSize
                    val updatedItems =
                        if (hasNextPage) {
                            items + CatalogItem.LoadMoreButtonItem
                        } else {
                            items
                        }

                    catalogItems += updatedItems
                    _loadedCatalogItems.postValue(updatedItems)
                    _loadingState.postValue(LoadingState.loaded())
                }
            }
        }
    }

    fun loadCartItemSize() {
        cartProductRepository.getCartItemSize { size ->
            _cartItemSize.postValue(size)
        }
    }

    fun loadRecentlyViewedProducts() {
        recentlyViewedProductRepository.getRecentlyViewedProducts { products ->
            _recentlyViewedProducts.postValue(products.map { it.toUiModel() })
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
        private const val INITIAL_PAGE = 0
    }
}

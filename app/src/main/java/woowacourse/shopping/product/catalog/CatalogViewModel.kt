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

    fun increaseQuantity(product: ProductUiModel) {
        viewModelScope.launch {
            if (product.quantity == 0) {
                val cartItemId = cartProductRepository.insertCartProduct(product.id, quantity = 1)
                val addedProduct = product.copy(quantity = 1, cartItemId = cartItemId)
                _updatedItem.postValue(addedProduct)
            } else if (product.cartItemId != null) {
                val result: Boolean =
                    cartProductRepository.updateProduct(product.cartItemId, product.quantity + 1)
                if (result) {
                    _updatedItem.postValue(product.copy(quantity = product.quantity + 1))
                }
            }
            loadCartItemSize()
        }
    }

    fun decreaseQuantity(product: ProductUiModel) {
        viewModelScope.launch {
            if (product.quantity == 1 && product.cartItemId != null) {
                val result: Boolean = cartProductRepository.deleteCartProduct(product.cartItemId)
                if (result) {
                    _updatedItem.postValue(product.copy(quantity = 0))
                }
            } else if (product.cartItemId != null) {
                val result: Boolean =
                    cartProductRepository.updateProduct(product.cartItemId, product.quantity - 1)
                if (result == true) {
                    _updatedItem.postValue(product.copy(quantity = product.quantity - 1))
                }
            }
            loadCartItemSize()
        }
    }

    fun loadNextCatalogProducts() {
        page++
        val endIndex = (page + 1) * PAGE_SIZE
        viewModelScope.launch {
            val allProductSize = catalogProductRepository.getAllProductsSize()
            loadCatalog(page, endIndex, 20, allProductSize)
        }
    }

    fun loadCatalogUntilCurrentPage() {
        val endIndex = (page + 1) * PAGE_SIZE
        viewModelScope.launch {
            val allProductSize = catalogProductRepository.getAllProductsSize()
            loadCatalog(0, endIndex, endIndex, allProductSize)
        }
    }

    fun loadCatalog(
        page: Int,
        endIndex: Int,
        size: Int = 20,
        allProductsSize: Long,
    ) {
        viewModelScope.launch {
            _loadingState.postValue(LoadingState.loading())
            val pagedProducts: List<ProductUiModel> =
                catalogProductRepository.getProductsByPage(page, size)
            val totalElements: Long = cartProductRepository.getTotalElements()
            val cartProducts: List<ProductUiModel> =
                cartProductRepository.getCartProducts(totalElements)

            val cartProductMap: Map<Long, ProductUiModel> =
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

            _loadedCatalogItems.postValue(updatedItems)
            _loadingState.postValue(LoadingState.loaded())
        }
    }

    fun loadCartItemSize() {
        viewModelScope.launch {
            val cartItemSize = cartProductRepository.getCartItemSize()
            _cartItemSize.postValue(cartItemSize)
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

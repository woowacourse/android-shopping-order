package woowacourse.shopping.product.catalog

import android.util.Log
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
        if (product.quantity == 0) {
            cartProductRepository.insertCartProduct(product.copy(quantity = 1)) { product ->
                _updatedItem.postValue(product)
            }
        } else if (product.cartItemId != null) {
            cartProductRepository.updateProduct(product.cartItemId, product.quantity + 1) { result ->
                if (result == true) {
                    _updatedItem.postValue(product.copy(quantity = product.quantity + 1))
                }
            }
        }

        loadCartItemSize()
    }

    fun decreaseQuantity(product: ProductUiModel) {
        if (product.quantity == 1 && product.cartItemId != null) {
            cartProductRepository.deleteCartProduct(product.cartItemId) { result ->
                if (result == true) {
                    _updatedItem.postValue(product.copy(quantity = 0))
                }
            }
        } else if (product.cartItemId != null) {
            cartProductRepository.updateProduct(product.cartItemId, product.quantity - 1) { result ->
                if (result == true) {
                    _updatedItem.postValue(product.copy(quantity = product.quantity - 1))
                }
            }
        }

        loadCartItemSize()
    }

    fun loadNextCatalogProducts() {
        page++
        catalogProductRepository.getAllProductsSize { allProductSize ->
            val endIndex = (page + 1) * PAGE_SIZE
            loadCatalog(page, endIndex, 20, allProductSize)
        }
    }

    fun loadCatalogUntilCurrentPage() {
        catalogProductRepository.getAllProductsSize { allProductSize ->
            val endIndex = (page + 1) * PAGE_SIZE
            loadCatalog(0, endIndex, endIndex, allProductSize)
        }
    }

    fun loadCatalog(
        page: Int,
        endIndex: Int,
        size: Int = 20,
        allProductsSize: Long,
    ) {
        _loadingState.postValue(LoadingState.loading())

        catalogProductRepository.getProductsByPage(page, size) { pagedProducts ->
            cartProductRepository.getTotalElements { totalElements ->
                cartProductRepository.getCartProducts(totalElements) { cartProducts ->

                    Log.d("loadCatalog", "$page, $size, $pagedProducts, $cartProducts")
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

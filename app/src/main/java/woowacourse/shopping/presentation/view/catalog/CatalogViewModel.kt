package woowacourse.shopping.presentation.view.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.di.provider.RepositoryProvider
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.presentation.model.CatalogItem
import woowacourse.shopping.presentation.model.toCatalogProductItem
import woowacourse.shopping.presentation.model.toUiModel
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData
import woowacourse.shopping.presentation.view.catalog.event.CatalogMessageEvent

class CatalogViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentRepository: RecentProductRepository,
) : ViewModel() {
    private val _toastEvent = MutableSingleLiveData<CatalogMessageEvent>()
    val toastEvent: SingleLiveData<CatalogMessageEvent> = _toastEvent

    private val _products = MutableLiveData<List<CatalogItem>>()
    val products: LiveData<List<CatalogItem>> = _products

    private val _cartItemCount = MutableLiveData<Int>()
    val cartItemCount: LiveData<Int> = _cartItemCount

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var page = 0

    init {
        loadProducts()
    }

    fun loadProducts() {
        _isLoading.value = true

        productRepository.fetchProducts(page, PRODUCT_LOAD_LIMIT) { result ->
            result
                .onSuccess { handleProductPageLoad(it) }
                .onFailure { emitToastMessage(CatalogMessageEvent.FETCH_PRODUCTS_FAILURE) }
        }
    }

    fun increaseProductQuantity(productId: Long) {
        cartRepository.insertCartProductQuantityToCart(productId, QUANTITY_STEP) { result ->
            result
                .onSuccess { updateProductQuantityInList(productId) }
                .onFailure { emitToastMessage(CatalogMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE) }
        }
    }

    fun decreaseProductQuantity(productId: Long) {
        cartRepository.decreaseCartProductQuantityFromCart(productId, QUANTITY_STEP) { result ->
            result
                .onSuccess { updateProductQuantityInList(productId) }
                .onFailure { emitToastMessage(CatalogMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE) }
        }
    }

    fun refreshCatalogItems() {
        if (_products.value.isNullOrEmpty()) return

        loadRecentProducts { recentProducts ->
            rebuildCatalogItems(recentProducts)
        }
    }

    private fun handleProductPageLoad(pageable: PageableItem<Product>) {
        page++

        val currentItems = currentProductItems()
        val newItems = pageable.items.map { it.toCatalogProductItem() }
        val mergedItems = (currentItems + newItems).distinctBy { it.productId }
        val catalogWithLoadMore = appendLoadMore(mergedItems, pageable.hasMore)

        loadRecentProducts { recentProducts ->
            rebuildCatalogItems(recentProducts, catalogWithLoadMore)
        }
    }

    private fun loadRecentProducts(onSuccess: (CatalogItem.RecentProducts) -> Unit) {
        recentRepository.getRecentProducts(RECENT_PRODUCT_LIMIT) { result ->
            result
                .onSuccess {
                    val uiModels = it.map { product -> product.toUiModel() }
                    onSuccess(CatalogItem.RecentProducts(uiModels))
                }.onFailure {
                    emitToastMessage(CatalogMessageEvent.FETCH_RECENT_PRODUCT_FAILURE)
                }
        }
    }

    private fun rebuildCatalogItems(
        recentProductsItem: CatalogItem.RecentProducts,
        currentItems: List<CatalogItem> = _products.value.orEmpty(),
    ) {
        val ids = currentItems.filterIsInstance<CatalogItem.ProductItem>().map { it.productId }
        val newCartProducts = cartRepository.findCartProductsByProductIds(ids)

        newCartProducts
            .onFailure { emitToastMessage(CatalogMessageEvent.FIND_PRODUCT_QUANTITY_FAILURE) }
            .onSuccess {
                val updatedItems = applyCartQuantities(it, currentItems)
                val finalCatalog = prependRecentProducts(recentProductsItem, updatedItems)
                _products.postValue(finalCatalog)
                updateCartItemCount()
            }

        _isLoading.postValue(false)
    }

    private fun updateProductQuantityInList(productId: Long) {
        cartRepository
            .findQuantityByProductId(productId)
            .onSuccess { newQuantity -> applyProductQuantityUpdate(productId, newQuantity) }
            .onFailure { emitToastMessage(CatalogMessageEvent.FIND_PRODUCT_QUANTITY_FAILURE) }
    }

    private fun applyProductQuantityUpdate(
        productId: Long,
        quantity: Int,
    ) {
        val currentProducts = _products.value.orEmpty()
        val updatedProducts =
            currentProducts.map { item ->
                if (item is CatalogItem.ProductItem && item.productId == productId) {
                    item.copy(quantity = quantity)
                } else {
                    item
                }
            }
        _products.postValue(updatedProducts)
        updateCartItemCount()
    }

    private fun updateCartItemCount() {
        cartRepository.fetchCartItemCount { result ->
            result
                .onSuccess { _cartItemCount.postValue(it) }
                .onFailure { emitToastMessage(CatalogMessageEvent.FETCH_CART_ITEM_COUNT_FAILURE) }
        }
    }

    private fun currentProductItems(): List<CatalogItem.ProductItem> = _products.value.orEmpty().filterIsInstance<CatalogItem.ProductItem>()

    private fun appendLoadMore(
        products: List<CatalogItem.ProductItem>,
        hasMore: Boolean,
    ): List<CatalogItem> =
        buildList {
            addAll(products)
            if (hasMore) add(CatalogItem.LoadMoreItem)
        }

    private fun prependRecentProducts(
        recentProducts: CatalogItem.RecentProducts?,
        items: List<CatalogItem>,
    ): List<CatalogItem> {
        val cleanedItems = items.filterNot { it is CatalogItem.RecentProducts }
        return buildList {
            if (recentProducts != null && recentProducts.products.isNotEmpty()) add(recentProducts)
            addAll(cleanedItems)
        }
    }

    private fun applyCartQuantities(
        cartProducts: List<CartProduct>,
        items: List<CatalogItem>,
    ): List<CatalogItem> {
        val cartItemMap = cartProducts.associateBy { it.product.id }

        return items.map { item ->
            if (item !is CatalogItem.ProductItem) return@map item

            val quantity = cartItemMap[item.productId]?.quantity ?: DEFAULT_QUANTITY
            item.copy(quantity = quantity)
        }
    }

    private fun emitToastMessage(event: CatalogMessageEvent) {
        _toastEvent.postValue(event)
    }

    companion object {
        private const val DEFAULT_QUANTITY = 0
        private const val QUANTITY_STEP = 1
        private const val PRODUCT_LOAD_LIMIT = 20
        private const val RECENT_PRODUCT_LIMIT = 10

        val Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T =
                    CatalogViewModel(
                        RepositoryProvider.productRepository,
                        RepositoryProvider.cartRepository,
                        RepositoryProvider.recentProductRepository,
                    ) as T
            }
    }
}

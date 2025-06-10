package woowacourse.shopping.presentation.view.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RepositoryProvider
import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Product
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.model.toCartItem
import woowacourse.shopping.presentation.model.toCartItemUiModel
import woowacourse.shopping.presentation.model.toProductUiModel
import woowacourse.shopping.presentation.view.catalog.adapter.CatalogItem

class CatalogViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _isLoadingData = MutableLiveData<Boolean>()
    val isLoadingData: LiveData<Boolean> = _isLoadingData

    private val _items = MutableLiveData<List<CatalogItem>>()
    val items: LiveData<List<CatalogItem>> = _items

    private val _itemUpdateEvent = MutableLiveData<ProductUiModel>()
    val itemUpdateEvent: LiveData<ProductUiModel> = _itemUpdateEvent

    private val _totalCartCount = MutableLiveData<Int>()
    val totalCartCount: LiveData<Int> = _totalCartCount

    private var isProcessingRequest = false

    fun loadCatalog(nextPage: Boolean) {
        viewModelScope.launch {
            _isLoadingData.value = true
            calculateTotalCartCount()
            val productsCount = _items.value?.filterIsInstance<CatalogItem.ProductItem>()?.size ?: 0
            val pageIndex =
                ((productsCount - 1) / PAGE_SIZE)
                    .coerceAtLeast(0)
                    .let { if (nextPage) it + 1 else it }
            val pageOfProducts = productRepository.loadProductsUpToPage(pageIndex, PAGE_SIZE)
            val cartItems = productRepository.loadAllCartItems()
            val recentProducts = productRepository.loadRecentProducts(RECENTLY_VIEWED_PRODUCTS_COUNT)
            val productUiModels = matchProductsToCartItems(pageOfProducts.items, cartItems)
            val items =
                buildList {
                    if (recentProducts.isNotEmpty()) {
                        val recentProductsItem =
                            CatalogItem.RecentProductsItem(recentProducts.map(Product::toProductUiModel))
                        add(recentProductsItem)
                    }
                    addAll(productUiModels.map { uiModel -> CatalogItem.ProductItem(uiModel) })
                    if (!pageOfProducts.isLast) add(CatalogItem.LoadMoreItem)
                }
            _items.postValue(items)
            _isLoadingData.value = false
        }
    }

    private fun matchProductsToCartItems(
        products: List<Product>,
        cartItems: List<CartItem>,
    ): List<ProductUiModel> {
        val idToCartItem =
            cartItems.associateBy(
                { cartItem -> cartItem.product.id },
                { cartItem -> cartItem.toProductUiModel() },
            )

        val fetchedUiModels =
            products.map { product ->
                idToCartItem[product.id] ?: product.toProductUiModel()
            }

        return fetchedUiModels
    }

    fun increaseQuantity(product: ProductUiModel) {
        viewModelScope.launch {
            if (isProcessingRequest) return@launch
            isProcessingRequest = true
            val cartItem = product.toCartItem()
            if (product.quantity == 0) {
                addToCart(cartItem.toCartItemUiModel())
                isProcessingRequest = false
                return@launch
            }
            cartRepository.increaseQuantity(cartItem)
            _itemUpdateEvent.postValue(
                cartItem.copy(quantity = cartItem.quantity + 1).toProductUiModel(),
            )
            calculateTotalCartCount()
            isProcessingRequest = false
        }
    }

    fun decreaseQuantity(product: ProductUiModel) {
        viewModelScope.launch {
            val cartItem = product.toCartItem()
            if (cartItem.quantity == 1) {
                cartRepository.deleteCartItem(cartItem.cartId)
                _itemUpdateEvent.postValue(product.copy(quantity = 0))
                calculateTotalCartCount()
            } else {
                if (isProcessingRequest) return@launch
                isProcessingRequest = true
                cartRepository.decreaseQuantity(cartItem)
                _itemUpdateEvent.postValue(
                    cartItem.copy(quantity = cartItem.quantity - 1).toProductUiModel(),
                )
            }
            calculateTotalCartCount()
            isProcessingRequest = false
        }
    }

    private suspend fun addToCart(cartItem: CartItemUiModel) {
        val newItem = cartItem.cartItem.copy(quantity = 1)
        cartRepository.addCartItem(newItem)?.let { addedItem ->
            _itemUpdateEvent.postValue(addedItem.toProductUiModel())
            calculateTotalCartCount()
        }
    }

    private suspend fun calculateTotalCartCount() {
        _totalCartCount.postValue(cartRepository.loadTotalCartCount())
    }

    companion object {
        private const val PAGE_SIZE = 20
        private const val RECENTLY_VIEWED_PRODUCTS_COUNT = 10

        @Suppress("UNCHECKED_CAST")
        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val productRepository = RepositoryProvider.productRepository
                    val cartRepository = RepositoryProvider.cartRepository
                    return CatalogViewModel(productRepository, cartRepository) as T
                }
            }
    }
}

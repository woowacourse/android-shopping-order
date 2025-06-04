package woowacourse.shopping.presentation.view.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.data.RepositoryProvider
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
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
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _items = MutableLiveData<List<CatalogItem>>()
    val items: LiveData<List<CatalogItem>> = _items

    private val _itemUpdateEvent = MutableLiveData<ProductUiModel>()
    val itemUpdateEvent: LiveData<ProductUiModel> = _itemUpdateEvent

    private val _totalCartCount = MutableLiveData<Int>()
    val totalCartCount: LiveData<Int> = _totalCartCount

    fun loadCatalog(nextPage: Boolean) {
        _isLoading.value = true
        calculateTotalCartCount()

        val productsCount = _items.value?.filterIsInstance<CatalogItem.ProductItem>()?.size ?: 0
        val pageIndex =
            ((productsCount - 1) / PAGE_SIZE)
                .coerceAtLeast(0)
                .let { if (nextPage) it + 1 else it }
        productRepository.loadProductsUpToPage(pageIndex, PAGE_SIZE) { products, isLastPage ->
            productRepository.loadAllCartItems { cartItems ->
                productRepository.loadRecentProducts(RECENTLY_VIEWED_PRODUCTS_COUNT) { recentProducts ->
                    val productUiModels = matchProductsToCartItems(products, cartItems)
                    val items =
                        buildList {
                            if (recentProducts.isNotEmpty()) {
                                val recentProductsItem =
                                    CatalogItem.RecentProductsItem(recentProducts.map(Product::toProductUiModel))
                                add(recentProductsItem)
                            }
                            addAll(productUiModels.map { uiModel -> CatalogItem.ProductItem(uiModel) })
                            if (!isLastPage) add(CatalogItem.LoadMoreItem)
                        }
                    _items.postValue(items)
                }
            }
            _isLoading.value = false
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
        val cartItem = product.toCartItem()
        if (product.quantity == 0) {
            addToCart(cartItem.toCartItemUiModel())
            return
        }
        cartRepository.increaseQuantity(cartItem) {
            _itemUpdateEvent.postValue(
                cartItem.copy(quantity = cartItem.quantity + 1).toProductUiModel(),
            )
            calculateTotalCartCount()
        }
    }

    fun decreaseQuantity(product: ProductUiModel) {
        val cartItem = product.toCartItem()
        if (cartItem.quantity == 1) {
            cartRepository.deleteCartItem(cartItem.cartId) {
                _itemUpdateEvent.postValue(product.copy(quantity = 0))
                calculateTotalCartCount()
            }
        } else {
            cartRepository.decreaseQuantity(cartItem) {
                _itemUpdateEvent.postValue(
                    cartItem.copy(quantity = cartItem.quantity - 1).toProductUiModel(),
                )
                calculateTotalCartCount()
            }
        }
    }

    private fun addToCart(cartItem: CartItemUiModel) {
        val newItem = cartItem.cartItem.copy(quantity = 1)
        cartRepository.addCartItem(newItem) { addedCartItem ->
            _itemUpdateEvent.postValue(addedCartItem?.toProductUiModel())
            calculateTotalCartCount()
        }
    }

    private fun calculateTotalCartCount() {
        cartRepository.getAllCartItemsCount { count ->
            _totalCartCount.postValue(count)
        }
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

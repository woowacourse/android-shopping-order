package woowacourse.shopping.presentation.view.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.model.toCartItem
import woowacourse.shopping.presentation.model.toCartItemUiModel
import woowacourse.shopping.presentation.model.toUiModel
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

    private val loadSize: Int = 20
    private var lastId: Long = DEFAULT_ID
    private var currentPage: Int = 0

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        _isLoading.value = true

        productRepository.loadCartItems { cartItems ->
            val totalCount = cartItems?.sumOf { it.quantity } ?: 0
            _totalCartCount.postValue(totalCount)

            val cartItemState =
                cartItems?.associateBy(
                    { it.product.id },
                    { it.toUiModel() },
                )

            productRepository.loadRecentProducts(limit = 10) { recentProducts ->
                val recentUiModels = recentProducts.map { it.toUiModel() }
                val recentItem = CatalogItem.RecentProductItem(recentUiModels)

                productRepository.loadProducts(currentPage, loadSize) { fetchedProducts, hasMore ->
                    val fetchedUiModels =
                        fetchedProducts.map { product ->
                            cartItemState?.get(product.id) ?: product.toUiModel()
                        }
                    lastId = fetchedUiModels.lastOrNull()?.id ?: DEFAULT_ID

                    val currentUiModels =
                        _items.value
                            .orEmpty()
                            .filterIsInstance<CatalogItem.ProductItem>()
                            .map { it.product }

                    val combinedUiModels =
                        (currentUiModels + fetchedUiModels)
                            .distinctBy { it.id }

                    val updatedItems = mutableListOf<CatalogItem>()
                    if (recentUiModels.isNotEmpty()) {
                        updatedItems.add(recentItem)
                    }
                    updatedItems.addAll(
                        combinedUiModels.map { CatalogItem.ProductItem(it) },
                    )
                    if (hasMore && updatedItems.none { it is CatalogItem.LoadMoreItem }) {
                        updatedItems.add(CatalogItem.LoadMoreItem)
                    }

                    _items.postValue(updatedItems)
                    currentPage++
                }
            }
            _isLoading.value = false
        }
    }

    fun refreshCartState() {
        if (_items.value.isNullOrEmpty()) return

        updateRecentProducts()

        productRepository.loadCartItems { cartItems ->
            val updatedCartState =
                cartItems?.associateBy(
                    { it.product.id },
                    { it.toUiModel() },
                )

            val totalCount = cartItems?.sumOf { it.quantity } ?: 0
            _totalCartCount.postValue(totalCount)

            val updatedItems =
                _items.value
                    ?.map { updatedItem ->
                        if (updatedItem is CatalogItem.ProductItem) {
                            val updatedProduct = updatedCartState?.get(updatedItem.product.id)
                            CatalogItem.ProductItem(
                                updatedProduct ?: updatedItem.product.copy(quantity = 0),
                            )
                        } else {
                            updatedItem
                        }
                    }

            _items.postValue(updatedItems.orEmpty())
        }
    }

    fun increaseQuantity(product: ProductUiModel) {
        val cartItem = product.toCartItem()
        if (product.quantity == 0) {
            addToCart(cartItem.toCartItemUiModel())
            return
        }
        cartRepository.increaseQuantity(cartItem) { id ->
            updateCartItem(id)
        }
    }

    fun decreaseQuantity(product: ProductUiModel) {
        val cartItem = product.toCartItem()

        if (cartItem.quantity <= 1) {
            cartRepository.deleteCartItem(cartItem.cartId) {
                _itemUpdateEvent.postValue(product.copy(quantity = 0))
                calculateTotalCartCount()
            }
        } else {
            cartRepository.decreaseQuantity(cartItem) { id ->
                updateCartItem(id)
            }
        }
    }

    private fun addToCart(cartItem: CartItemUiModel) {
        val newItem = cartItem.cartItem.copy(quantity = 1)
        cartRepository.addCartItem(newItem) {
            cartRepository.getAllCartItems { cartItems ->
                cartItems?.find { cartItem -> cartItem.product.id == newItem.product.id }?.let { foundItem ->
                    _itemUpdateEvent.postValue(foundItem.toUiModel())
                    calculateTotalCartCount()
                }
            }
        }
    }

    private fun updateCartItem(cartId: Long) {
        getCartItemByCartId(cartId) { cartItem ->
            if (cartItem != null) {
                _itemUpdateEvent.postValue(cartItem.toUiModel())
            }
            calculateTotalCartCount()
        }
    }

    private fun getCartItemByCartId(
        id: Long,
        callback: (CartItem?) -> Unit,
    ) {
        cartRepository.getAllCartItems { cartItems ->
            val foundItem = cartItems?.find { cartItem -> cartItem.cartId == id }
            callback(foundItem)
        }
    }

    private fun calculateTotalCartCount() {
        cartRepository.getAllCartItemsCount { count ->
            _totalCartCount.postValue(count?.quantity)
        }
    }

    private fun updateRecentProducts() {
        productRepository.loadRecentProducts(limit = 10) { recentProducts ->
            val recentUiModels = recentProducts.map { it.toUiModel() }
            val recentItem = CatalogItem.RecentProductItem(recentUiModels)

            val currentItems = _items.value.orEmpty()

            val updatedItems =
                buildList {
                    if (recentUiModels.isNotEmpty()) {
                        add(recentItem)
                    }

                    addAll(
                        currentItems.filter { it !is CatalogItem.RecentProductItem },
                    )
                }

            _items.postValue(updatedItems)
        }
    }

    companion object {
        private const val DEFAULT_ID = 0L

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

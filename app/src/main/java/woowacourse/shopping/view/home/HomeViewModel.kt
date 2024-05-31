package woowacourse.shopping.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.data.model.CartItem
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.util.Event
import woowacourse.shopping.view.cart.CartViewModel.Companion.DESCENDING_SORT_ORDER
import woowacourse.shopping.view.cart.QuantityClickListener
import woowacourse.shopping.view.home.adapter.product.HomeViewItem.ProductViewItem
import woowacourse.shopping.view.state.UiState

class HomeViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel(), HomeClickListener, QuantityClickListener {
    private val _homeUiState = MutableLiveData<UiState<List<ProductViewItem>>>(UiState.Loading)
    val homeUiState: LiveData<UiState<List<ProductViewItem>>>
        get() = _homeUiState

    private val _recentProducts = MutableLiveData<List<RecentProduct>>()
    val recentProducts: LiveData<List<RecentProduct>>
        get() = _recentProducts

    val isRecentProductsEmpty: LiveData<Boolean> = recentProducts.map { recentProductsValue ->
        recentProductsValue.isEmpty()
    }

    private val _loadedProductViewItems = MutableLiveData<List<ProductViewItem>>(emptyList())
    val loadedProductViewItems: LiveData<List<ProductViewItem>>
        get() = _loadedProductViewItems

    private val _canLoadMore = MutableLiveData(false)
    val canLoadMore: LiveData<Boolean>
        get() = _canLoadMore

    private val _cartTotalQuantity = MutableLiveData(0)
    val cartTotalQuantity: LiveData<Int>
        get() = _cartTotalQuantity

    private val _navigateToDetail = MutableLiveData<Event<Int>>()
    val navigateToDetail: LiveData<Event<Int>>
        get() = _navigateToDetail

    private val _navigateToCart = MutableLiveData<Event<Boolean>>()
    val navigateToCart: LiveData<Event<Boolean>>
        get() = _navigateToCart

    private var page = 0
    private var cartItems: List<CartItem> = emptyList()

    init {
        loadRecentProducts()
        loadCartItems()
        loadProductViewItems()
    }

    fun updateData() {
        loadRecentProducts()
        loadCartItems()
        updateProductViewItems()
    }

    private fun loadRecentProducts() {
        _recentProducts.value = recentProductRepository.findAll(RECENT_PRODUCTS_LIMIT)
    }

    private fun loadProductViewItems() {
        runCatching {
            productRepository.getProductResponse(
                category = CATEGORY_UNDEFINED,
                page = page++,
                size = PAGE_SIZE,
                sort = DESCENDING_SORT_ORDER,
            )
        }.onSuccess { productResponse ->
            val products = productResponse.getOrNull()?.products ?: emptyList()
            val productViewItems = products.map { product ->
                val quantity = getCartItemByProductId(product.productId)?.quantity ?: 0
                ProductViewItem(product, quantity)
            }
            _canLoadMore.value = productResponse.getOrNull()?.last?.not() ?: false
            _loadedProductViewItems.value = loadedProductViewItems.value?.plus(productViewItems)
            _homeUiState.value = UiState.Success(loadedProductViewItems.value ?: emptyList())
        }
    }

    private fun loadCartItems() {
        runCatching {
            cartRepository.getCartItems(0, (cartTotalQuantity.value ?: 0), DESCENDING_SORT_ORDER)
        }.onSuccess { cartResponse ->
            cartItems = cartResponse.getOrNull()?.cartItems ?: emptyList()
            _cartTotalQuantity.value = cartRepository.getCartTotalQuantity().getOrNull()?.quantity
        }
    }

    private fun getCartItemByProductId(productId: Int): CartItem? {
        return cartItems.firstOrNull { cartItem -> cartItem.product.productId == productId }
    }

    private fun updateProductViewItems() {
        val updatedProductViewItems = cartItems.map { cartItem ->
            ProductViewItem(cartItem.product, cartItem.quantity)
        }

        updatedProductViewItems.forEach { updatedProductViewItem ->
            val position = loadedProductViewItems.value?.indexOfFirst { loadedProductViewItem ->
                loadedProductViewItem.product.productId == updatedProductViewItem.product.productId
            } ?: -1
            if (position != -1) {
                (loadedProductViewItems.value as MutableList)[position] = updatedProductViewItem
            }
        }

        loadedProductViewItems.value?.forEachIndexed { index, loadedProductViewItem ->
            if (isCartItemDeleted(loadedProductViewItem)) {
                val deletedProductViewItem = ProductViewItem(loadedProductViewItem.product, 0)
                (loadedProductViewItems.value as MutableList)[index] = deletedProductViewItem
            }
        }
        _loadedProductViewItems.value = loadedProductViewItems.value?.toList()
    }

    private fun isCartItemDeleted(loadedProductViewItem: ProductViewItem): Boolean {
        return loadedProductViewItem.quantity > 0 && getCartItemByProductId(loadedProductViewItem.product.productId) == null
    }

    private fun updateProductViewItemQuantity(
        product: Product,
        quantity: Int,
    ) {
        val updatedProductViewItem = ProductViewItem(product, quantity)
        val position =
            _loadedProductViewItems.value?.indexOfFirst { loadedProductViewItem -> loadedProductViewItem.product.productId == product.productId }
                ?: -1
        if (position != -1) {
            (loadedProductViewItems.value as MutableList)[position] = updatedProductViewItem
            _loadedProductViewItems.value = loadedProductViewItems.value?.toList()
        }
    }

    override fun onProductClick(productId: Int) {
        _navigateToDetail.value = Event(productId)
    }

    override fun onLoadMoreButtonClick() {
        loadProductViewItems()
    }

    override fun onShoppingCartButtonClick() {
        _navigateToCart.value = Event(true)
    }

    override fun onPlusButtonClick(product: Product) {
        runCatching {
            cartRepository.addCartItem(product.productId, 1)
        }.onSuccess {
            updateProductViewItemQuantity(product, 1)
            loadCartItems()
        }
    }

    override fun onQuantityPlusButtonClick(productId: Int) {
        val cartItem = getCartItemByProductId(productId) ?: return
        runCatching {
            cartRepository.updateCartItem(cartItem.cartItemId, cartItem.quantity + 1)
        }.onSuccess {
            updateProductViewItemQuantity(cartItem.product, cartItem.quantity + 1)
            loadCartItems()
        }
    }

    override fun onQuantityMinusButtonClick(productId: Int) {
        val cartItem = getCartItemByProductId(productId) ?: return
        runCatching {
            cartRepository.updateCartItem(cartItem.cartItemId, cartItem.quantity - 1)
        }.onSuccess {
            if (cartItem.quantity == 1) {
                cartRepository.deleteCartItem(cartItem.cartItemId)
            } else {
                cartRepository.updateCartItem(cartItem.cartItemId, cartItem.quantity - 1)
            }
            updateProductViewItemQuantity(cartItem.product, cartItem.quantity - 1)
            loadCartItems()
        }
    }

    companion object {
        private val CATEGORY_UNDEFINED: String? = null
        private const val PAGE_SIZE = 20
        private const val RECENT_PRODUCTS_LIMIT = 10
    }
}

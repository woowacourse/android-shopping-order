package woowacourse.shopping.ui.home.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.event.Event
import woowacourse.shopping.ui.home.action.HomeNavigationActions
import woowacourse.shopping.ui.home.adapter.product.HomeViewItem.ProductViewItem
import woowacourse.shopping.ui.home.listener.HomeClickListener
import woowacourse.shopping.ui.home.listener.ProductClickListener
import woowacourse.shopping.ui.order.viewmodel.OrderViewModel.Companion.DESCENDING_SORT_ORDER
import woowacourse.shopping.ui.state.UiState

class HomeViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel(), HomeClickListener, ProductClickListener {
    private val _homeUiState = MutableLiveData<UiState<List<ProductViewItem>>>(UiState.Loading)
    val homeUiState: LiveData<UiState<List<ProductViewItem>>>
        get() = _homeUiState

    private val loadedProductViewItems: MutableList<ProductViewItem> = mutableListOf()
    private var cartItems: List<CartItem> = emptyList()

    private val _recentProducts = MutableLiveData<List<RecentProduct>>()
    val recentProducts: LiveData<List<RecentProduct>>
        get() = _recentProducts

    val isRecentProductsEmpty: LiveData<Boolean> =
        recentProducts.map { recentProductsValue ->
            recentProductsValue.isEmpty()
        }

    private val _canLoadMore = MutableLiveData(false)
    val canLoadMore: LiveData<Boolean>
        get() = _canLoadMore

    private val _cartTotalQuantity = MutableLiveData(0)
    val cartTotalQuantity: LiveData<Int>
        get() = _cartTotalQuantity

    private val _homeNavigationActions = MutableLiveData<Event<HomeNavigationActions>>()
    val homeNavigationActions: LiveData<Event<HomeNavigationActions>>
        get() = _homeNavigationActions

    private var page = 0

    init {
        Handler(Looper.getMainLooper()).postDelayed({
            loadCartItems()
            loadProductViewItems()
            loadRecentProducts()
        }, 1000)
    }

    fun updateData() {
        loadCartItems()
        updateProductViewItems()
        loadRecentProducts()
    }

    private fun loadRecentProducts() {
        _recentProducts.value = recentProductRepository.findAll(RECENT_PRODUCTS_LIMIT)
    }

    private fun loadProductViewItems() {
        runCatching {
            productRepository.getProducts(
                category = CATEGORY_UNDEFINED,
                page = page,
                size = PAGE_SIZE,
                sort = ASCENDING_SORT_ORDER,
            )
        }.onSuccess {
            page += 1
            val products = it.getOrNull()?.products ?: emptyList()
            val productViewItems =
                products.map { product ->
                    val quantity = getCartItemByProductId(product.productId)?.quantity ?: 0
                    ProductViewItem(product, quantity)
                }
            _canLoadMore.value = it.getOrNull()?.canLoadMore
            loadedProductViewItems.addAll(productViewItems)
            _homeUiState.value = UiState.Success(loadedProductViewItems)
        }.onFailure {
            _homeUiState.value = UiState.Error(it)
        }
    }

    private fun loadCartItems() {
        runCatching {
            cartRepository.getCartItems(0, (cartTotalQuantity.value ?: 0), DESCENDING_SORT_ORDER)
        }.onSuccess {
            cartItems = it.getOrNull() ?: emptyList()
            _cartTotalQuantity.value = cartRepository.getCartTotalQuantity().getOrNull()
        }
    }

    private fun getCartItemByProductId(productId: Int): CartItem? {
        return cartItems.firstOrNull { cartItem -> cartItem.product.productId == productId }
    }

    private fun updateProductViewItems() {
        val updatedProductViewItems =
            cartItems.map { cartItem ->
                ProductViewItem(cartItem.product, cartItem.quantity)
            }

        updatedProductViewItems.forEach { updatedProductViewItem ->
            val position =
                loadedProductViewItems.indexOfFirst { loadedProductViewItem ->
                    loadedProductViewItem.product.productId == updatedProductViewItem.product.productId
                }
            if (position != -1) {
                loadedProductViewItems[position] = updatedProductViewItem
            }
        }

        loadedProductViewItems.forEachIndexed { index, loadedProductViewItem ->
            if (isCartItemDeleted(loadedProductViewItem)) {
                val deletedProductViewItem = ProductViewItem(loadedProductViewItem.product, 0)
                loadedProductViewItems[index] = deletedProductViewItem
            }
        }
        _homeUiState.value = UiState.Success(loadedProductViewItems)
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
            loadedProductViewItems.indexOfFirst { loadedProductViewItem -> loadedProductViewItem.product.productId == product.productId }
        if (position != -1) {
            loadedProductViewItems[position] = updatedProductViewItem
            _homeUiState.value = UiState.Success(loadedProductViewItems)
        }

    }

    override fun onLoadMoreButtonClick() {
        loadProductViewItems()
    }

    override fun onShoppingCartButtonClick() {
        _homeNavigationActions.value = Event(HomeNavigationActions.NavigateToCart)
    }

    override fun onProductClick(productId: Int) {
        _homeNavigationActions.value = Event(HomeNavigationActions.NavigateToDetail(productId))
    }

    override fun onPlusButtonClick(product: Product) {
        val addedProduct = ProductViewItem(product, 1)
        runCatching {
            cartRepository.addCartItem(addedProduct.product.productId, addedProduct.quantity)
        }.onSuccess {
            updateProductViewItemQuantity(addedProduct.product, addedProduct.quantity)
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
        const val ASCENDING_SORT_ORDER = "asc"
    }
}

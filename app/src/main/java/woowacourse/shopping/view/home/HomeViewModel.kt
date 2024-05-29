package woowacourse.shopping.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.data.model.CartItem2
import woowacourse.shopping.data.model.Product2
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.CartRepository2
import woowacourse.shopping.domain.repository.ProductRepository2
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.util.Event
import woowacourse.shopping.view.cart.QuantityClickListener
import woowacourse.shopping.view.home.adapter.product.HomeViewItem.ProductViewItem
import woowacourse.shopping.view.state.UIState

class HomeViewModel(
    private val productRepository: ProductRepository2,
    private val cartRepository: CartRepository2,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel(), HomeClickListener, QuantityClickListener {
    private var pageNumber = 0

    private val _shoppingUiState = MutableLiveData<UIState<List<ProductViewItem>>>(UIState.Loading)
    val shoppingUiState: LiveData<UIState<List<ProductViewItem>>>
        get() = _shoppingUiState

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

    private val _totalQuantity = MutableLiveData(0)
    val totalQuantity: LiveData<Int>
        get() = _totalQuantity

    private val _updatedProductItem = MutableLiveData<ProductViewItem>()
    val updatedProductItem: LiveData<ProductViewItem>
        get() = _updatedProductItem

    private val _navigateToDetail = MutableLiveData<Event<Int>>()
    val navigateToDetail: LiveData<Event<Int>>
        get() = _navigateToDetail

    private val _navigateToCart = MutableLiveData<Event<Boolean>>()
    val navigateToCart: LiveData<Event<Boolean>>
        get() = _navigateToCart

    private val _loadedProductItems = MutableLiveData<List<ProductViewItem>>(emptyList())
    val loadedProductItems: LiveData<List<ProductViewItem>>
        get() = _loadedProductItems

    private var currentCartItems: List<CartItem2> = emptyList()

    init {
        loadRecentProducts()
        loadProducts()
        loadTotalQuantity()
    }

    private fun loadRecentProducts() {
        _recentProducts.value = recentProductRepository.findAll(RECENT_PRODUCTS_LIMIT)
    }

    private fun loadProducts() {
        val productResponse =
            productRepository.getProducts(
                category = CATEGORY_UNDEFINED,
                page = pageNumber++,
                size = PAGE_SIZE,
                sort = DEFAULT_SORT_ORDER,
            )

        val products = productResponse.getOrNull()?.products ?: emptyList()
        currentCartItems = cartRepository.getCartItems(0, totalQuantity.value ?: 0, "asc")
            .getOrNull()?.cartItems ?: emptyList()

        val newItems =
            products.map { product ->
                val quantity =
                    currentCartItems.firstOrNull { it.product.id == product.id }?.quantity ?: 0
                ProductViewItem(product, quantity)
            }
        _canLoadMore.value = productResponse.getOrNull()?.last?.not() ?: false

        _loadedProductItems.value = loadedProductItems.value?.plus(newItems)
        _shoppingUiState.value = UIState.Success(loadedProductItems.value ?: emptyList())
    }

    fun updateData() {
        loadRecentProducts()

        currentCartItems =
            cartRepository.getCartItems(0, totalQuantity.value ?: 0, "asc").getOrNull()?.cartItems
                ?: emptyList()
        _totalQuantity.value = cartRepository.getCartTotalQuantity().getOrNull()?.quantity ?: 0

        currentCartItems.forEach {
            println("${it.product.name} ${it.quantity}")
        }

        val updatedProductItems =
            currentCartItems.map { cartItem ->
                ProductViewItem(cartItem.product, cartItem.quantity)
            }

        loadedProductItems.value?.forEachIndexed { index, loadedItem ->
            if (loadedItem.quantity > 0 && currentCartItems.firstOrNull { it.product.id == loadedItem.product.id } == null) {
                val productItem = ProductViewItem(loadedItem.product, 0)
                (loadedProductItems.value as MutableList)[index] = productItem
            }
        }

        updatedProductItems.forEach { updatedItem ->
            val position =
                loadedProductItems.value?.indexOfFirst { loadedItem ->
                    loadedItem.product.id == updatedItem.product.id
                }
            if (position != -1 && position != null) {
                (loadedProductItems.value as MutableList)[position] = updatedItem
            }
        }
        _loadedProductItems.value = loadedProductItems.value?.toList()
        println("changed : ${currentCartItems}")
    }

    private fun loadTotalQuantity() {
        _totalQuantity.value = cartRepository.getCartTotalQuantity().getOrNull()?.quantity ?: 0
    }

    override fun onLoadMoreButtonClick() {
        loadProducts()
    }

    override fun onShoppingCartButtonClick() {
        _navigateToCart.value = Event(true)
    }

    override fun onPlusButtonClick(product: Product2) {
        _totalQuantity.value = totalQuantity.value?.plus(1)
        cartRepository.addCartItem(product.id, 1).getOrNull()
        currentCartItems = cartRepository.getCartItems(0, totalQuantity.value ?: 0, "asc").getOrNull()?.cartItems
            ?: emptyList()

        updateLoadedProductQuantity(product, 1)
    }

    private fun updateLoadedProductQuantity(
        product: Product2,
        quantity: Int,
    ) {
        val productItem = ProductViewItem(product, quantity)
        _updatedProductItem.value = productItem

        val position = _loadedProductItems.value?.indexOfFirst { it.product.id == product.id }
        if (position != null) {
            (loadedProductItems.value as MutableList)[position] = productItem
        }
    }

    override fun onProductClick(productId: Int) {
        _navigateToDetail.value = Event(productId)
    }

    override fun onQuantityPlusButtonClick(productId: Int) {
        val product = productRepository.getProductById(productId).getOrNull() ?: return
        val currentCartItem = currentCartItems.firstOrNull { it.product.id == product.id }
        val quantity = currentCartItem?.quantity ?: 0
        val cartItemId = currentCartItem?.cartItemId ?: return
        _totalQuantity.value = totalQuantity.value?.plus(1)
        cartRepository.updateCartItem(cartItemId, quantity + 1).getOrNull()

        currentCartItems = cartRepository.getCartItems(0, totalQuantity.value ?: 0, "asc").getOrNull()?.cartItems
            ?: emptyList()
        updateLoadedProductQuantity(product, quantity + 1)
    }

    override fun onQuantityMinusButtonClick(productId: Int) {
        val product = productRepository.getProductById(productId).getOrNull() ?: return
        val currentCartItem = currentCartItems.firstOrNull { it.product.id == product.id }
        val quantity = currentCartItem?.quantity ?: 0
        val cartItemId = currentCartItem?.cartItemId ?: return
        val position = currentCartItems.indexOfFirst { it.cartItemId == cartItemId }
        _totalQuantity.value = totalQuantity.value?.minus(1)?.coerceAtLeast(0)
        if (quantity == 1) {
            val result = cartRepository.deleteCartItem(cartItemId).getOrNull()
            currentCartItems = cartRepository.getCartItems(0, totalQuantity.value ?: 0, "asc").getOrNull()?.cartItems
                ?: emptyList()
        } else {
            cartRepository.updateCartItem(cartItemId, quantity - 1).getOrNull()
            currentCartItems = cartRepository.getCartItems(0, totalQuantity.value ?: 0, "asc").getOrNull()?.cartItems
                ?: emptyList()
        }

        updateLoadedProductQuantity(product, quantity - 1)
    }

    companion object {
        private val CATEGORY_UNDEFINED: String? = null
        private const val DEFAULT_SORT_ORDER = "asc"
        private const val PAGE_SIZE = 20
        private const val RECENT_PRODUCTS_LIMIT = 10
    }
}

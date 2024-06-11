package woowacourse.shopping.presentation.ui.shopping

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentRepository
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.model.ProductModel
import woowacourse.shopping.presentation.ui.model.toUiModel
import woowacourse.shopping.presentation.util.Event

class ShoppingViewModel(
    private val productRepository: ProductRepository,
    private val recentRepository: RecentRepository,
    private val cartRepository: CartRepository,
) : ViewModel(), ShoppingHandler {
    private var currentPage: Int = 0

    private var cartItems: List<Cart> = listOf()

    private val _cartItemQuantity = MutableLiveData<Int>()
    val cartItemQuantity: LiveData<Int> get() = _cartItemQuantity

    private val _recentProducts = MutableLiveData<UiState<List<ProductModel>>>(UiState.Loading)
    val recentProducts: LiveData<UiState<List<ProductModel>>> get() = _recentProducts

    private val _shoppingProducts = MutableLiveData<UiState<List<ProductModel>>>(UiState.Loading)
    val shoppingProducts: LiveData<UiState<List<ProductModel>>> get() = _shoppingProducts

    private val _error = MutableLiveData<Event<ShoppingError>>()

    val error: LiveData<Event<ShoppingError>> get() = _error

    private val _moveEvent = MutableLiveData<Event<FromShoppingToScreen>>()

    val moveEvent: LiveData<Event<FromShoppingToScreen>> get() = _moveEvent

    private val shoppingProductsData
        get() = (shoppingProducts.value as? UiState.Success)?.data ?: emptyList()

    val showSkeleton: LiveData<Boolean> = shoppingProducts.map { it is UiState.Loading }

    init {
        fetchInitialCartProducts()
        fetchRecentProducts()
    }

    private fun fetchCartCount() {
        viewModelScope.launch {
            cartRepository.getCount()
                .onSuccess { cartCount ->
                    _cartItemQuantity.value = cartCount
                }
                .onFailure { }
        }
    }

    private fun fetchInitialCartProducts() {
        viewModelScope.launch {
            cartRepository.loadAll()
                .onSuccess { carts ->
                    cartItems = carts
                    _cartItemQuantity.value = carts.sumOf { it.quantity }
                    fetchInitialProducts(carts)
                }
                .onFailure {
                    _error.value = Event(ShoppingError.CartItemsNotFound)
                }
        }
    }

    private fun fetchInitialProducts(carts: List<Cart>) {
        viewModelScope.launch {
            productRepository.load(currentPage, PRODUCT_PAGE_SIZE)
                .onSuccess { result ->
                    currentPage++
                    addProductModels(result, carts)
                }.onFailure {
                    _error.value = Event(ShoppingError.ProductItemsNotFound)
                }
        }
    }

    private fun addProductModels(
        products: List<Product>,
        carts: List<Cart>,
    ) {
        val cartMap = carts.associateBy { it.product.id }
        val newProductModels =
            products.map { product ->
                val count = cartMap[product.id]?.quantity ?: 0
                product.toUiModel(quantity = count)
            }
        val currentProductModels =
            (_shoppingProducts.value as? UiState.Success)?.data ?: emptyList()
        val combinedProductModels = currentProductModels + newProductModels
        _shoppingProducts.value = UiState.Success(combinedProductModels)
    }

    fun fetchRecentProducts() {
        viewModelScope.launch {
            recentRepository.loadAll()
                .onSuccess { recentViewed ->
                    _recentProducts.value = UiState.Success(recentViewed.map { it.toUiModel() })
                }.onFailure {
                    _error.value = Event(ShoppingError.RecentProductItemsNotFound)
                }
        }
    }

    fun updateProductQuantity(
        productId: Long,
        newQuantity: Int,
    ) {
        fetchCartCount()
        val productIndex = shoppingProductsData.indexOfFirst { it.id == productId }
        if (productIndex == -1) return

        cartItems =
            if (newQuantity == 0) {
                cartItems.filter { it.product.id != productId }
            } else {
                cartItems.map { cartItem ->
                    if (cartItem.product.id == productId) {
                        cartItem.copy(quantity = newQuantity)
                    } else {
                        cartItem
                    }
                }
            }

        val updatedProducts = shoppingProductsData.map { if (it.id == productId) it.copy(quantity = newQuantity) else it }
        _shoppingProducts.value = UiState.Success(updatedProducts)
    }

    override fun onProductItemClick(productId: Long) {
        val cartId = cartItems.find { it.product.id == productId }?.cartId ?: -1
        _moveEvent.value = Event(FromShoppingToScreen.ProductDetail(productId, cartId))
    }

    override fun onLoadMoreClick() {
        viewModelScope.launch(Dispatchers.IO) {
            productRepository.load(currentPage, PRODUCT_PAGE_SIZE)
                .onSuccess { result ->
                    currentPage++
                    addProductModels(result, cartItems)
                }
                .onFailure {
                    _error.value = Event(ShoppingError.AllProductsLoaded)
                }
        }
    }

    override fun onCartMenuItemClick() {
        _moveEvent.value = Event(FromShoppingToScreen.Cart)
    }

    override fun onPlusButtonClick(productId: Long) {
        val selectedProduct = shoppingProductsData.find { it.id == productId } ?: return
        val initialCount = 1

        viewModelScope.launch {
            cartRepository.saveNewCartItem(productId, initialCount)
                .onSuccess { newCartId ->
                    val updatedProduct = selectedProduct.copy(quantity = initialCount)
                    updateProductUI(updatedProduct)
                    productRepository.loadById(productId)
                        .onSuccess {
                            cartItems = cartItems.plus(Cart(newCartId, it, 1))
                        }

                    _cartItemQuantity.value = _cartItemQuantity.value?.plus(1)
                }
                .onFailure {
                    _error.value = Event(ShoppingError.CartItemsNotModified)
                }
        }
    }

    override fun onDecreaseQuantity(productId: Long) {
        val selectedProduct = shoppingProductsData.find { it.id == productId } ?: return
        val newQuantity = selectedProduct.quantity - DECREMENT_AMOUNT
        val cartItem = cartItems.find { it.product.id == productId } ?: return

        viewModelScope.launch {
            cartRepository.updateCartItemQuantity(cartItem.cartId, newQuantity)
                .onSuccess {
                    val updatedProduct = selectedProduct.copy(quantity = newQuantity)
                    updateProductUI(updatedProduct)

                    if (newQuantity == 0) {
                        cartItems = cartItems.filter { it.cartId != cartItem.cartId }
                    } else {
                        val updatedCartProduct = cartItem.copy(quantity = newQuantity)
                        updateCartItem(updatedCartProduct)
                    }
                    _cartItemQuantity.value = _cartItemQuantity.value?. minus(1)
                }
                .onFailure {
                    _error.value = Event(ShoppingError.CartItemsNotModified)
                }
        }
    }

    override fun onIncreaseQuantity(productId: Long) {
        val selectedProduct = shoppingProductsData.find { it.id == productId } ?: return
        val newQuantity = selectedProduct.quantity + INCREMENT_AMOUNT
        val cartItem = cartItems.find { it.product.id == productId } ?: return

        viewModelScope.launch {
            cartRepository.updateCartItemQuantity(cartItem.cartId, newQuantity)
                .onSuccess {
                    val updatedProduct = selectedProduct.copy(quantity = newQuantity)
                    updateProductUI(updatedProduct)

                    val updatedCartProduct = cartItem.copy(quantity = newQuantity)
                    updateCartItem(updatedCartProduct)

                    _cartItemQuantity.value = _cartItemQuantity.value?.plus(1)
                }
                .onFailure {
                    _error.value = Event(ShoppingError.CartItemsNotModified)
                }
        }
    }

    private fun updateProductUI(updatedProduct: ProductModel) {
        val updatedProductsData =
            shoppingProductsData.map {
                if (it.id == updatedProduct.id) updatedProduct else it
            }
        _shoppingProducts.value = UiState.Success(updatedProductsData)
    }

    private fun updateCartItem(updatedCartProduct: Cart) {
        val updatedCartProducts =
            cartItems.map {
                if (it.cartId == updatedCartProduct.cartId) updatedCartProduct else it
            }
        cartItems = updatedCartProducts
    }

    companion object {
        const val PRODUCT_PAGE_SIZE = 20
        const val INCREMENT_AMOUNT = 1
        const val DECREMENT_AMOUNT = 1

        class Factory(
            private val productRepository: ProductRepository,
            private val recentRepository: RecentRepository,
            private val cartRepository: CartRepository,
        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ShoppingViewModel(
                    productRepository,
                    recentRepository,
                    cartRepository,
                ) as T
            }
        }
    }
}

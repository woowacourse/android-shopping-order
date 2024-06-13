package woowacourse.shopping.presentation.ui.shopping

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.mapper.toUiModel
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentRepository
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.model.ProductModel
import woowacourse.shopping.presentation.util.Event

class ShoppingViewModel(
    private val productRepository: ProductRepository,
    private val recentRepository: RecentRepository,
    private val cartRepository: CartRepository,
) : ViewModel(), ShoppingHandler {
    private var currentPage: Int = 0

    private var cartItems: List<Cart> = listOf()

    private val _recentProducts = MutableLiveData<UiState<List<ProductModel>>>(UiState.Loading)
    val recentProducts: LiveData<UiState<List<ProductModel>>> get() = _recentProducts

    private val _shoppingProducts = MutableLiveData<UiState<List<ProductModel>>>(UiState.Loading)
    val shoppingProducts: LiveData<UiState<List<ProductModel>>> get() = _shoppingProducts

    private val _error = MutableLiveData<Event<ShoppingError>>()

    val cartItemQuantity: LiveData<Int> =
        shoppingProducts.map { state ->
            when (state) {
                is UiState.Success -> state.data.sumOf { it.quantity }
                else -> 0
            }
        }

    val error: LiveData<Event<ShoppingError>> get() = _error

    private val _moveEvent = MutableLiveData<Event<FromShoppingToScreen>>()

    val moveEvent: LiveData<Event<FromShoppingToScreen>> get() = _moveEvent

    private val shoppingProductsData
        get() = (shoppingProducts.value as? UiState.Success)?.data ?: emptyList()

    val showSkeleton: LiveData<Boolean> = shoppingProducts.map { it is UiState.Loading }

    init {
        fetchProducts()
        fetchRecentProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            runCatching {
                cartItems = cartRepository.loadAll().getOrThrow()
                val products = productRepository.load(currentPage, PRODUCT_PAGE_SIZE).getOrThrow()
                currentPage++
                updateProductModels(products)
            }.onFailure {
                _error.value = Event(ShoppingError.ProductNotLoaded)
            }
        }
    }

    private fun updateProductModels(products: List<Product>) {
        val cartMap = cartItems.associateBy { it.product.id }
        val newProductModels =
            products.map { product ->
                val count = cartMap[product.id]?.quantity ?: 0
                product.toUiModel(quantity = count)
            }

        val combinedProductModels = shoppingProductsData + newProductModels
        _shoppingProducts.value = UiState.Success(combinedProductModels)
    }

    fun fetchRecentProducts() {
        viewModelScope.launch {
            recentRepository.loadAll()
                .map { recentViewed -> recentViewed.map { it.toUiModel() } }
                .onSuccess { recentViewedModel ->
                    _recentProducts.value = UiState.Success(recentViewedModel)
                }.onFailure {
                    _error.value = Event(ShoppingError.RecentProductItemsNotFound)
                }
        }
    }

    fun updateProductQuantity(
        productId: Long,
        newQuantity: Int,
    ) {
        val productIndex = shoppingProductsData.indexOfFirst { it.id == productId }
        if (productIndex == -1) return

        if (newQuantity == 0) {
            cartItems = cartItems.filter { it.product.id != productId }
        } else {
            val existingCartItem = cartItems.find { it.product.id == productId }
            if (existingCartItem == null) {
                updateAllCartItems()
            } else {
                updateCartItemWithProductId(productId, newQuantity)
            }
        }
        val updatedProducts = shoppingProductsData.map { if (it.id == productId) it.copy(quantity = newQuantity) else it }
        _shoppingProducts.value = UiState.Success(updatedProducts)
    }

    private fun updateAllCartItems() {
        viewModelScope.launch {
            cartRepository.loadAll()
                .onSuccess { cartItems = it }
                .onFailure { _error.value = Event(ShoppingError.CartItemsNotModified) }
        }
    }

    private fun updateCartItemWithProductId(
        productId: Long,
        newQuantity: Int,
    ) {
        cartItems =
            cartItems.map { cartItem ->
                if (cartItem.product.id == productId) {
                    cartItem.copy(quantity = newQuantity)
                } else {
                    cartItem
                }
            }
    }

    override fun navigateToDetail(productId: Long) {
        val cartId = cartItems.find { it.product.id == productId }?.cartId ?: -1
        _moveEvent.value = Event(FromShoppingToScreen.ProductDetail(productId, cartId))
    }

    override fun onLoadMoreProducts() {
        viewModelScope.launch {
            productRepository.load(currentPage, PRODUCT_PAGE_SIZE)
                .onSuccess { result ->
                    currentPage++
                    updateProductModels(result)
                }
                .onFailure {
                    _error.value = Event(ShoppingError.AllProductsLoaded)
                }
        }
    }

    override fun navigateToCart() {
        _moveEvent.value = Event(FromShoppingToScreen.Cart)
    }

    override fun addProductToCart(productId: Long) {
        val selectedProduct = shoppingProductsData.find { it.id == productId } ?: return
        val updatedProduct = selectedProduct.copy(quantity = INITIAL_COUNT)

        viewModelScope.launch {
            runCatching {
                val newCartId = cartRepository.saveNewCartItem(productId, INITIAL_COUNT).getOrThrow()
                val product = productRepository.loadById(productId).getOrThrow()

                updateProductModel(updatedProduct)
                cartItems = cartItems.plus(Cart(newCartId, product, INITIAL_COUNT))
            }.onFailure {
                _error.value = Event(ShoppingError.CartItemsNotModified)
            }
        }
    }

    override fun decreaseQuantity(productId: Long) {
        val selectedProduct = shoppingProductsData.find { it.id == productId } ?: return
        val newQuantity = selectedProduct.quantity - DECREMENT_AMOUNT
        val cartItem = cartItems.find { it.product.id == productId } ?: return

        viewModelScope.launch {
            cartRepository.updateCartItemQuantity(cartItem.cartId, newQuantity)
                .onSuccess {
                    val updatedProduct = selectedProduct.copy(quantity = newQuantity)
                    updateProductModel(updatedProduct)

                    if (newQuantity == 0) {
                        cartItems = cartItems.filter { it.cartId != cartItem.cartId }
                    } else {
                        val updatedCartProduct = cartItem.copy(quantity = newQuantity)
                        updateCartItem(updatedCartProduct)
                    }
                }
                .onFailure {
                    _error.value = Event(ShoppingError.CartItemsNotModified)
                }
        }
    }

    override fun increaseQuantity(productId: Long) {
        val selectedProduct = shoppingProductsData.find { it.id == productId } ?: return
        val newQuantity = selectedProduct.quantity + INCREMENT_AMOUNT
        val cartItem = cartItems.find { it.product.id == productId } ?: return

        viewModelScope.launch {
            cartRepository.updateCartItemQuantity(cartItem.cartId, newQuantity)
                .onSuccess {
                    val updatedProduct = selectedProduct.copy(quantity = newQuantity)
                    updateProductModel(updatedProduct)

                    val updatedCartProduct = cartItem.copy(quantity = newQuantity)
                    updateCartItem(updatedCartProduct)
                }
                .onFailure {
                    _error.value = Event(ShoppingError.CartItemsNotModified)
                }
        }
    }

    private fun updateProductModel(updatedProduct: ProductModel) {
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
        const val INITIAL_COUNT = 1

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

package woowacourse.shopping.presentation.ui.shopping

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.product.ProductRepositoryImpl
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.ProductListItem
import woowacourse.shopping.domain.ProductListItem.ShoppingProductItem.Companion.fromProductsAndCarts
import woowacourse.shopping.domain.RecentProductItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentRepository
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.util.Event

class ShoppingViewModel(
    private val productRepository: ProductRepository = ProductRepositoryImpl(),
    private val recentRepository: RecentRepository,
    private val cartRepository: CartRepository,
) : ViewModel(), ShoppingHandler {
    private var currentPage: Int = 0

    private val cartProducts: MutableList<Cart> = mutableListOf()

    private val _cartItemQuantity = MutableLiveData<Int>()
    val cartItemQuantity: LiveData<Int> get() = _cartItemQuantity

    private val _recentProducts = MutableLiveData<UiState<List<RecentProductItem>>>(UiState.Loading)

    val recentProducts: LiveData<UiState<List<RecentProductItem>>> get() = _recentProducts

    private val _shoppingProducts =
        MutableLiveData<UiState<List<ProductListItem.ShoppingProductItem>>>(UiState.Loading)

    val shoppingProducts: LiveData<UiState<List<ProductListItem.ShoppingProductItem>>> get() = _shoppingProducts

    private val shoppingProductItems = mutableListOf<ProductListItem.ShoppingProductItem>()

    private val _error = MutableLiveData<Event<ShoppingError>>()

    val error: LiveData<Event<ShoppingError>> get() = _error

    private val _moveEvent = MutableLiveData<Event<FromShoppingToScreen>>()

    val moveEvent: LiveData<Event<FromShoppingToScreen>> get() = _moveEvent

    fun loadInitialShoppingItems() {
        if (shoppingProducts.value !is UiState.Success<List<ProductListItem.ShoppingProductItem>>) {
            Log.e("ㅌㅅㅌ", "데이터 불러옴")
//            val handler = Handler(Looper.getMainLooper())
            fetchInitialRecentProducts()
//            handler.postDelayed({
            fetchCartCount()
            fetchInitialCartProducts()
//            }, 1000)
        }
    }

    fun fetchInitialRecentProducts() {
        recentRepository.loadAll().onSuccess {
            _recentProducts.value = UiState.Success(it)
        }.onFailure {
            _error.value = Event(ShoppingError.RecentProductItemsNotFound)
        }
    }

    fun fetchCartCount() {
        cartRepository.getCount(
            onSuccess = { totalCartItemsQuantity ->
                _cartItemQuantity.value = totalCartItemsQuantity
            },
            onFailure = {},
        )
    }

    private fun fetchInitialCartProducts() {
        cartRepository.load(
            startPage = 0,
            pageSize = _cartItemQuantity.value ?: 0,
            onSuccess = { cartItems, _ ->
                Log.e("ㅌㅅㅌ", "fetchInitialCartProducts -> success $cartItems")
                cartProducts.addAll(cartItems)
                fetchInitialProducts(cartItems)
            },
            onFailure = {
                Log.e("ㅌㅅㅌ", "fetchInitialCartProducts -> fail")
                _error.value = Event(ShoppingError.CartItemsNotFound)
            },
        )
    }

    private fun fetchInitialProducts(carts: List<Cart>) {
        productRepository.load(
            currentPage,
            PAGE_SIZE,
            onSuccess = { products ->
                currentPage++
                addShoppingProducts(products, carts)
            },
            onFailure = {
                _error.value = Event(ShoppingError.ProductItemsNotFound)
            },
        )
    }

    private fun fetchProductForNewPage() {
        productRepository.load(currentPage, PAGE_SIZE, onSuccess = { products ->
            currentPage++
            addShoppingProducts(products, cartProducts)
        }, onFailure = {
            _error.value = Event(ShoppingError.AllProductsLoaded)
        })
    }

    private fun addShoppingProducts(
        products: List<Product>,
        carts: List<Cart>,
    ) {
        val newShoppingProducts = fromProductsAndCarts(products, carts)
        shoppingProductItems.addAll(newShoppingProducts)
        _shoppingProducts.value = UiState.Success(shoppingProductItems)
    }

    private fun modifyShoppingProductQuantity(
        cartId: Long,
        productId: Long,
        resultQuantity: Int,
    ) {
        val productIndex = shoppingProductItems.indexOfFirst { it.id == productId }
        val updatedProduct =
            shoppingProductItems[productIndex].copy(
                quantity = resultQuantity,
                cartId = cartId,
            )
        shoppingProductItems[productIndex] = updatedProduct
        _shoppingProducts.value = UiState.Success(shoppingProductItems)
    }

    fun updateProductQuantity(
        productId: Long,
        newQuantity: Int,
    ) {
        val productIndex = shoppingProductItems.indexOfFirst { it.id == productId }
        if (productIndex != -1) {
            var cartId = cartProducts.find { it.product.id == productId }?.cartId ?: -1
            if (newQuantity == 0) cartId = -1
            val updatedProduct =
                shoppingProductItems[productIndex].copy(
                    cartId = cartId,
                    quantity = newQuantity,
                )
            shoppingProductItems[productIndex] = updatedProduct
            _shoppingProducts.value = UiState.Success(shoppingProductItems)
        }
    }

    override fun onProductItemClick(productId: Long) {
        val cartId = cartProducts.find { it.product.id == productId }?.cartId ?: -1
        val quantity = shoppingProductItems.find { it.id == productId }?.quantity ?: 0
        _moveEvent.value = Event(FromShoppingToScreen.ProductDetail(productId, cartId, quantity))
    }

    override fun onCartMenuItemClick() {
        _moveEvent.value = Event(FromShoppingToScreen.Cart)
    }

    override fun onLoadMoreClick() {
        fetchProductForNewPage()
    }

    override fun onDecreaseQuantity(item: ProductListItem.ShoppingProductItem?) {
        item?.let { product ->
            cartRepository.updateDecrementQuantity(
                cartId = product.cartId,
                productId = product.id,
                decrementAmount = 1,
                quantity = product.quantity,
                onSuccess = { cartId, resultQuantity ->
                    if (resultQuantity == 0) {
                        modifyShoppingProductQuantity(-1, product.id, resultQuantity)
                    } else {
                        modifyShoppingProductQuantity(cartId, product.id, resultQuantity)
                    }
                },
                onFailure = {
                    _error.value = Event(ShoppingError.CartItemsNotModified)
                },
            )
        }
    }

    override fun onIncreaseQuantity(item: ProductListItem.ShoppingProductItem?) {
        item?.let { product ->
            cartRepository.updateIncrementQuantity(
                cartId = product.cartId,
                productId = product.id,
                incrementAmount = 1,
                quantity = product.quantity,
                onSuccess = { cartId, incrementAmount ->
                    modifyShoppingProductQuantity(cartId, product.id, incrementAmount)
                },
                onFailure = {
                    _error.value = Event(ShoppingError.CartItemsNotModified)
                },
            )
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}

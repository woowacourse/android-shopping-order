package woowacourse.shopping.presentation.ui.shopping

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
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

    private var cartProducts: List<Cart> = emptyList()

    private val _cartItemQuantity = MutableLiveData<Int>()
    val cartItemQuantity: LiveData<Int> get() = _cartItemQuantity

    private val _recentProducts = MutableLiveData<UiState<List<RecentProductItem>>>(UiState.Loading)

    val recentProducts: LiveData<UiState<List<RecentProductItem>>> get() = _recentProducts

    private val _shoppingProducts =
        MutableLiveData<UiState<List<ProductListItem.ShoppingProductItem>>>(UiState.Loading)

    val shoppingProducts: LiveData<UiState<List<ProductListItem.ShoppingProductItem>>> get() = _shoppingProducts

    private val _error = MutableLiveData<Event<ShoppingError>>()

    val error: LiveData<Event<ShoppingError>> get() = _error

    private val _moveEvent = MutableLiveData<Event<FromShoppingToScreen>>()

    val moveEvent: LiveData<Event<FromShoppingToScreen>> get() = _moveEvent

    fun loadInitialShoppingItems() {
        if (shoppingProducts.value !is UiState.Success) {
//            val handler = Handler(Looper.getMainLooper())
//            handler.postDelayed({
            fetchAllRecentProducts()
            fetchCartItemCount()
            fetchInitialCartProducts()
//            }, 1000)
        }
    }

    fun fetchAllRecentProducts() {
        recentRepository.loadAll().onSuccess {
            _recentProducts.value = UiState.Success(it)
        }.onFailure {
            _error.value = Event(ShoppingError.RecentProductItemsNotFound)
        }
    }

    fun fetchCartItemCount() {
        cartRepository.getCount(
            onSuccess = { _cartItemQuantity.value = it },
            onFailure = { Event(ShoppingError.CartItemCountNotFound) },
        )
    }

    private fun fetchInitialCartProducts() {
        cartRepository.loadAll(
            onSuccess = { cartItems ->
                cartProducts = cartItems
                fetchProductsByPage()
            },
            onFailure = { _error.value = Event(ShoppingError.CartItemsNotFound) },
        )
    }

    private fun fetchProductsByPage() {
        viewModelScope.launch {
            productRepository.load(
                currentPage,
                PAGE_SIZE,
            ).onSuccess { productModel ->
                currentPage++
                addShoppingProducts(productModel.products, productModel.isLast)
            }.onFailure {
                Log.d("ㅌㅅㅌ", "error : ${it.message}")
                _error.value = Event(ShoppingError.ProductItemsNotFound)
            }
        }
    }

    private fun addShoppingProducts(
        products: List<Product>,
        isLast: Boolean,
    ) {
        if (isLast && products.isEmpty()) {
            _error.value = Event(ShoppingError.AllProductsLoaded)
            return
        }
        val state = shoppingProducts.value ?: return
        val originShoppingProducts =
            when (state) {
                is UiState.Success -> state.data.toMutableList()
                is UiState.Loading -> mutableListOf()
            }
        val newShoppingProducts = fromProductsAndCarts(products, cartProducts)
        val resultShoppingProducts = originShoppingProducts.plus(newShoppingProducts)
        _shoppingProducts.value = UiState.Success(resultShoppingProducts)
    }

    override fun onProductItemClick(productId: Long) {
        val state = shoppingProducts.value
        if (state is UiState.Success) {
            val originShoppingProducts = state.data.toMutableList()
            val quantity = originShoppingProducts.find { it.id == productId }?.quantity ?: 0
            _moveEvent.value =
                Event(FromShoppingToScreen.ProductDetail(productId, quantity))
        }
    }

    override fun onCartMenuItemClick() {
        _moveEvent.value = Event(FromShoppingToScreen.Cart)
    }

    override fun onLoadMoreClick() {
        fetchProductsByPage()
    }

    override fun onDecreaseQuantity(product: ProductListItem.ShoppingProductItem?) {
        product ?: return
        cartRepository.modifyExistCartQuantity(
            productId = product.id,
            quantityDelta = -1,
            onSuccess = { _, resultQuantity ->
                setNewShoppingProductQuantity(product.id, resultQuantity)
            },
            onFailure = {
                _error.value = Event(ShoppingError.CartItemsNotModified)
            },
        )
    }

    override fun onIncreaseQuantity(product: ProductListItem.ShoppingProductItem?) {
        product ?: return
        cartRepository.modifyExistCartQuantity(
            productId = product.id,
            quantityDelta = 1,
            onSuccess = { _, incrementAmount ->
                setNewShoppingProductQuantity(product.id, incrementAmount)
            },
            onFailure = {
                _error.value = Event(ShoppingError.CartItemsNotModified)
            },
        )
    }

    fun setNewShoppingProductQuantity(
        productId: Long,
        newQuantity: Int,
    ) {
        val state = shoppingProducts.value
        if (state !is UiState.Success) return
        val updatedShoppingProduct =
            state.data.map { shoppingProduct ->
                if (shoppingProduct.id == productId) {
                    shoppingProduct.copy(quantity = newQuantity)
                } else {
                    shoppingProduct
                }
            }
        _shoppingProducts.value = UiState.Success(updatedShoppingProduct)
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}

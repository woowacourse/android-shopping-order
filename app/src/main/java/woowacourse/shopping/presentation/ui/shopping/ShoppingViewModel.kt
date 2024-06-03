package woowacourse.shopping.presentation.ui.shopping

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.cart.local.LocalCartDataSourceImpl
import woowacourse.shopping.data.cart.remote.RemoteCartDataSource
import woowacourse.shopping.data.local.AppDatabase
import woowacourse.shopping.data.product.ProductRepositoryImpl
import woowacourse.shopping.data.recent.RecentProductRepositoryImpl
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentRepository
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.model.ProductModel
import woowacourse.shopping.presentation.ui.model.toUiModel
import woowacourse.shopping.presentation.util.Event

class ShoppingViewModel(
    private val productRepository: ProductRepository = ProductRepositoryImpl(),
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

    val showSkeleton: LiveData<Boolean>
        get() = shoppingProducts.switchMap { MutableLiveData(it is UiState.Loading) }

    init {
        fetchCartCount()
        fetchInitialCartProducts()
        fetchRecentProducts()
    }

    private fun fetchCartCount() {
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
            pageSize = cartItemQuantity.value ?: 0,
            onSuccess = { cartItems, _ ->
                this.cartItems = cartItems
                fetchInitialProducts(cartItems)
            },
            onFailure = {
                _error.value = Event(ShoppingError.CartItemsNotFound)
            },
        )
    }

    private fun fetchInitialProducts(carts: List<Cart>) {
        productRepository.load(
            currentPage,
            PRODUCT_PAGE_SIZE,
            onSuccess = { products ->
                currentPage++
                addProductModels(products, carts)
            },
            onFailure = {
                _error.value = Event(ShoppingError.ProductItemsNotFound)
            },
        )
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

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val currentProductModels =
                (_shoppingProducts.value as? UiState.Success)?.data ?: emptyList()
            val combinedProductModels = currentProductModels + newProductModels
            _shoppingProducts.value = UiState.Success(combinedProductModels)
        }, 1000) // 임시용
    }

    fun fetchRecentProducts() {
        recentRepository.loadAll()
            .onSuccess { recentViewed ->
                _recentProducts.value = UiState.Success(recentViewed.map { it.toUiModel() })
            }.onFailure {
                _error.value = Event(ShoppingError.RecentProductItemsNotFound)
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

        val updatedProducts =
            shoppingProductsData.map {
                if (it.id == productId) it.copy(quantity = newQuantity) else it
            }
        _shoppingProducts.value = UiState.Success(updatedProducts)
    }

    override fun onProductItemClick(productId: Long) {
        val cartId = cartItems.find { it.product.id == productId }?.cartId ?: -1
        val quantity = shoppingProductsData.find { it.id == productId }?.quantity ?: 0
        _moveEvent.value = Event(FromShoppingToScreen.ProductDetail(productId, cartId, quantity))
    }

    override fun onLoadMoreClick() {
        productRepository.load(
            currentPage,
            PRODUCT_PAGE_SIZE,
            onSuccess = { products ->
                currentPage++
                addProductModels(products, cartItems)
            },
            onFailure = {
                _error.value = Event(ShoppingError.AllProductsLoaded)
            },
        )
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
        const val PRODUCT_PAGE_SIZE = 20

        class Factory : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val recentDao = AppDatabase.instanceOrNull.recentProductDao()
                val cartDao = AppDatabase.instanceOrNull.cartDao()
                return ShoppingViewModel(
                    productRepository = ProductRepositoryImpl(),
                    recentRepository = RecentProductRepositoryImpl(recentDao),
                    cartRepository =
                        CartRepositoryImpl(
                            localCartDataSource = LocalCartDataSourceImpl(cartDao),
                            remoteCartDataSource = RemoteCartDataSource(),
                        ),
                ) as T
            }
        }
    }
}

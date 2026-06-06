package woowacourse.shopping.ui.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.repository.RecentlyViewedProductRepository
import woowacourse.shopping.data.local.userdata.UserDataSource
import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.data.remote.server.apiresult.onFailure
import woowacourse.shopping.data.remote.server.apiresult.onSuccess
import woowacourse.shopping.data.remote.server.repository.CartRepository
import woowacourse.shopping.data.remote.server.repository.ProductRepository
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.Products
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.ui.ViewModelConst

class ShoppingViewModel(
    private val cartRepository: CartRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val productRepository: ProductRepository,
    private val userDataSource: UserDataSource,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            ShoppingUiState(
                notificationAllowed = userDataSource.isNotificationEnable(),
            ),
        )
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<ShoppingEvent>()
    val event = _event.asSharedFlow()
    val recentlyViewedProductsId: StateFlow<List<Long>?> =
        recentlyViewedProductRepository
            .getAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList(),
            )

    val lastViewProductId: StateFlow<Long?> =
        recentlyViewedProductRepository
            .getLatestItem()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = null,
            )

    init {
        viewModelScope.launch {
            recentlyViewedProductsId.collect { ids ->
                if (ids.isNullOrEmpty()) {
                    _uiState.update { it.copy(recentlyViewedProducts = Products()) }
                } else {
                    val recentlyViewedProducts = mutableListOf<Product>()
                    ids.forEach { id ->
                        productRepository.getProduct(id).handleWithSnackBar(
                            errorMessage = ShoppingEvent.Message.RecentProductsLoadFailed,
                        ) {
                            recentlyViewedProducts.add(it)
                        }
                    }
                    _uiState.update {
                        it.copy(
                            recentlyViewedProducts =
                                Products(
                                    recentlyViewedProducts,
                                ),
                        )
                    }
                }
            }
        }
        viewModelScope.launch {
            fetchProducts()
            fetchCart()
        }
    }

    suspend fun fetchProducts() {
        _uiState.update { it.copy(isLoading = true) }
        productRepository
            .getProducts(uiState.value.currentIndex, PAGE_SIZE)
            .handleWithSnackBar { data ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        products = it.products + Products(data),
                    )
                }
            }.onFailure { _, _ ->
                _uiState.update { it.copy(isLoading = false) }
            }
    }

    suspend fun fetchCart() {
        cartRepository
            .getPagedCart(0, ViewModelConst.CART_MAX_COUNT)
            .handleWithSnackBar { data ->
                _uiState.update { it.copy(cart = data) }
            }
    }

    fun addToCart(purchaseProduct: PurchaseProduct) {
        viewModelScope.launch {
            val existingItem =
                _uiState.value.cart.purchaseProducts.find {
                    it.product.id == purchaseProduct.product.id
                }
            if (existingItem != null) {
                updateCountWithID(existingItem.id, existingItem.count + 1)
            } else {
                cartRepository.insert(purchaseProduct).handleWithSnackBar {
                    _event.emit(ShoppingEvent.ShowSnackBar(ShoppingEvent.Message.CartAdded))
                    fetchCart()
                }
            }
        }
    }

    fun updateCountWithID(
        id: Long,
        updateAmount: Int,
    ) {
        viewModelScope.launch {
            val target = _uiState.value.cart.findById(id)
            if (target != null) {
                val nextCount = target.count + updateAmount
                if (nextCount >= 1) {
                    cartRepository
                        .updateCount(target.id, nextCount)
                        .handleWithSnackBar {
                            _event.emit(
                                ShoppingEvent.ShowSnackBar(
                                    ShoppingEvent.Message.QuantityUpdated,
                                ),
                            )
                            fetchCart()
                        }
                }
            }
        }
    }

    fun removeWithID(id: Long) {
        viewModelScope.launch {
            val target = _uiState.value.cart.findById(id)
            if (target != null) {
                cartRepository.deleteCartItem(target.id)
                fetchCart()
            }
        }
    }

    private suspend fun <T> ApiResult<T>.handleWithSnackBar(
        errorMessage: ShoppingEvent.Message? = null,
        onSuccessAction: suspend (T) -> Unit,
    ): ApiResult<T> =
        this
            .onSuccess { onSuccessAction(it) }
            .onFailure { code, msg ->
                val message =
                    errorMessage ?: if (code != null) {
                        ShoppingEvent.Message.NetworkError(code)
                    } else {
                        ShoppingEvent.Message.ExceptionError(msg)
                    }
                _event.emit(ShoppingEvent.ShowSnackBar(message))
            }

    fun updateHistory(product: Product) {
        viewModelScope.launch {
            recentlyViewedProductRepository.updateList(product)
        }
    }

    fun loadMore() {
        val nextIndex = uiState.value.currentIndex + 1
        _uiState.update { it.copy(currentIndex = nextIndex) }
        viewModelScope.launch {
            fetchProducts()
        }
    }

    fun moveToCart() {
        viewModelScope.launch {
            _event.emit(ShoppingEvent.NavigateToCart)
        }
    }

    fun moveToProductDetail(selectedId: Long) {
        viewModelScope.launch {
            _event.emit(
                ShoppingEvent.NavigateToProductDetail(
                    selectedProductId = selectedId,
                    lastViewedProductId = lastViewProductId.value,
                ),
            )
        }
    }

    fun addToCartTrigger(purchaseProduct: PurchaseProduct) {
        viewModelScope.launch {
            _event.emit(
                ShoppingEvent.AddToCart(purchaseProduct),
            )
        }
    }

    fun updateCountTrigger(
        productId: Long,
        updateAmount: Int,
    ) {
        viewModelScope.launch {
            _event.emit(
                ShoppingEvent.UpdateCount(
                    productID = productId,
                    updateAmount = updateAmount,
                ),
            )
        }
    }

    fun removeFromCartTrigger(purchaseProductId: Long) {
        viewModelScope.launch {
            _event.emit(
                ShoppingEvent.RemoveFormCart(purchaseProductId),
            )
        }
    }

    fun loadMoreTrigger() {
        viewModelScope.launch {
            _event.emit(
                ShoppingEvent.LoadMore,
            )
        }
    }

    fun changeNotificationAllow() {
        viewModelScope.launch {
            val currentState = _uiState.value.notificationAllowed
            userDataSource.setNotificationEnable(currentState.not())
            _uiState.update { it.copy(notificationAllowed = currentState.not()) }
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}

class ShoppingViewModelFactory(
    private val cartRepository: CartRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val productRepository: ProductRepository,
    private val userDataSource: UserDataSource,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShoppingViewModel(
                cartRepository,
                recentlyViewedProductRepository,
                productRepository,
                userDataSource,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

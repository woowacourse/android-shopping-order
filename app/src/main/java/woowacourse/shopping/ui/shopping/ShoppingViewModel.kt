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
import woowacourse.shopping.data.local.NotificationSettingStorage
import woowacourse.shopping.data.local.repository.RecentlyViewedProductRepository
import woowacourse.shopping.data.remote.server.apiresult.ApiResult
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
    private val notificationSettingStorage: NotificationSettingStorage,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            ShoppingUiState(
                notificationAllowed = notificationSettingStorage.isNotificationEnabled(),
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
                        when (val result = productRepository.getProduct(id)) {
                            is ApiResult.Success -> recentlyViewedProducts.add(result.data)
                            is ApiResult.Error ->
                                _event.emit(ShoppingEvent.ShowSnackBar("최근 본 상품 목록을 불러오는데 실패했습니다."))

                            is ApiResult.Exception ->
                                _event.emit(ShoppingEvent.ShowSnackBar("최근 본 상품 목록을 불러오는데 실패했습니다."))
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
        when (val result = productRepository.getProducts(uiState.value.currentIndex, PAGE_SIZE)) {
            is ApiResult.Success -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        products = it.products + Products(result.data),
                    )
                }
            }

            is ApiResult.Error ->
                _event.emit(
                    ShoppingEvent.ShowSnackBar("${ViewModelConst.NETWORK_ERROR_LABEL} ${result.code}"),
                )

            is ApiResult.Exception ->
                _event.emit(
                    ShoppingEvent.ShowSnackBar("${ViewModelConst.ERROR_LABEL} ${result.e.message}"),
                )
        }
    }

    suspend fun fetchCart() {
        when (
            val allCartItemResult =
                cartRepository.getPagedCart(0, ViewModelConst.CART_MAX_COUNT)
        ) {
            is ApiResult.Success -> _uiState.update { it.copy(cart = allCartItemResult.data) }
            is ApiResult.Error ->
                _event.emit(
                    ShoppingEvent.ShowSnackBar("${ViewModelConst.NETWORK_ERROR_LABEL} ${allCartItemResult.code}"),
                )

            is ApiResult.Exception ->
                _event.emit(
                    ShoppingEvent.ShowSnackBar("${ViewModelConst.ERROR_LABEL} ${allCartItemResult.e.message}"),
                )
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
                when (val result = cartRepository.insert(purchaseProduct)) {
                    is ApiResult.Success -> {
                        _event.emit(ShoppingEvent.ShowSnackBar(ADD_TO_CART))
                        fetchCart()
                    }

                    is ApiResult.Error ->
                        _event.emit(
                            ShoppingEvent.ShowSnackBar("${ViewModelConst.NETWORK_ERROR_LABEL} ${result.code}"),
                        )

                    is ApiResult.Exception ->
                        _event.emit(
                            ShoppingEvent.ShowSnackBar("${ViewModelConst.ERROR_LABEL} ${result.e.message}"),
                        )
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
                    when (val result = cartRepository.updateCount(target.id, nextCount)) {
                        is ApiResult.Success -> {
                            _event.emit(ShoppingEvent.ShowSnackBar(UPDATE_AMOUNT))
                            fetchCart()
                        }

                        is ApiResult.Error ->
                            _event.emit(
                                ShoppingEvent.ShowSnackBar("${ViewModelConst.NETWORK_ERROR_LABEL} ${result.code}"),
                            )

                        is ApiResult.Exception ->
                            _event.emit(
                                ShoppingEvent.ShowSnackBar("${ViewModelConst.ERROR_LABEL} ${result.e.message}"),
                            )
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
            notificationSettingStorage.setNotificationEnabled(currentState.not())
            _uiState.update { it.copy(notificationAllowed = currentState.not()) }
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
        private const val ADD_TO_CART = "장바구니에 상품을 추가했습니다."
        private const val UPDATE_AMOUNT = "상품의 수량을 변경했습니다."
    }
}

class ShoppingViewModelFactory(
    private val cartRepository: CartRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val productRepository: ProductRepository,
    private val notificationSettingStorage: NotificationSettingStorage,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShoppingViewModel(
                cartRepository,
                recentlyViewedProductRepository,
                productRepository,
                notificationSettingStorage,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

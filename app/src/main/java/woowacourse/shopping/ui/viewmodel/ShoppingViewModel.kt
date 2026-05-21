package woowacourse.shopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.repository.RecentlyViewedProductRepository
import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.data.remote.server.repository.CartRepository
import woowacourse.shopping.data.remote.server.repository.ProductRepository
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.Products
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.ui.state.ShoppingUiState

class ShoppingViewModel(
    private val cartRepository: CartRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingUiState())

    val uiState = _uiState.asStateFlow()

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
                started = SharingStarted.WhileSubscribed(5000),
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
                                _uiState.update {
                                    it.copy(isLoading = false, errorMsg = "${ViewModelConst.NETWORK_ERROR_LABEL}${result.code}")
                                }
                            is ApiResult.Exception ->
                                _uiState.update {
                                    it.copy(isLoading = false, errorMsg = "${ViewModelConst.ERROR_LABEL}${result.e.message}")
                                }
                        }
                    }
                    _uiState.update { it.copy(recentlyViewedProducts = Products(recentlyViewedProducts)) }
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

            is ApiResult.Error -> {
                _uiState.update {
                    it.copy(isLoading = false, errorMsg = "${ViewModelConst.NETWORK_ERROR_LABEL}${result.code}")
                }
            }

            is ApiResult.Exception -> {
                _uiState.update {
                    it.copy(isLoading = false, errorMsg = "${ViewModelConst.ERROR_LABEL}${result.e.message}")
                }
            }
        }
    }

    suspend fun fetchCart() {
        when (val allCartItemResult = cartRepository.getPagedCart(0, ViewModelConst.CART_MAX_COUNT)) {
            is ApiResult.Success -> _uiState.update { it.copy(cart = allCartItemResult.data) }
            is ApiResult.Error ->
                _uiState.update {
                    it.copy(errorMsg = "${ViewModelConst.NETWORK_ERROR_LABEL}${allCartItemResult.code}")
                }
            is ApiResult.Exception ->
                _uiState.update {
                    it.copy(errorMsg = "${ViewModelConst.ERROR_LABEL}${allCartItemResult.e.message}")
                }
        }
    }

    fun addToCart(purchaseProduct: PurchaseProduct) {
        viewModelScope.launch {
            val existingItem =
                _uiState.value.cart.purchaseProducts.find {
                    it.product.id == purchaseProduct.product.id
                }
            if (existingItem != null) {
                when (val result = cartRepository.updateCount(existingItem.id, existingItem.count + 1)) {
                    is ApiResult.Success -> {}
                    is ApiResult.Error ->
                        _uiState.update {
                            it.copy(isLoading = false, errorMsg = "${ViewModelConst.NETWORK_ERROR_LABEL}${result.code}")
                        }
                    is ApiResult.Exception ->
                        _uiState.update {
                            it.copy(isLoading = false, errorMsg = "${ViewModelConst.ERROR_LABEL}${result.e.message}")
                        }
                }
            } else {
                when (val result = cartRepository.insert(purchaseProduct)) {
                    is ApiResult.Success -> fetchCart()
                    is ApiResult.Error ->
                        _uiState.update {
                            it.copy(isLoading = false, errorMsg = "${ViewModelConst.NETWORK_ERROR_LABEL}${result.code}")
                        }
                    is ApiResult.Exception ->
                        _uiState.update {
                            it.copy(isLoading = false, errorMsg = "${ViewModelConst.ERROR_LABEL}${result.e.message}")
                        }
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
                        is ApiResult.Success -> fetchCart()
                        is ApiResult.Error ->
                            _uiState.update {
                                it.copy(isLoading = false, errorMsg = "${ViewModelConst.NETWORK_ERROR_LABEL}${result.code}")
                            }
                        is ApiResult.Exception ->
                            _uiState.update {
                                it.copy(isLoading = false, errorMsg = "${ViewModelConst.ERROR_LABEL}${result.e.message}")
                            }
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

    fun onErrorMsgShown() {
        _uiState.update { it.copy(errorMsg = null) }
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}

class ShoppingViewModelFactory(
    private val cartRepository: CartRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val productRepository: ProductRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShoppingViewModel(
                cartRepository,
                recentlyViewedProductRepository,
                productRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

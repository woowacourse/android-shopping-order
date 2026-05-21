package woowacourse.shopping.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
import woowacourse.shopping.domain.PurchaseProducts
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

    private val _cart = MutableStateFlow(PurchaseProducts())
    val cart = _cart.asStateFlow()

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
                if(ids.isNullOrEmpty()) {
                    _uiState.update { it.copy(recentlyViewedProducts = Products()) }
                } else {
                    val recentlyViewedProducts = ids.map { id ->
                        productRepository.getProduct(id)
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
        val products = productRepository.getProducts(uiState.value.currentIndex, PAGE_SIZE)
        _uiState.update {
            it.copy(
                isLoading = false,
                products = it.products + Products(products),
            )
        }
    }

    suspend fun fetchCart() {
        val allCartItemResult = cartRepository.getPagedCart(0, ViewModelConst.MAX_COUNT)
        when(allCartItemResult) {
            is ApiResult.Success -> {
                _cart.update { allCartItemResult.data }
                _uiState.update { it.copy(cartItemCount = _cart.value.totalCount()) }
            }
            is ApiResult.Error -> ""
            is ApiResult.Exception -> ""
        }
    }

    fun addToCart(purchaseProduct: PurchaseProduct) {
        viewModelScope.launch {
            val existingItem =
                _cart.value.purchaseProducts.find {
                    it.product.id == purchaseProduct.product.id
                }
            if (existingItem != null) {
                cartRepository.updateCount(existingItem.id, existingItem.count + 1)
            } else {
                cartRepository.insert(purchaseProduct)
            }
            fetchCart()
        }
    }

    fun updateCountWithID(
        id: Long,
        updateAmount: Int,
    ) {
        viewModelScope.launch {
            val target = _cart.value.findById(id)
            if (target != null) {
                val nextCount = target.count + updateAmount
                if (nextCount >= 1) {
                    cartRepository.updateCount(target.id, nextCount)
                    fetchCart()
                }
            }
        }
    }

    fun removeWithID(id: Long) {
        viewModelScope.launch {
            val target = _cart.value.findById(id)
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

    companion object {
        private val PAGE_SIZE = 20
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

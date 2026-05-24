package woowacourse.shopping.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.model.PurchaseProduct
import woowacourse.shopping.domain.model.PurchaseProducts
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentlyViewedProductRepository
import woowacourse.shopping.ui.event.UiEvent

class ShoppingViewModel(
    private val cartRepository: CartRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    val recentlyViewedProductIds: StateFlow<List<Long>?> =
        recentlyViewedProductRepository
            .getAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _products = MutableStateFlow<Products>(Products())
    val products: StateFlow<Products> = _products.asStateFlow()

    private val _cart = MutableStateFlow(PurchaseProducts())
    val cart = _cart.asStateFlow()

    private val _cartProductCount = MutableStateFlow(0)
    val cartProductCount = _cartProductCount.asStateFlow()

    val recentlyViewedProducts: StateFlow<Products> =
        combine(recentlyViewedProductIds, products) { productIds, allProducts ->
            val productList =
                productIds?.mapNotNull { productId ->
                    allProducts.findWithId(productId)
                } ?: emptyList()
            Products(productList)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Products()
        )

    val lastViewProductId: StateFlow<Long?> = recentlyViewedProductRepository.getLatestItem()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    init {
        fetchProducts()
        fetchCart()
    }

    fun fetchProducts(page: Int = 0) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = productRepository.getProducts(page, PAGE_SIZE)
                _products.update { it + Products(response) }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchCart() {
        viewModelScope.launch {
            _cartProductCount.update {
                cartRepository.getProductCount()
            }
            _cart.update {
                cartRepository.getAllCartItems(CART_PAGE_SIZE)
            }
        }
    }


    fun addToCart(purchaseProduct: PurchaseProduct) {
        viewModelScope.launch {
            try {
                val existingItem = cart.value.purchaseProducts.find {
                    it.product.id == purchaseProduct.product.id
                }
                if (existingItem != null) {
                    cartRepository.updateCount(existingItem.id, existingItem.count + 1)
                    updateKnownCartItemCount(existingItem.id, existingItem.count + 1)
                    updateKnownCartProductCount(1)
                } else {
                    cartRepository.insert(purchaseProduct)
                    updateKnownCartProductCount(purchaseProduct.count)
                    fetchCart()
                }
                _uiEvent.emit(UiEvent.ShowMessage("장바구니에 담았습니다."))
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("장바구니 담기에 실패했습니다."))
            }
        }
    }

    fun updateCountWithID(
        id: Long,
        updateAmount: Int,
    ) {
        viewModelScope.launch {
            val target = cart.value.findById(id)
            if (target != null) {
                val nextCount = target.count + updateAmount
                if(nextCount >= 1) {
                    cartRepository.updateCount(target.id, nextCount)
                    updateKnownCartItemCount(target.id, nextCount)
                    updateKnownCartProductCount(updateAmount)
                }
            }
        }
    }

    fun removeWithID(id: Long) {
        viewModelScope.launch {
            try {
                val target = cart.value.findById(id)
                if (target != null) {
                    cartRepository.deleteCartItem(target.id)
                    removeKnownCartItem(target.id)
                    updateKnownCartProductCount(-target.count)
                    _uiEvent.emit(UiEvent.ShowMessage("상품을 삭제했습니다."))
                }
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("상품 삭제에 실패했습니다."))
            }
        }
    }

    fun updateHistory(product: Product) {
        viewModelScope.launch {
            recentlyViewedProductRepository.updateList(product)
        }
    }


    fun loadMore() {
        _currentIndex.value++
        fetchProducts(currentIndex.value)
    }

    private fun updateKnownCartItemCount(
        id: Long,
        count: Int,
    ) {
        _cart.update { cart ->
            PurchaseProducts(
                cart.purchaseProducts.map {
                    if (it.id == id) it.copy(count = count) else it
                },
            )
        }
    }

    private fun removeKnownCartItem(id: Long) {
        _cart.update { cart ->
            PurchaseProducts(cart.purchaseProducts.filter { it.id != id })
        }
    }

    private fun updateKnownCartProductCount(amount: Int) {
        _cartProductCount.update { (it + amount).coerceAtLeast(0) }
    }

    companion object {
        private val PAGE_SIZE = 20
        private const val CART_PAGE_SIZE = 5
    }
}

class ShoppingViewModelFactory(
    private val cartRepository: CartRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val productRepository: ProductRepository
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

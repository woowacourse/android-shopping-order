package woowacourse.shopping.ui.recommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.product.Products
import woowacourse.shopping.domain.model.order.PurchaseProduct
import woowacourse.shopping.domain.model.order.PurchaseProducts
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentlyViewedProductRepository
import woowacourse.shopping.ui.event.UiEvent

class RecommendationViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    initialSelectedIds: List<Long> = emptyList(),
) : ViewModel() {
    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<UiEvent> = _uiEvent.receiveAsFlow()

    private val _selectedItemIds = MutableStateFlow<List<Long>>(initialSelectedIds)
    val selectedItemIds = _selectedItemIds.asStateFlow()

    val lastViewProductId: StateFlow<Long?> = recentlyViewedProductRepository.getLatestItem()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val lastViewedProduct: StateFlow<Product?> = lastViewProductId.flatMapLatest { id ->
        flow {
            if (id != null && id != 0L) {
                try {
                    emit(productRepository.getProduct(id))
                } catch (e: Exception) {
                    emit(null)
                }
            } else {
                emit(null)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _allRecommendedProducts = MutableStateFlow(Products(emptyList()))

    private val _allCartItems = MutableStateFlow<PurchaseProducts>(PurchaseProducts())
    val allCartItems = _allCartItems.asStateFlow()

    private val cartProductIds: StateFlow<List<Long>> = allCartItems.map { allCart ->
        allCart.purchaseProducts.map { it.product.id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recommendedProducts: StateFlow<Products> = combine(_allRecommendedProducts, cartProductIds) { products, cartIds ->
        Products(products.products.filter { it.id !in cartIds })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Products(emptyList())
    )

    val totalPrice: StateFlow<Int> = combine(allCartItems, selectedItemIds) { allCart, selectedIds ->
        allCart.purchaseProducts
            .filter { it.id in selectedIds }
            .sumOf { it.totalPrice }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val selectedCount: StateFlow<Int> = selectedItemIds.map {
        it.size
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    init {
        viewModelScope.launch {
            lastViewProductId.collect { id ->
                try {
                    val products = if (id != null && id != 0L) {
                        val product = productRepository.getProduct(id)
                        productRepository.getCategoryProducts(category = product.category)
                    } else {
                        productRepository.getProducts(0, 10)
                    }
                    _allRecommendedProducts.update { Products(products) }
                } catch (e: Exception) {
                    _allRecommendedProducts.update { Products(emptyList()) }
                }
            }
        }

        fetchCart()
    }

    fun fetchCart() {
        viewModelScope.launch {
            _allCartItems.update {
                cartRepository.getAllCartItems(CART_PAGE_SIZE)
            }
        }
    }

    fun addToCart(purchaseProduct: PurchaseProduct) {
        viewModelScope.launch {
            try {
                val existingItem = allCartItems.value.purchaseProducts.find {
                    it.product.id == purchaseProduct.product.id
                }
                if (existingItem != null) {
                    cartRepository.updateCount(existingItem.id, existingItem.count + 1)
                    updateKnownCartItemCount(existingItem.id, existingItem.count + 1)
                } else {
                    cartRepository.insert(purchaseProduct)
                    fetchCart()
                }
                _uiEvent.send(UiEvent.ShowMessage("장바구니에 담았습니다."))
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.ShowMessage("장바구니 담기에 실패했습니다."))
            }
        }
    }

    fun updateCountWithID(
        id: Long,
        updateAmount: Int,
    ) {
        viewModelScope.launch {
            val target = allCartItems.value.findById(id)
            if (target != null) {
                val nextCount = target.count + updateAmount
                if(nextCount >= 1) {
                    cartRepository.updateCount(target.id, nextCount)
                    updateKnownCartItemCount(target.id, nextCount)
                }
            }
        }
    }

    fun removeWithID(id: Long) {
        viewModelScope.launch {
            try {
                val target = allCartItems.value.findById(id)
                if (target != null) {
                    cartRepository.deleteCartItem(target.id)
                    removeKnownCartItem(target.id)
                    _uiEvent.send(UiEvent.ShowMessage("상품을 삭제했습니다."))
                }
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.ShowMessage("상품 삭제에 실패했습니다."))
            }
        }
    }

    private fun updateKnownCartItemCount(
        id: Long,
        count: Int,
    ) {
        _allCartItems.update { cart ->
            PurchaseProducts(
                cart.purchaseProducts.map {
                    if (it.id == id) it.copy(count = count) else it
                },
            )
        }
    }

    private fun removeKnownCartItem(id: Long) {
        _allCartItems.update { cart ->
            PurchaseProducts(cart.purchaseProducts.filter { it.id != id })
        }
    }

    companion object {
        private const val CART_PAGE_SIZE = 5
    }
}

class RecommendationViewModelFactory(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val initialSelectedIds: List<Long> = emptyList(),
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecommendationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecommendationViewModel(
                cartRepository,
                productRepository,
                recentlyViewedProductRepository,
                initialSelectedIds,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

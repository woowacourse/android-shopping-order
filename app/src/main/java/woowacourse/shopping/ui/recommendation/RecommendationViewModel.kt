package woowacourse.shopping.ui.recommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.repository.RecentlyViewedProductRepository
import woowacourse.shopping.data.remote.server.repository.CartRepository
import woowacourse.shopping.data.remote.server.repository.ProductRepository
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.Products
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.domain.PurchaseProducts

class RecommendationViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    initialSelectedIds: List<Long> = emptyList(),
): ViewModel() {

    private val _selectedItemIds = MutableStateFlow<List<Long>>(initialSelectedIds)
    val selectedItemIds = _selectedItemIds.asStateFlow()

    val lastViewProductId: StateFlow<Long?> = recentlyViewedProductRepository.getLatestItem()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

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

    private val _recommendedProducts = MutableStateFlow(Products(emptyList()))
    val recommendedProducts = _recommendedProducts.asStateFlow()

    private val _allCartItems = MutableStateFlow<PurchaseProducts>(PurchaseProducts())
    val allCartItems = _allCartItems.asStateFlow()

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
                    _recommendedProducts.update { Products(products) }
                } catch (e: Exception) {
                    _recommendedProducts.update { Products(emptyList()) }
                }
            }
        }

        fetchCart()
    }

    fun fetchCart() {
        viewModelScope.launch {
            _allCartItems.update {
                cartRepository.getPagedCart(0, 1000000)
            }
        }
    }

    fun addToCart(purchaseProduct: PurchaseProduct) {
        viewModelScope.launch {
            val existingItem = allCartItems.value.purchaseProducts.find {
                it.product.id == purchaseProduct.product.id
            }
            if (existingItem != null) {
                cartRepository.updateCount(existingItem.id, existingItem.count + 1)
            }else {
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
            val target = allCartItems.value.findById(id)
            if (target != null) {
                val nextCount = target.count + updateAmount
                if(nextCount >= 1) {
                    cartRepository.updateCount(target.id, nextCount)
                    fetchCart()
                }
            }
        }
    }

    fun removeWithID(id: Long) {
        viewModelScope.launch {
            val target = allCartItems.value.findById(id)
            if(target != null){
                cartRepository.deleteCartItem(target.id)
                fetchCart()
            }
        }
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

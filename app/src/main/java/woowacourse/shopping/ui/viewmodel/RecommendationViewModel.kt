package woowacourse.shopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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
    initPrice: Int,
    initCheckedItemIds: List<Long>
) : ViewModel() {
    val lastViewProductId: StateFlow<Long?> =
        recentlyViewedProductRepository
            .getLatestItem()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null,
            )

    val lastViewedProduct: StateFlow<Product?> =
        flow {
            emit(productRepository.getProduct(lastViewProductId.value ?: 0))
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    private val _recommendedProducts = MutableStateFlow(Products(emptyList()))
    val recommendedProducts = _recommendedProducts.asStateFlow()

    private val _checkedItemsIds = MutableStateFlow(initCheckedItemIds)
    val checkedItemIds = _checkedItemsIds.asStateFlow()

    private val _totalPrice = MutableStateFlow(initPrice)
    val totalPrice = _totalPrice.asStateFlow()

    private val _allCartItems = MutableStateFlow<PurchaseProducts>(PurchaseProducts())
    val allCartItems = _allCartItems.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val latestViewedProductId = recentlyViewedProductRepository.getLatestItem().first()
                if (latestViewedProductId != null) {
                    val product = productRepository.getProduct(latestViewedProductId)
                    _recommendedProducts.update {
                        Products(productRepository.getCategoryProducts(category = product.category))
                    }
                }
            } catch (e: Exception) {
                // Handle or log the error here.
                // Currently just failing silently, which keeps the list empty.
                _recommendedProducts.update { Products(emptyList()) }
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
            val existingItem =
                allCartItems.value.purchaseProducts.find {
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
            val target = allCartItems.value.findById(id)
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
            val target = allCartItems.value.findById(id)
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
}

class RecommendationViewModelFactory(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecommendationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecommendationViewModel(
                cartRepository,
                productRepository,
                recentlyViewedProductRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

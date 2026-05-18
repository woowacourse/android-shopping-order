package woowacourse.shopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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

    private val _lastViewedProductCategory = MutableStateFlow<String>("")
    val lastViewedProductCategory = _lastViewedProductCategory.asStateFlow()
    private val _recommendedProducts = MutableStateFlow(Products(emptyList()))
    val recommendedProducts = _recommendedProducts.asStateFlow()

    private val _checkedItemsIds = MutableStateFlow(initCheckedItemIds)
    val checkedItemIds = _checkedItemsIds.asStateFlow()

    private val _totalPrice = MutableStateFlow(initPrice)
    val totalPrice = _totalPrice.asStateFlow()

    private val _allCartItems = MutableStateFlow<PurchaseProducts>(PurchaseProducts())
    val allCartItems = _allCartItems.asStateFlow()

    init {
        fetchCart()
        fetchRecommendations()
    }

    private fun fetchRecommendations() {
        viewModelScope.launch {
            try {
                val latestId = recentlyViewedProductRepository.getLatestItem().first()

                if (latestId != null) {
                    val product = productRepository.getProduct(latestId)
                    _lastViewedProductCategory.value = product.category

                    val categoryProducts = productRepository.getCategoryProducts(
                        category = lastViewedProductCategory.value
                    )

                    val cartProductIds = allCartItems.value.purchaseProducts.map { it.product.id }

                    val recommendations = categoryProducts.filter { it.id !in cartProductIds }

                    _recommendedProducts.value = Products(recommendations)
                }
            } catch (_: Exception) {
                _recommendedProducts.value = Products()
            }
        }
    }

    private fun fetchCart() {
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
    private val initPrice: Int,
    private val initCheckItemIds: List<Long>,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecommendationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecommendationViewModel(
                cartRepository,
                productRepository,
                recentlyViewedProductRepository,
                initPrice,
                initCheckItemIds
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

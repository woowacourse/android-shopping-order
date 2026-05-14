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
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.Products
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.domain.PurchaseProducts

class RecommendationViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
): ViewModel() {
    private lateinit var latestViewedProduct: Product

    private lateinit var _recommendedProducts: Products
    private val _cart = MutableStateFlow(PurchaseProducts())
    val cart = _cart.asStateFlow()

    init {
        viewModelScope.launch {
            val latestViewedProductId = recentlyViewedProductRepository.getLatestItem()
            latestViewedProduct = productRepository.getProduct(
                latestViewedProductId.first() ?: 0L
            )
            _recommendedProducts = Products(
                productRepository.getCategoryProducts(category = latestViewedProduct.category)
            )
        }
        fetchCart()
    }

    val recommendedProducts = _recommendedProducts

    fun fetchCart() {
        viewModelScope.launch {
            _cart.update {
                cartRepository.getPagedCart(0, 1000000)
            }
        }
    }

    fun addToCart(purchaseProduct: PurchaseProduct) {
        viewModelScope.launch {
            val existingItem = cart.value.purchaseProducts.find {
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
            val target = cart.value.findById(id)
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
            val target = cart.value.findById(id)
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
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecommendationViewModel(
                cartRepository,
                productRepository,
                recentlyViewedProductRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

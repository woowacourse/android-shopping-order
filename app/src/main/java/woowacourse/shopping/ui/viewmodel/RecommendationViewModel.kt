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
import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.data.remote.server.repository.CartRepository
import woowacourse.shopping.data.remote.server.repository.ProductRepository
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.Products
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.domain.PurchaseProducts
import woowacourse.shopping.ui.state.RecommendationUiSate

class RecommendationViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    initPrice: Int,
    initCheckedItemIds: List<Long>,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        RecommendationUiSate(
            totalPrice = initPrice,
            checkedIds = initCheckedItemIds
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            fetchCart()
            fetchRecommendations()
        }
    }

    private suspend fun fetchRecommendations() {
        val latestId = recentlyViewedProductRepository.getLatestItem().first()

        if (latestId != null) {
            val product = productRepository.getProduct(latestId)
            val lastViewedCategory = product.category

            val categoryProducts =
                productRepository.getCategoryProducts(
                    category = lastViewedCategory
                )

            val cartProductIds = _uiState.value.cart.purchaseProducts.map { it.product.id }

            val recommendations = categoryProducts.filter { it.id !in cartProductIds }

            _uiState.update { it.copy(recommendedProducts = Products(recommendations)) }
        }
    }

    private suspend fun fetchCart() {
        when(val allCartItemResult = cartRepository.getPagedCart(0, ViewModelConst.MAX_COUNT)) {
            is ApiResult.Success -> _uiState.update { it.copy(cart = allCartItemResult.data) }
            is ApiResult.Error -> ""
            is ApiResult.Exception -> ""
        }
    }

    fun addToCart(purchaseProduct: PurchaseProduct) {
        viewModelScope.launch {
            val existingItem =
                _uiState.value.cart.purchaseProducts.find {
                    it.product.id == purchaseProduct.product.id
                }
            if (existingItem != null) {
                cartRepository.updateCount(existingItem.id, existingItem.count + 1)
            } else {
                cartRepository.insert(purchaseProduct)
                val newCheckedIds = _uiState.value.checkedIds + purchaseProduct.id
                val newTotalPrice = _uiState.value.totalPrice + purchaseProduct.totalPrice()
                _uiState.update {
                    it.copy(
                        totalPrice = newTotalPrice,
                        checkedIds = newCheckedIds
                    )
                }
            }
            fetchCart()
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
                    cartRepository.updateCount(target.id, nextCount)
                    fetchCart()
                }
                if (updateAmount > 0) {
                    _uiState.update { it.copy(totalPrice = it.totalPrice + target.price()) }
                } else {
                    _uiState.update { it.copy(totalPrice = it.totalPrice - target.price()) }
                }
            }
        }
    }

    fun removeWithID(id: Long) {
        viewModelScope.launch {
            val target = _uiState.value.cart.findById(id)
            if (target != null) {
                cartRepository.deleteCartItem(target.id)
                _uiState.update { it.copy(totalPrice = it.totalPrice - target.totalPrice()) }
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
                initCheckItemIds,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

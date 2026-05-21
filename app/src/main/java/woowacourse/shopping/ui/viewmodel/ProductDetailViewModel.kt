package woowacourse.shopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.repository.RecentlyViewedProductRepository
import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.data.remote.server.repository.CartRepository
import woowacourse.shopping.data.remote.server.repository.ProductRepository
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.domain.PurchaseProducts
import woowacourse.shopping.ui.state.ProductDetailUIState

class ProductDetailViewModel(
    private val cartRepository: CartRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val productRepository: ProductRepository,
    private val selectedProductId: Long,
    private val lastViewedProductId: Long?,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUIState())
    val uiState = _uiState.asStateFlow()
    private val _cart = MutableStateFlow(PurchaseProducts())


    init {
        viewModelScope.launch {
            val allCartItemResult = cartRepository.getPagedCart(0, ViewModelConst.MAX_COUNT)
            when(allCartItemResult) {
                is ApiResult.Success -> _cart.update { allCartItemResult.data }
                is ApiResult.Error -> ""
                is ApiResult.Exception -> ""
            }
            fetchProduct()
        }
    }

    private suspend fun fetchProduct() {
        val selectedProduct = productRepository.getProduct(selectedProductId)
        if (lastViewedProductId != null){
            val lastViewedProduct = productRepository.getProduct(lastViewedProductId)

            _uiState.update { it.copy(product = selectedProduct, lastViewProduct = lastViewedProduct) }
        } else {
            _uiState.update { it.copy(product = selectedProduct) }
        }
    }

    fun addCount() {
        val currentCount = _uiState.value.count
        _uiState.update { it.copy(count = currentCount + 1) }
    }

    fun minusCount() {
        val currentCount = _uiState.value.count
        if (currentCount > 1) {
            _uiState.update { it.copy(count = currentCount - 1) }
        }
    }

    fun addPurchaseProduct(purchaseProduct: PurchaseProduct) {
        viewModelScope.launch {
            val existCartItem = _cart.value.findById(purchaseProduct.productId())
            if (existCartItem != null) {
                val newTotalCount = existCartItem.count + purchaseProduct.count
                cartRepository.updateCount(existCartItem.id, newTotalCount)
            } else {
                cartRepository.insert(purchaseProduct)
            }
        }
    }

    fun updateHistory(product: Product) {
        viewModelScope.launch {
            recentlyViewedProductRepository.updateList(product)
        }
    }
}

class ProductDetailViewModelFactory(
    private val cartRepository: CartRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val productRepository: ProductRepository,
    private val selectedProductId: Long,
    private val lastViewedProductId: Long?,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductDetailViewModel(
                cartRepository,
                recentlyViewedProductRepository,
                productRepository,
                selectedProductId,
                lastViewedProductId,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

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
            val allCartItemResult = cartRepository.getPagedCart(0, ViewModelConst.CART_MAX_COUNT)
            when (allCartItemResult) {
                is ApiResult.Success -> _cart.update { allCartItemResult.data }
                is ApiResult.Error -> _uiState.update { it.copy(errorMsg = "${ViewModelConst.NETWORK_ERROR_LABEL}${allCartItemResult.code}") }
                is ApiResult.Exception -> _uiState.update { it.copy(errorMsg = "${ViewModelConst.ERROR_LABEL}${allCartItemResult.e.message}") }
            }
            fetchProduct()
        }
    }

    private suspend fun fetchProduct() {
        when (val selectedProductResult = productRepository.getProduct(selectedProductId)) {
            is ApiResult.Success -> {
                val selectedProduct = selectedProductResult.data
                if (lastViewedProductId != null) {
                    when (val lastViewedProductResult =
                        productRepository.getProduct(lastViewedProductId)) {
                        is ApiResult.Success -> {
                            _uiState.update {
                                it.copy(
                                    product = selectedProduct,
                                    lastViewProduct = lastViewedProductResult.data,
                                )
                            }
                        }

                        is ApiResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    product = selectedProduct,
                                    errorMsg = "${ViewModelConst.NETWORK_ERROR_LABEL}${lastViewedProductResult.code}",
                                )
                            }
                        }

                        is ApiResult.Exception -> {
                            _uiState.update {
                                it.copy(
                                    product = selectedProduct,
                                    errorMsg = "${ViewModelConst.ERROR_LABEL}${lastViewedProductResult.e.message}",
                                )
                            }
                        }
                    }
                } else {
                    _uiState.update { it.copy(product = selectedProduct) }
                }
            }

            is ApiResult.Error -> {
                _uiState.update { it.copy(errorMsg = "${ViewModelConst.NETWORK_ERROR_LABEL}${selectedProductResult.code}") }
            }

            is ApiResult.Exception -> {
                _uiState.update { it.copy(errorMsg = "${ViewModelConst.ERROR_LABEL}${selectedProductResult.e.message}") }
            }
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

    fun updateHistory(product: Product) {
        viewModelScope.launch {
            recentlyViewedProductRepository.updateList(product)
        }
    }

    fun onErrorMsgShown() {
        _uiState.update { it.copy(errorMsg = null) }
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
            @Suppress("UNCHECKED_CAST") return ProductDetailViewModel(
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

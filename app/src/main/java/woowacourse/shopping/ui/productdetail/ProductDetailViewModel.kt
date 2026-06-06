package woowacourse.shopping.ui.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.repository.RecentlyViewedProductRepository
import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.data.remote.server.apiresult.onFailure
import woowacourse.shopping.data.remote.server.apiresult.onSuccess
import woowacourse.shopping.data.remote.server.repository.CartRepository
import woowacourse.shopping.data.remote.server.repository.ProductRepository
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.domain.PurchaseProducts
import woowacourse.shopping.ui.ViewModelConst

class ProductDetailViewModel(
    private val cartRepository: CartRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val productRepository: ProductRepository,
    private val selectedProductId: Long,
    private val lastViewedProductId: Long?,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductDetailUIState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<ProductDetailEvent>()
    val event = _event.asSharedFlow()
    private val _cart = MutableStateFlow(PurchaseProducts())

    init {
        viewModelScope.launch {
            cartRepository.getPagedCart(0, ViewModelConst.CART_MAX_COUNT)
                .handleWithSnackBar { data -> _cart.update { data } }
            fetchProduct()
        }
    }

    private suspend fun fetchProduct() {
        productRepository.getProduct(selectedProductId).handleWithSnackBar { selectedProduct ->
            if (lastViewedProductId != null) {
                productRepository.getProduct(lastViewedProductId).handleWithSnackBar { lastViewedProduct ->
                    _uiState.update {
                        it.copy(
                            product = selectedProduct,
                            lastViewProduct = lastViewedProduct,
                        )
                    }
                }.onFailure { _, _ ->
                    _uiState.update { it.copy(product = selectedProduct) }
                }
            } else {
                _uiState.update { it.copy(product = selectedProduct) }
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

    fun moveToLastViewedProduct(productId: Long) {
        viewModelScope.launch {
            _event.emit(
                ProductDetailEvent.MoveToLastViewedProductDetail(productId),
            )
        }
    }

    fun moveToShopping() {
        viewModelScope.launch {
            _event.emit(
                ProductDetailEvent.MoveToShopping,
            )
        }
    }

    private suspend fun <T> ApiResult<T>.handleWithSnackBar(
        errorMessage: ProductDetailEvent.Message? = null,
        onSuccessAction: suspend (T) -> Unit,
    ): ApiResult<T> {
        return this.onSuccess { onSuccessAction(it) }
            .onFailure { code, msg ->
                val message =
                    errorMessage ?: if (code != null) {
                        ProductDetailEvent.Message.NetworkError(code)
                    } else {
                        ProductDetailEvent.Message.ExceptionError(msg)
                    }
                _event.emit(ProductDetailEvent.SnackbarEvent(message))
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

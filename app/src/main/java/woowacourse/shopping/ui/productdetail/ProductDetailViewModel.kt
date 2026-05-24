package woowacourse.shopping.ui.productdetail

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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.order.PurchaseProduct
import woowacourse.shopping.domain.model.order.PurchaseProducts
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentlyViewedProductRepository
import woowacourse.shopping.ui.event.UiEvent

class ProductDetailViewModel(
    private val cartRepository: CartRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val productRepository: ProductRepository,
    private val selectedProductId: Long,
    private val lastViewedProductId: Long?,
) : ViewModel() {
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    private val _count = MutableStateFlow(1)
    val countState = _count.asStateFlow()

    val selectedProduct: StateFlow<Product?> = flow {
        emit(productRepository.getProduct(selectedProductId))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _cart = MutableStateFlow(PurchaseProducts())
    val cart = _cart.asStateFlow()

    val lastViewedProduct: StateFlow<Product?> = flow {
        lastViewedProductId?.let {
            emit(productRepository.getProduct(lastViewedProductId))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    init {
        viewModelScope.launch {
            cartRepository.findCartItemByProductId(selectedProductId)?.let { cartItem ->
                _cart.update {
                    PurchaseProducts(listOf(cartItem))
                }
            }
        }
    }

    fun addCount() {
        _count.update { it + 1 }
    }

    fun minusCount() {
        if (countState.value > 1) {
            _count.update { it - 1 }
        }
    }

    fun addPurchaseProduct(
        purchaseProduct: PurchaseProduct,
        onSuccess: () -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                val existCartItem = cart.value.findById(purchaseProduct.productId)
                if (existCartItem != null) {
                    val newTotalCount = existCartItem.count + purchaseProduct.count
                    cartRepository.updateCount(existCartItem.id, newTotalCount)
                    _cart.update {
                        PurchaseProducts(listOf(existCartItem.copy(count = newTotalCount)))
                    }
                } else {
                    cartRepository.insert(purchaseProduct)
                }
                _uiEvent.emit(UiEvent.ShowMessage("장바구니에 담았습니다."))
                onSuccess()
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("장바구니 담기에 실패했습니다."))
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
                lastViewedProductId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package woowacourse.shopping.ui.productdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.toRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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
import woowacourse.shopping.ui.navigation.ShoppingRoute

class ProductDetailViewModel internal constructor(
    private val cartRepository: CartRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val productRepository: ProductRepository,
    private val selectedProductId: Long,
    private val lastViewedProductId: Long?,
) : ViewModel() {
    constructor(
        cartRepository: CartRepository,
        recentlyViewedProductRepository: RecentlyViewedProductRepository,
        productRepository: ProductRepository,
        savedStateHandle: SavedStateHandle,
    ) : this(
        cartRepository = cartRepository,
        recentlyViewedProductRepository = recentlyViewedProductRepository,
        productRepository = productRepository,
        selectedProductId =
            savedStateHandle.toRoute<ShoppingRoute.ProductDetail>().selectedProductId,
        lastViewedProductId =
            savedStateHandle.toRoute<ShoppingRoute.ProductDetail>().lastViewedProductId,
    )

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<UiEvent> = _uiEvent.receiveAsFlow()

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
                val existCartItem = cart.value.findByProductId(purchaseProduct.productId)
                if (existCartItem != null) {
                    val newTotalCount = existCartItem.count + purchaseProduct.count
                    cartRepository.updateCount(existCartItem.id, newTotalCount)
                    _cart.update {
                        PurchaseProducts(listOf(existCartItem.copy(count = newTotalCount)))
                    }
                } else {
                    cartRepository.insert(purchaseProduct)
                    cartRepository.findCartItemByProductId(purchaseProduct.productId)?.let { cartItem ->
                        _cart.update {
                            PurchaseProducts(listOf(cartItem))
                        }
                    }
                }
                _uiEvent.send(UiEvent.ShowMessage("장바구니에 담았습니다."))
                onSuccess()
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.ShowMessage("장바구니 담기에 실패했습니다."))
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
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductDetailViewModel(
                cartRepository = cartRepository,
                recentlyViewedProductRepository = recentlyViewedProductRepository,
                productRepository = productRepository,
                savedStateHandle = extras.createSavedStateHandle(),
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

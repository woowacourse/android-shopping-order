package woowacourse.shopping.ui.recommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.repository.OutstandingProductRepository
import woowacourse.shopping.data.local.repository.RecentlyViewedProductRepository
import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.data.remote.server.apiresult.onFailure
import woowacourse.shopping.data.remote.server.apiresult.onSuccess
import woowacourse.shopping.data.remote.server.repository.CartRepository
import woowacourse.shopping.data.remote.server.repository.ProductRepository
import woowacourse.shopping.domain.Products
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.ui.ViewModelConst

class RecommendationViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val outstandingProductRepository: OutstandingProductRepository,
    initPrice: Int,
    initCheckedItemIds: List<Long>,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            RecommendationUiSate(
                totalPrice = initPrice,
                checkedIds = initCheckedItemIds,
            ),
        )
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<RecommendationEvent>()
    val event = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            fetchCart()
            fetchRecommendations()
        }
    }

    private suspend fun fetchRecommendations() {
        val latestId = recentlyViewedProductRepository.getLatestItem().first()

        if (latestId != null) {
            productRepository.getProduct(latestId).handleWithSnackBar { product ->
                val lastViewedCategory = product.category

                productRepository.getCategoryProducts(category = lastViewedCategory)
                    .handleWithSnackBar { categoryProducts ->
                        val cartProductIds =
                            _uiState.value.cart.purchaseProducts
                                .map { it.product.id }
                        val recommendations = categoryProducts.filter { it.id !in cartProductIds }

                        _uiState.update { it.copy(recommendedProducts = Products(recommendations)) }
                    }
            }
        }
    }

    suspend fun fetchCart() {
        cartRepository.getPagedCart(0, ViewModelConst.CART_MAX_COUNT)
            .handleWithSnackBar { data ->
                _uiState.update { it.copy(cart = data) }
            }
    }

    fun fetchTotalPrice() {
        _uiState.update { state ->
            val totalPrice =
                state.cart.purchaseProducts
                    .filter { it.id in state.checkedIds }
                    .sumOf { it.totalPrice() }
            state.copy(totalPrice = totalPrice)
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
                    .handleWithSnackBar {
                        fetchCart()
                        fetchTotalPrice()
                        _event.emit(
                            RecommendationEvent.SnackbarEvent(
                                RecommendationEvent.Message.QuantityUpdated
                            )
                        )
                    }
            } else {
                cartRepository.insert(purchaseProduct).handleWithSnackBar {
                    fetchCart()
                    val addedItem = _uiState.value.cart.findById(purchaseProduct.product.id)
                    if (addedItem != null) {
                        _uiState.update { it.copy(checkedIds = it.checkedIds + addedItem.id) }
                        fetchTotalPrice()
                        _event.emit(
                            RecommendationEvent.SnackbarEvent(
                                RecommendationEvent.Message.CartAdded
                            )
                        )
                    }
                }
            }
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
                        .handleWithSnackBar {
                            fetchCart()
                            fetchTotalPrice()
                            _event.emit(
                                RecommendationEvent.SnackbarEvent(
                                    RecommendationEvent.Message.QuantityUpdated
                                )
                            )
                        }
                }
            }
        }
    }

    fun removeWithID(id: Long) {
        viewModelScope.launch {
            val target = _uiState.value.cart.findById(id)
            if (target != null) {
                cartRepository.deleteCartItem(target.id)
                    .handleWithSnackBar {
                        _uiState.update { it.copy(checkedIds = it.checkedIds - target.id) }
                        fetchCart()
                        fetchTotalPrice()
                        _event.emit(
                            RecommendationEvent.SnackbarEvent(
                                RecommendationEvent.Message.CartRemoved
                            )
                        )
                    }
            }
        }
    }

    private suspend fun <T> ApiResult<T>.handleWithSnackBar(
        errorMessage: RecommendationEvent.Message? = null,
        onSuccessAction: suspend (T) -> Unit,
    ): ApiResult<T> {
        return this.onSuccess { onSuccessAction(it) }
            .onFailure { code, msg ->
                val message =
                    errorMessage ?: if (code != null) {
                        RecommendationEvent.Message.NetworkError(code)
                    } else {
                        RecommendationEvent.Message.ExceptionError(msg)
                    }
                _event.emit(RecommendationEvent.SnackbarEvent(message))
            }
    }

    fun addToCartTrigger(purchaseProduct: PurchaseProduct) {
        viewModelScope.launch {
            _event.emit(
                RecommendationEvent.AddToCart(
                    purchaseProduct,
                ),
            )
        }
    }

    fun updateAmountTrigger(
        targetId: Long,
        updateAmount: Int,
    ) {
        viewModelScope.launch {
            _event.emit(
                RecommendationEvent.UpdateAmount(
                    targetID = targetId,
                    updateAmount = updateAmount,
                ),
            )
        }
    }

    fun removeFromCartTrigger(targetId: Long) {
        viewModelScope.launch {
            _event.emit(
                RecommendationEvent.RemoveFromCart(targetId),
            )
        }
    }

    fun navigateToPayment(checkedIds: List<Long>) {
        viewModelScope.launch {
            outstandingProductRepository.replaceAll(checkedIds)
            _event.emit(
                RecommendationEvent.NavigateToPayment(checkedIds),
            )
        }
    }

    fun navigateToCart() {
        viewModelScope.launch {
            _event.emit(
                RecommendationEvent.NavigateToCart,
            )
        }
    }
}

class RecommendationViewModelFactory(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val outstandingProductRepository: OutstandingProductRepository,
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
                outstandingProductRepository,
                initPrice,
                initCheckItemIds,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

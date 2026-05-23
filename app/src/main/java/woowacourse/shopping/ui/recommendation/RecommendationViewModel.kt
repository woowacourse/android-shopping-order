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
import woowacourse.shopping.data.local.repository.RecentlyViewedProductRepository
import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.data.remote.server.repository.CartRepository
import woowacourse.shopping.data.remote.server.repository.ProductRepository
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.Products
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.ui.ViewModelConst

class RecommendationViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
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
            when (val productResult = productRepository.getProduct(latestId)) {
                is ApiResult.Success -> {
                    val product = productResult.data
                    val lastViewedCategory = product.category

                    when (val categoryProductsResult = productRepository.getCategoryProducts(category = lastViewedCategory)) {
                        is ApiResult.Success -> {
                            val categoryProducts = categoryProductsResult.data
                            val cartProductIds =
                                _uiState.value.cart.purchaseProducts
                                    .map { it.product.id }
                            val recommendations = categoryProducts.filter { it.id !in cartProductIds }

                            _uiState.update { it.copy(recommendedProducts = Products(recommendations)) }
                        }

                        is ApiResult.Error ->
                            _event.emit(
                                RecommendationEvent.SnackbarEvent(
                                    "${ViewModelConst.NETWORK_ERROR_LABEL}${categoryProductsResult.message}"
                                )
                            )

                        is ApiResult.Exception ->
                            _event.emit(
                                RecommendationEvent.SnackbarEvent(
                                    "${ViewModelConst.ERROR_LABEL}${categoryProductsResult.e.message}"
                                )
                            )
                    }
                }

                is ApiResult.Error ->
                    _event.emit(
                        RecommendationEvent.SnackbarEvent(
                            "${ViewModelConst.NETWORK_ERROR_LABEL}${productResult.code}"
                        )
                    )

                is ApiResult.Exception ->
                    _event.emit(
                        RecommendationEvent.SnackbarEvent(
                            "${ViewModelConst.ERROR_LABEL}${productResult.e.message}"
                        )
                    )
            }
        }
    }

    suspend fun fetchCart() {
        when (val allCartItemResult =
            cartRepository.getPagedCart(0, ViewModelConst.CART_MAX_COUNT)) {
            is ApiResult.Success -> _uiState.update { it.copy(cart = allCartItemResult.data) }

            is ApiResult.Error ->
                _event.emit(
                    RecommendationEvent.SnackbarEvent(
                        "${ViewModelConst.NETWORK_ERROR_LABEL}${allCartItemResult.code}"
                    )
                )

            is ApiResult.Exception ->
                _event.emit(
                    RecommendationEvent.SnackbarEvent(
                        "${ViewModelConst.ERROR_LABEL}${allCartItemResult.e.message}"
                    )
                )
        }
    }

    fun addToCart(purchaseProduct: PurchaseProduct) {
        viewModelScope.launch {
            val existingItem =
                _uiState.value.cart.purchaseProducts.find {
                    it.product.id == purchaseProduct.product.id
                }
            if (existingItem != null) {
                when (val result = cartRepository.updateCount(existingItem.id, existingItem.count + 1)) {
                    is ApiResult.Success -> fetchCart()
                    is ApiResult.Error ->
                        _event.emit(
                            RecommendationEvent.SnackbarEvent(
                                "${ViewModelConst.NETWORK_ERROR_LABEL}${result.code}"
                            )
                        )

                    is ApiResult.Exception ->
                        _event.emit(
                            RecommendationEvent.SnackbarEvent(
                                "${ViewModelConst.ERROR_LABEL}${result.e.message}"
                            )
                        )
                }
            } else {
                when (val result = cartRepository.insert(purchaseProduct)) {
                    is ApiResult.Success -> {
                        val newCheckedIds = _uiState.value.checkedIds + purchaseProduct.id
                        val newTotalPrice = _uiState.value.totalPrice + purchaseProduct.totalPrice()
                        _uiState.update {
                            it.copy(
                                totalPrice = newTotalPrice,
                                checkedIds = newCheckedIds,
                            )
                        }
                        fetchCart()
                    }

                    is ApiResult.Error ->
                        _event.emit(
                            RecommendationEvent.SnackbarEvent(
                                "${ViewModelConst.NETWORK_ERROR_LABEL}${result.code}"
                            )
                        )

                    is ApiResult.Exception ->
                        _event.emit(
                            RecommendationEvent.SnackbarEvent(
                                "${ViewModelConst.ERROR_LABEL}${result.e.message}"
                            )
                        )
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
                    when (val result = cartRepository.updateCount(target.id, nextCount)) {
                        is ApiResult.Success -> {
                            fetchCart()
                            if (updateAmount > 0) {
                                _uiState.update { it.copy(totalPrice = it.totalPrice + target.price()) }
                            } else {
                                _uiState.update { it.copy(totalPrice = it.totalPrice - target.price()) }
                            }
                        }

                        is ApiResult.Error ->
                            _event.emit(
                                RecommendationEvent.SnackbarEvent(
                                    "${ViewModelConst.NETWORK_ERROR_LABEL}${result.code}"
                                )
                            )

                        is ApiResult.Exception ->
                            _event.emit(
                                RecommendationEvent.SnackbarEvent(
                                    "${ViewModelConst.ERROR_LABEL}${result.e.message}"
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
                when (val result = cartRepository.deleteCartItem(target.id)) {
                    is ApiResult.Success -> {
                        _uiState.update { it.copy(totalPrice = it.totalPrice - target.totalPrice()) }
                        fetchCart()
                    }

                    is ApiResult.Error ->
                        _event.emit(
                            RecommendationEvent.SnackbarEvent(
                                "${ViewModelConst.NETWORK_ERROR_LABEL}${result.code}"
                            )
                        )

                    is ApiResult.Exception ->
                        _event.emit(
                            RecommendationEvent.SnackbarEvent(
                                "${ViewModelConst.ERROR_LABEL}${result.e.message}"
                            )
                        )
                }
            }
        }
    }

    fun updateHistory(product: Product) {
        viewModelScope.launch {
            recentlyViewedProductRepository.updateList(product)
        }
    }

    fun addToCartTrigger(purchaseProduct: PurchaseProduct) {
        viewModelScope.launch {
            _event.emit(
                RecommendationEvent.AddToCart(
                    purchaseProduct
                )
            )
        }
    }

    fun updateAmountTrigger(targetId: Long, updateAmount: Int) {
        viewModelScope.launch {
            _event.emit(
                RecommendationEvent.UpdateAmount(
                    targetID = targetId,
                    updateAmount = updateAmount
                )
            )
        }
    }

    fun removeFromCartTrigger(targetId: Long) {
        viewModelScope.launch {
            _event.emit(
                RecommendationEvent.RemoveFromCart(targetId)
            )
        }
    }

    fun navigateToPayment(checkedIds: List<Long>) {
        viewModelScope.launch {
            _event.emit(
                RecommendationEvent.NavigateToPayment(checkedIds)
            )
        }
    }

    fun navigateToProductDetail(selectedProductId: Long) {
        viewModelScope.launch {
            _event.emit(
                RecommendationEvent.NavigateToProductDetail(
                    selectedProductId
                )
            )
        }
    }

    fun navigateToCart() {
        viewModelScope.launch {
            _event.emit(
                RecommendationEvent.NavigateToCart
            )
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

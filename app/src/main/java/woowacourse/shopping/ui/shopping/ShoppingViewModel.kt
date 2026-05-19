package woowacourse.shopping.ui.shopping

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.NetworkMonitor
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentProductRepository
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.Products
import woowacourse.shopping.ui.common.error.ErrorMessageMapper
import woowacourse.shopping.ui.common.model.ProductUiModel

class ShoppingViewModel(
    networkMonitor: NetworkMonitor,
    private val productRepo: ProductRepository,
    private val cartRepo: CartRepository,
    private val recentProductRepo: RecentProductRepository,
    private val loadSize: Int,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<String>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    val isNetworkConnected: StateFlow<Boolean> =
        networkMonitor.isConnected
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = true,
            )

    init {
        initialize()
        observeRecentProducts()
    }

    fun increase(uiModel: ProductUiModel) {
        viewModelScope.launch {
            try {
                val cartItemId =
                    if (uiModel.cartItemId == null) {
                        cartRepo.add(productId = uiModel.product.id, quantity = 1)
                    } else {
                        cartRepo.updateQuantity(
                            cartItemId = uiModel.cartItemId,
                            quantity = uiModel.quantity + 1,
                        )
                        uiModel.cartItemId
                    }
                _uiState.update { state ->
                    val updatedProducts =
                        state.visibleProducts.map {
                            if (uiModel.product.id == it.product.id) {
                                it.copy(cartItemId = cartItemId, quantity = uiModel.quantity + 1)
                            } else {
                                it
                            }
                        }
                    state.copy(
                        visibleProducts = updatedProducts,
                        cartCount = state.cartCount + 1,
                    )
                }
            } catch (e: Exception) {
                handleError("increase", e, "장바구니 담기에 실패했어요.")
            }
        }
    }

    fun decrease(uiModel: ProductUiModel) {
        viewModelScope.launch {
            try {
                val cartItemId =
                    uiModel.cartItemId
                        ?: throw IllegalArgumentException("상품(${uiModel.product.name})의 카트 아이템 아이디가 null 입니다.")
                if (uiModel.quantity > 1) {
                    cartRepo.updateQuantity(
                        cartItemId = cartItemId,
                        quantity = uiModel.quantity - 1,
                    )
                } else {
                    cartRepo.delete(cartItemId)
                }

                _uiState.update { state ->
                    val updatedProducts =
                        state.visibleProducts.map {
                            if (uiModel.product.id == it.product.id) {
                                if (uiModel.quantity > 1) {
                                    it.copy(quantity = uiModel.quantity - 1)
                                } else {
                                    it.copy(cartItemId = null, quantity = 0)
                                }
                            } else {
                                it
                            }
                        }
                    state.copy(
                        visibleProducts = updatedProducts,
                        cartCount = maxOf(0, state.cartCount - 1),
                    )
                }
            } catch (e: Exception) {
                handleError("decrease", e, "수량 변경에 실패했어요.")
            }
        }
    }

    fun loadMore() {
        val currentSize = _uiState.value.visibleProducts.size
        val currentProducts = _uiState.value.visibleProducts
        if (currentSize >= _uiState.value.sizeInRepo) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val page =
                    productRepo.getProducts(
                        page = (currentSize - 1) / loadSize + 1,
                        size = loadSize,
                    )
                val newUiModels = mapToProductUiModels(page.items)
                val combineProducts = currentProducts + newUiModels

                _uiState.update {
                    it.copy(
                        visibleCount = minOf(it.visibleCount + loadSize, page.totalElements),
                        visibleProducts = combineProducts,
                        hasNext = !page.isLast,
                        sizeInRepo = page.totalElements,
                    )
                }
            } catch (e: Exception) {
                handleError("loadMore", e, "상품을 더 불러올 수 없어요.")
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                val cartItems = cartRepo.getAllCartItems()
                val totalCartCount = cartItems.items.sumOf { it.quantity }

                _uiState.update { state ->
                    val updatedUiModels =
                        state.visibleProducts.map { ui ->
                            val cartItem = cartItems.items.find { it.product.id == ui.product.id }
                            ui.copy(
                                cartItemId = cartItem?.id,
                                quantity = cartItem?.quantity ?: 0,
                            )
                        }
                    state.copy(
                        visibleProducts = updatedUiModels,
                        cartCount = totalCartCount,
                    )
                }
            } catch (e: Exception) {
                handleError("refresh", e, "장바구니 상태를 동기화할 수 없어요.")
            }
        }
    }

    private fun initialize() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val page = productRepo.getProducts(0, loadSize)
                _uiState.update {
                    it.copy(
                        visibleProducts = mapToProductUiModels(page.items),
                        hasNext = !page.isLast,
                        sizeInRepo = page.totalElements,
                    )
                }
            } catch (e: Exception) {
                handleError("initialize", e, "상품을 불러올 수 없어요.")
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun observeRecentProducts() {
        recentProductRepo
            .observeRecent()
            .onEach { products ->
                _uiState.update { it.copy(recentProducts = Products(products)) }
            }.catch { e ->
                Log.e(TAG, "observeRecentProducts: 에러", e)
            }.launchIn(viewModelScope)
    }

    private suspend fun mapToProductUiModels(products: List<Product>): List<ProductUiModel> {
        val cart = cartRepo.getAllCartItems()

        return products.map { product ->
            val cartItem = cart.items.find { it.product.id == product.id }
            ProductUiModel(
                product = product,
                cartItemId = cartItem?.id,
                quantity = cartItem?.quantity ?: 0,
            )
        }
    }

    private suspend fun handleError(
        tag: String,
        e: Exception,
        defaultMessage: String,
    ) {
        if (e is CancellationException) throw e
        Log.e(TAG, "$tag 에러", e)
        _events.send(ErrorMessageMapper.toUserMessage(e, defaultMessage))
    }

    companion object {
        private const val TAG = "ShoppingViewModel"

        fun provideFactory(
            applicationContext: Context,
            productRepo: ProductRepository,
            cartRepo: CartRepository,
            recentProductRepo: RecentProductRepository,
            loadSize: Int,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ShoppingViewModel(
                        networkMonitor = NetworkMonitor(applicationContext),
                        productRepo = productRepo,
                        cartRepo = cartRepo,
                        recentProductRepo = recentProductRepo,
                        loadSize = loadSize,
                    ) as T
            }
    }
}

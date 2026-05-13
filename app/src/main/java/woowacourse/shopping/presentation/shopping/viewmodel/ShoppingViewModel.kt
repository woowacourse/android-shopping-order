package woowacourse.shopping.presentation.shopping.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException
import woowacourse.shopping.di.RepositoryProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.presentation.common.addToCartUseCase
import woowacourse.shopping.presentation.common.model.ProductUiModel
import woowacourse.shopping.presentation.common.model.toUiModel
import woowacourse.shopping.presentation.shopping.model.ShoppingItemUiModel
import woowacourse.shopping.presentation.shopping.model.ShoppingUiState

class ShoppingViewModel(
    private val productRepository: ProductRepository = RepositoryProvider.productRepository,
    private val cartRepository: CartRepository = RepositoryProvider.cartRepository,
    private val recentProductRepository: RecentProductRepository = RepositoryProvider.recentProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingUiState())
    val uiState: StateFlow<ShoppingUiState> = _uiState.asStateFlow()

    private val pageSize = 20

    fun initialize() {
        viewModelScope.launch {
            if (uiState.value.offset == 0) loadMore()
            loadRecentProducts(10)
        }
    }

    fun loadCartItemQuantities() {
        viewModelScope.launch {
            val products = productRepository.getProducts(0, 100000)
            val cart = cartRepository.getCart()
            _uiState.update {
                it.copy(
                    products =
                        products.map { product ->
                            ShoppingItemUiModel(
                                product.toUiModel(),
                                cart[product.id]?.quantity ?: 0,
                            )
                        },
                    totalQuantity = cart.totalQuantity,
                )
            }
        }
    }

    fun loadMore() {
        viewModelScope.launch { loadNext() }
    }

    fun increase(id: Long) {
        viewModelScope.launch {
            addToCartUseCase(cartRepository, id)
            loadCartItemQuantities()
        }
    }

    fun decrease(id: Long) {
        viewModelScope.launch {
            val cartItem =
                cartRepository.getCart().items.find { it.product.id == id } ?: return@launch
            cartRepository.changeCartItem(id, cartItem.decrease().quantity)

            loadCartItemQuantities()
        }
    }

    fun upsertRecentProduct(id: Long) {
        viewModelScope.launch {
            recentProductRepository.upsertRecentProduct(id)
        }
    }

    fun loadRecentProducts(limit: Int) {
        viewModelScope.launch {
            refreshRecentProducts(limit)
        }
    }

    private suspend fun refreshRecentProducts(limit: Int) {
        _uiState.update {
            it.copy(
                recentProducts =
                    recentProductRepository.getRecentProducts(limit).map { product ->
                        product.toUiModel()
                    },
            )
        }
    }

    private suspend fun getProductData(
        offset: Int,
        limit: Int,
    ): ImmutableList<ProductUiModel> =
        productRepository
            .getProducts(offset, limit)
            .map { it.toUiModel() }
            .toImmutableList()

    private suspend fun loadNext() {
        if (uiState.value.isLoading || !uiState.value.canLoadMore) return
        _uiState.update {
            it.copy(isLoading = true)
        }

        try {
            val loadData =
                getProductData(
                    offset = uiState.value.offset,
                    limit = pageSize,
                )
            val newItems = loadData.map { ShoppingItemUiModel(it, quantity = 0) }
            _uiState.update {
                it.copy(
                    products = it.products.plus(newItems),
                    offset = it.offset + loadData.size,
                    canLoadMore = loadData.size == pageSize,
                )
            }
            loadCartItemQuantities()
        } catch (e: IOException) {
            _uiState.update { it.copy(errorMessage = "네트워크 연결을 확인해주세요.") }
        } finally {
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}

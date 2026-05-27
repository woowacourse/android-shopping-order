package woowacourse.shopping.presentation.shopping.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException
import woowacourse.shopping.di.AppModule
import woowacourse.shopping.domain.model.ProductsPage
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.usecase.AddToCartUseCase
import woowacourse.shopping.presentation.common.model.toUiModel
import woowacourse.shopping.presentation.shopping.model.ShoppingItemUiModel
import woowacourse.shopping.presentation.shopping.model.ShoppingUiState

class ShoppingViewModel(
    private val addToCartUseCase: AddToCartUseCase = AppModule.addToCartUseCase,
    private val productRepository: ProductRepository = AppModule.productRepository,
    private val cartRepository: CartRepository = AppModule.cartRepository,
    private val recentProductRepository: RecentProductRepository = AppModule.recentProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingUiState())
    val uiState: StateFlow<ShoppingUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<ShoppingEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private val exceptionHandler =
        CoroutineExceptionHandler { _, err ->
            println("DSDS:$err")
            viewModelScope.launch {
                _uiEvents.emit(ShoppingEvent.ShowError("알 수 없는 오류가 발생했습니다."))
            }
        }

    private val pageSize = 20

    fun initialize() {
        viewModelScope.launch(exceptionHandler) {
            if (uiState.value.page == 0) loadMore()
            loadRecentProducts(10)
        }
    }

    fun loadCartItemQuantities() {
        viewModelScope.launch(exceptionHandler) {
            val cart = cartRepository.getCart()
            _uiState.update {
                it.copy(
                    products =
                        it.products.map { item ->
                            item.copy(quantity = cart[item.product.id]?.quantity ?: 0)
                        },
                    totalQuantity = cart.totalQuantity,
                )
            }
        }
    }

    fun loadMore() {
        viewModelScope.launch(exceptionHandler) { loadNext() }
    }

    fun increase(id: Long) {
        viewModelScope.launch(exceptionHandler) {
            addToCartUseCase.invoke(id)
            loadCartItemQuantities()
        }
    }

    fun decrease(id: Long) {
        viewModelScope.launch(exceptionHandler) {
            val cartItem =
                cartRepository.getCart().items.find { it.product.id == id } ?: return@launch
            cartRepository.changeCartItem(id, cartItem.decrease().quantity)

            loadCartItemQuantities()
        }
    }

    fun upsertRecentProduct(id: Long) {
        viewModelScope.launch(exceptionHandler) {
            recentProductRepository.upsertRecentProduct(id)
        }
    }

    fun loadRecentProducts(limit: Int) {
        viewModelScope.launch(exceptionHandler) {
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

    private suspend fun getProductsPage(
        page: Int,
        limit: Int,
    ): ProductsPage =
        productRepository
            .getProducts(page, limit)

    private suspend fun loadNext() {
        if (uiState.value.isLoading) return
        if (uiState.value.page > 0 && !uiState.value.canLoadMore) return
        _uiState.update {
            it.copy(isLoading = true)
        }

        try {
            val loadData =
                getProductsPage(
                    page = uiState.value.page,
                    limit = pageSize,
                )
            val newItems =
                loadData.products
                    .map { it.toUiModel() }
                    .map { ShoppingItemUiModel(product = it, quantity = 0) }

            _uiState.update {
                it.copy(
                    products = it.products.plus(newItems),
                    page = it.page + 1,
                    canLoadMore = !loadData.isLast,
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

sealed interface ShoppingEvent {
    data class ShowError(
        val message: String,
    ) : ShoppingEvent
}

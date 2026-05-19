package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentItemRepository
import woowacourse.shopping.model.Product
import woowacourse.shopping.ui.model.mapper.toUiModel
import java.io.IOException

class CartViewModel(
    private val cartRepository: CartRepository,
    private val recentItemRepository: RecentItemRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val selectedCartItems = MutableStateFlow<ImmutableList<String>>(persistentListOf())
    private val page = MutableStateFlow(0)

    init {
        observeCartUiState()
        refreshCartItems()
    }

    private fun observeCartUiState() {
        viewModelScope.launch {
            combine(
                cartRepository.cartItems,
                selectedCartItems,
                page,
            ) { cartItems, selectedItems, page ->
                _uiState.value.toUiState(
                    cartItems = cartItems,
                    selectedItems = selectedItems,
                    page = page,
                    pageSize = PAGE_SIZE,
                )
            }.collect { nextState ->
                _uiState.value = nextState

                if (nextState.page != page.value) {
                    page.value = nextState.page
                }
                if (nextState.selectedCartItems != selectedCartItems.value) {
                    selectedCartItems.value = nextState.selectedCartItems
                }
            }
        }
    }

    private fun refreshCartItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                cartRepository.refreshCartItems()
            } catch (_: IOException) {
                _uiState.update { it.copy(errorMessage = "상품 불러오기 실패") }
            } catch (_: HttpException) {
                _uiState.update { it.copy(errorMessage = "상품 불러오기 실패") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun nextPage() {
        page.update { it + 1 }
    }

    fun previousPage() {
        page.update { it - 1 }
    }

    fun deleteItem(cartId: String) {
        viewModelScope.launch {
            cartRepository.deleteItem(cartId)
        }
    }

    fun updateQuantity(
        productId: String,
        quantity: Int,
    ) {
        viewModelScope.launch {
            cartRepository.setCartItem(productId, quantity = quantity)

            if (_uiState.value.isOrder) {
                cartRepository
                    .cartItems
                    .value
                    .firstOrNull { it.product.id == productId }
                    ?.let { cartItem ->
                        selectedCartItems.update { selectedItems ->
                            if (cartItem.id in selectedItems) {
                                selectedItems
                            } else {
                                (selectedItems + cartItem.id).toImmutableList()
                            }
                        }
                    }
            }
        }
    }

    fun checkItem(cartItemId: String) {
        selectedCartItems.update { selectedItemsId ->
            if (cartItemId in selectedItemsId) {
                (selectedItemsId - cartItemId).toImmutableList()
            } else {
                (selectedItemsId + cartItemId).toImmutableList()
            }
        }
    }

    fun isAllSelectClick() {
        val cartItems = cartRepository.cartItems.value

        selectedCartItems.update {
            if (_uiState.value.isAllChecked) {
                persistentListOf()
            } else {
                cartItems.map { it.id }.toImmutableList()
            }
        }
    }

    suspend fun loadRecommendProducts(): List<Product> {
        val productId = recentItemRepository.getLastViewedItem()?.id

        return productId?.let {
            val category = productRepository.getProductById(productId).category
            productRepository
                .getProducts(
                    category = category,
                    page = 0,
                    size = 10,
                ).products
        } ?: emptyList()
    }

    fun setOrder() {
        viewModelScope.launch {
            val cartItems = cartRepository.cartItems.value
            val cartProductIds = cartItems.map { it.product.id }.toSet()
            val recommendProducts =
                loadRecommendProducts()
                    .filter { product ->
                        product.id !in cartProductIds
                    }.map { product ->
                        product.toUiModel(quantity = null)
                    }.toImmutableList()

            _uiState.update {
                it.copy(
                    isOrder = true,
                    recommendProducts = recommendProducts,
                )
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 5

        fun provideFactory(
            cartRepository: CartRepository,
            recentItemRepository: RecentItemRepository,
            productRepository: ProductRepository,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    CartViewModel(
                        cartRepository = cartRepository,
                        recentItemRepository = recentItemRepository,
                        productRepository = productRepository,
                    )
                }
            }
    }
}

package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentItemRepository
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.ui.model.CartItemUiModel
import woowacourse.shopping.ui.model.mapper.toUiModel

class CartViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentItemRepository: RecentItemRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                getCartItemsByPage()
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun getCartItemsByPage(targetPage: Int = uiState.value.page) {
        cartRepository
            .getCartItemsByPage(page = targetPage, size = PAGE_SIZE)
            .onSuccess { cartResponseResult ->
                val items =
                    cartResponseResult.cartItems
                        .map { cartItem ->
                            cartItem.toUiModel(isSelected(cartItem.id))
                        }.toImmutableList()

                _uiState.update { state ->
                    val selectedCartItems =
                        state.selectedCartItems +
                            items
                                .filter { state.selectedCartItems.containsKey(it.id) }
                                .associate { it.id to it.toSelectedCartItem() }
                    state.copy(
                        page = targetPage,
                        items = items,
                        isCanMoveNext = cartResponseResult.isLastPage.not(),
                        totalCartQuantity = calculateTotalQuantity(selectedCartItems),
                        totalCartCount = cartResponseResult.totalElement,
                        selectedCartItems = selectedCartItems,
                        totalPrice = calculateTotalPrice(selectedCartItems),
                        isAllChecked =
                            selectedCartItems.size.toLong() == cartResponseResult.totalElement && cartResponseResult.totalElement > 0,
                        errorMessage = null,
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(errorMessage = "카트 정보를 불러오는데 실패했습니다.")
                }
            }
    }

    fun nextPage() {
        if (_uiState.value.isCanMoveNext.not()) return
        viewModelScope.launch {
            getCartItemsByPage(targetPage = uiState.value.page + 1)
        }
    }

    fun previousPage() {
        if (_uiState.value.page == 0) return
        viewModelScope.launch {
            getCartItemsByPage(targetPage = uiState.value.page - 1)
        }
    }

    fun deleteItem(cartId: Long) {
        viewModelScope.launch {
            cartRepository
                .deleteItem(cartId)
                .onSuccess {
                    val targetPage = calculatePage()
                    _uiState.update { state ->
                        val selectedItem = state.selectedCartItems - cartId

                        state.copy(
                            selectedCartItems = selectedItem,
                            totalCartQuantity = calculateTotalQuantity(selectedItem),
                            totalPrice = calculateTotalPrice(selectedItem),
                            isAllChecked = selectedItem.size.toLong() == state.totalCartCount - 1 && state.totalCartCount - 1 > 0,
                            errorMessage = null,
                        )
                    }
                    getCartItemsByPage(targetPage)
                }.onFailure {
                    _uiState.update { it.copy(errorMessage = "상품 삭제에 실패했습니다.") }
                }
        }
    }

    private fun calculatePage(): Int {
        val nextTotalCount = (_uiState.value.totalCartCount - 1).coerceAtLeast(0)
        val lastPage = ((nextTotalCount - 1) / PAGE_SIZE).coerceAtLeast(0)

        return minOf(_uiState.value.page, lastPage.toInt())
    }

    fun updateQuantity(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            cartRepository
                .setCartItem(productId, quantity = quantity)
                .onSuccess {
                    getCartItemsByPage()
                }.onFailure {
                    _uiState.update { it.copy(errorMessage = "수량 변경에 실패했습니다.") }
                }
        }
    }

    fun updateQuantityAndSelect(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            val previousCartItemId =
                cartRepository
                    .getAllCartItems()
                    .getOrNull()
                    ?.firstOrNull { it.product.id == productId }
                    ?.id

            cartRepository
                .setCartItem(productId, quantity = quantity)
                .onSuccess {
                    updateRecommendProductQuantity(productId, quantity)
                    if (quantity == 0) {
                        removeSelectedCartItem(previousCartItemId)
                    } else {
                        selectCartItem(productId)
                    }
                    getCartItemsByPage()
                }.onFailure {
                    _uiState.update { it.copy(errorMessage = "수량 변경에 실패했습니다.") }
                }
        }
    }

    fun loadRecommendProducts() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRecommendLoading = true,
                    recommendErrorMessage = null,
                )
            }

            val lastViewedItemId = recentItemRepository.getLastViewedItemId()
            if (lastViewedItemId == null) {
                _uiState.update {
                    it.copy(
                        isRecommendLoading = false,
                        recommendProducts = persistentListOf(),
                    )
                }
                return@launch
            }

            val lastViewedProduct =
                productRepository
                    .getProductById(lastViewedItemId)
                    .getOrElse {
                        _uiState.update {
                            it.copy(
                                isRecommendLoading = false,
                                recommendErrorMessage = "추천 상품을 불러오지 못했습니다.",
                            )
                        }
                        return@launch
                    }

            productRepository
                .getProducts(
                    category = lastViewedProduct.category,
                    page = RECOMMEND_PRODUCT_PAGE,
                    size = RECOMMEND_PRODUCT_SIZE,
                ).onSuccess { result ->
                    val quantityMap = cartRepository.getCartQuantityMap().first()
                    val cartProductIds = quantityMap.keys
                    val recommendProducts =
                        result.products
                            .filterNot { product -> product.id in cartProductIds }
                            .map { product -> product.toUiModel(quantity = 0) }
                            .toImmutableList()

                    _uiState.update {
                        it.copy(
                            recommendProducts = recommendProducts,
                            isRecommendLoading = false,
                            recommendErrorMessage = null,
                        )
                    }
                }.onFailure {
                    _uiState.update {
                        it.copy(
                            isRecommendLoading = false,
                            recommendErrorMessage = "추천 상품을 불러오지 못했습니다.",
                        )
                    }
                }
        }
    }

    private suspend fun selectCartItem(productId: Long) {
        cartRepository
            .getAllCartItems()
            .onSuccess { cartItems ->
                val cartItem = cartItems.firstOrNull { it.product.id == productId } ?: return@onSuccess
                _uiState.update { state ->
                    val selectedCartItems = state.selectedCartItems + (cartItem.id to cartItem.toSelectedCartItem())

                    state.copy(
                        selectedCartItems = selectedCartItems,
                        totalCartQuantity = calculateTotalQuantity(selectedCartItems),
                        totalPrice = calculateTotalPrice(selectedCartItems),
                        errorMessage = null,
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(errorMessage = "선택한 상품을 주문에 추가하지 못했습니다.") }
            }
    }

    private fun removeSelectedCartItem(cartItemId: Long?) {
        if (cartItemId == null) return

        _uiState.update { state ->
            val selectedCartItems = state.selectedCartItems - cartItemId

            state.copy(
                selectedCartItems = selectedCartItems,
                totalCartQuantity = calculateTotalQuantity(selectedCartItems),
                totalPrice = calculateTotalPrice(selectedCartItems),
                errorMessage = null,
            )
        }
    }

    private fun updateRecommendProductQuantity(
        productId: Long,
        quantity: Int,
    ) {
        _uiState.update { state ->
            state.copy(
                recommendProducts =
                    state.recommendProducts
                        .map { product ->
                            if (product.id == productId) {
                                product.copy(quantity = quantity)
                            } else {
                                product
                            }
                        }.toImmutableList(),
            )
        }
    }

    fun checkItem(cartItemId: Long) {
        _uiState.update { state ->
            val item = state.items.firstOrNull { it.id == cartItemId } ?: return@update state
            val selectedItems =
                if (state.selectedCartItems.containsKey(cartItemId)) {
                    state.selectedCartItems - cartItemId
                } else {
                    state.selectedCartItems + (cartItemId to item.toSelectedCartItem())
                }
            val items =
                state.items
                    .map { item ->
                        if (item.id == cartItemId) {
                            item.copy(isChecked = item.isChecked.not())
                        } else {
                            item
                        }
                    }.toImmutableList()

            state.copy(
                items = items,
                selectedCartItems = selectedItems,
                totalCartQuantity = calculateTotalQuantity(selectedItems),
                totalPrice = calculateTotalPrice(selectedItems),
                isAllChecked = selectedItems.size.toLong() == state.totalCartCount && state.totalCartCount > 0,
            )
        }
    }

    private fun isSelected(cartItemId: Long): Boolean = _uiState.value.selectedCartItems.containsKey(cartItemId)

    fun isAllSelectClick() {
        viewModelScope.launch {
            if (_uiState.value.isAllChecked) {
                _uiState.update { state ->
                    val items =
                        state.items
                            .map { it.copy(isChecked = false) }
                            .toImmutableList()
                    state.copy(
                        selectedCartItems = emptyMap(),
                        totalCartQuantity = 0,
                        totalPrice = 0,
                        isAllChecked = false,
                        items = items,
                        errorMessage = null,
                    )
                }
                return@launch
            }

            cartRepository
                .getAllCartItems()
                .onSuccess { cartItems ->
                    val selectedCartItem =
                        cartItems.associate { cartItem ->
                            cartItem.id to
                                SelectedCartItem(
                                    totalPrice = cartItem.getTotalPrice().amount,
                                    quantity = cartItem.quantity,
                                )
                        }

                    _uiState.update { state ->
                        val items = state.items.map { it.copy(isChecked = true) }.toImmutableList()
                        state.copy(
                            items = items,
                            selectedCartItems = selectedCartItem,
                            totalCartQuantity = calculateTotalQuantity(selectedCartItem),
                            totalPrice = calculateTotalPrice(selectedCartItem),
                            isAllChecked = true,
                            errorMessage = null,
                        )
                    }
                }.onFailure {
                    _uiState.update {
                        it.copy(errorMessage = "전체 선택에 실패했습니다.")
                    }
                }
        }
    }

    private fun CartItemUiModel.toSelectedCartItem(): SelectedCartItem =
        SelectedCartItem(
            totalPrice = totalPrice,
            quantity = quantity,
        )

    private fun CartItem.toSelectedCartItem(): SelectedCartItem =
        SelectedCartItem(
            totalPrice = getTotalPrice().amount,
            quantity = quantity,
        )

    private fun calculateTotalPrice(selectedCartItems: Map<Long, SelectedCartItem>): Long = selectedCartItems.values.sumOf { it.totalPrice }

    private fun calculateTotalQuantity(selectedCartItems: Map<Long, SelectedCartItem>): Int = selectedCartItems.values.sumOf { it.quantity }

    companion object {
        private const val PAGE_SIZE = 5
        private const val RECOMMEND_PRODUCT_PAGE = 0
        private const val RECOMMEND_PRODUCT_SIZE = 10

        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val appContainer = (this[APPLICATION_KEY] as ShoppingApplication).appContainer

                    CartViewModel(
                        cartRepository = appContainer.cartRepository,
                        productRepository = appContainer.productRepository,
                        recentItemRepository = appContainer.recentItemRepository,
                    )
                }
            }
    }
}

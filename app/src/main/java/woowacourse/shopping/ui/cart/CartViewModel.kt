package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.domain.model.cart.CartItems
import woowacourse.shopping.domain.model.cart.Quantity
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.UiEvent
import woowacourse.shopping.ui.util.toUiModel

class CartViewModel(
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val pagedCartItemsState = MutableStateFlow<PagedCartItems?>(null)
    private val selectedItemsState = MutableStateFlow<Set<Int>>(emptySet())
    private val recommendProductsState = MutableStateFlow<List<Product>>(emptyList())
    private val isCartOrRecommendState = MutableStateFlow(CartFlow.CART)
    private val allCartItemsState = MutableStateFlow<CartItems?>(null)
    private val _uiEvent = MutableSharedFlow<CartUiEvent>()
    val uiEvent: SharedFlow<CartUiEvent> = _uiEvent.asSharedFlow()
    private var currentPage = 0

    val uiState: StateFlow<CartUiState> =
        combine(
            pagedCartItemsState,
            selectedItemsState,
            recommendProductsState,
            isCartOrRecommendState,
            allCartItemsState,
        ) { pagedCartItems, selectedItems, recommendProducts, isCartOrRecommend, allCartItems ->

            pagedCartItems ?: return@combine CartUiState.Loading
            allCartItems ?: return@combine CartUiState.Loading

            val pagedItems = pagedCartItems.items
            val allCartIds = allCartItems.values.map { it.id }.toSet()

            val isAllSelected =
                allCartIds.isNotEmpty() &&
                    allCartIds.all { it in selectedItems }

            CartUiState.Success(
                cartItems = pagedItems.values.toUiModel(selectedItems, isAllSelected),
                selectedItems = selectedItems,
                isAllSelected = isAllSelected,
                currentPage = currentPage,
                totalPages = pagedCartItems.totalPages,
                hasPrevious = !pagedCartItems.isFirst,
                hasNext = !pagedCartItems.isLast,
                totalCount = allCartItems.calculateQuantity(selectedItems, isAllSelected),
                totalPrice = allCartItems.selectedCartItemsPrice(selectedItems, isAllSelected),
                recommendProducts = recommendProducts,
                currentFlow = isCartOrRecommend,
                quantitiesByProductId = pagedItems.values.associate { it.product.id to it.quantity.value },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartUiState.Loading,
        )

    init {
        refresh()
        viewModelScope.launch {
            cartRepository.cartEvents.collect {
                refresh()
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            refreshCartItems()
            loadRecommendProduct()
        }
    }

    private fun loadPage(page: Int) {
        viewModelScope.launch {
            pagedCartItemsState.update { null }
            refreshCartItems(page)
        }
    }

    private fun loadRecommendProduct() {
        viewModelScope.launch {
            val recommended = recentProductRepository.getMostRecentProduct() ?: return@launch
            val recommendedCategory = recommended.category
            val productList = productRepository.getCategoryProducts(recommendedCategory, 0, 100)
            val cartProducts =
                cartRepository
                    .getAllCartItems()
                    .values
                    .map { it.product }
                    .toSet()

            val result = productList.getCategoryProductsLimit(cartProducts, recommended)
            recommendProductsState.update { result }
        }
    }

    fun removeCartItem(cartId: Int) {
        viewModelScope.launch {
            selectedItemsState.update { it - cartId }
            cartRepository.remove(cartId)

            refreshCartItems()
            _uiEvent.emit(CartUiEvent.ShowSnackbar("장바구니에서 삭제했습니다"))
        }
    }

    fun addCartItem(product: Product) {
        viewModelScope.launch {
            val cartId = cartRepository.addProduct(product)
            selectedItemsState.update { it + cartId }
            refreshCartItems()
            _uiEvent.emit(CartUiEvent.ShowSnackbar("장바구니에 추가했습니다"))
        }
    }

    fun increaseRecommendProduct(productId: Int) {
        viewModelScope.launch {
            val target = findCartItemByProductId(productId) ?: return@launch
            cartRepository.increase(target.id, Quantity(target.quantity.value + 1))
            refreshCartItems()
        }
    }

    fun decreaseRecommendProduct(productId: Int) {
        viewModelScope.launch {
            val target = findCartItemByProductId(productId) ?: return@launch

            if (target.quantity.value == 1) {
                cartRepository.remove(target.id)
                selectedItemsState.update { it - target.id }
            } else {
                cartRepository.decrease(target.id, Quantity(target.quantity.value - 1))
            }
            refreshCartItems()
        }
    }

    fun increase(cartId: Int) {
        viewModelScope.launch {
            val target = findCartItemByCartId(cartId) ?: return@launch
            cartRepository.increase(cartId, Quantity(target.quantity.value + 1))
            refreshCartItems()
        }
    }

    fun decrease(cartId: Int) {
        viewModelScope.launch {
            val target = findCartItemByCartId(cartId) ?: return@launch
            if (target.quantity.value == 1) {
                cartRepository.remove(cartId)
                selectedItemsState.update { it - cartId }
            } else {
                cartRepository.decrease(cartId, Quantity(target.quantity.value - 1))
            }
            refreshCartItems()
        }
    }

    fun toggleSelection(id: Int) {
        selectedItemsState.update { current ->
            if (id in current) current - id else current + id
        }
    }

    fun toggleAllSelection() {
        val allCartItems = allCartItemsState.value ?: return
        val allCartIds = allCartItems.values.map { it.id }.toSet()
        selectedItemsState.update { selectedItems ->
            val isAllSelected =
                allCartIds.isNotEmpty() && allCartIds.all { it in selectedItems }

            if (isAllSelected) {
                selectedItems - allCartIds
            } else {
                selectedItems + allCartIds
            }
        }
    }

    fun goToNextPage() {
        if (pagedCartItemsState.value?.isLast != false) return
        loadPage(currentPage + 1)
    }

    fun goToPreviousPage() {
        if (pagedCartItemsState.value?.isFirst != false) return
        loadPage(currentPage - 1)
    }

    fun onClickOrder() {
        val selectedItems = selectedItemsState.value

        if (selectedItems.isEmpty()) return

        if (isCartOrRecommendState.value == CartFlow.CART) {
            isCartOrRecommendState.value = CartFlow.RECOMMEND
            return
        }

        viewModelScope.launch {
            _uiEvent.emit(CartUiEvent.OrderRequested(selectedItems.toList()))
        }
    }

    private suspend fun refreshCartItems(page: Int = currentPage) {
        val allResult = cartRepository.getAllCartItems()
        val cartItemTypeCount = allResult.values.size
        val targetPage = page.coerceAtMost(totalPage(cartItemTypeCount))

        val pagedResult = cartRepository.getCartItems(targetPage, PAGE_SIZE)

        currentPage = targetPage
        pagedCartItemsState.update { pagedResult }
        allCartItemsState.update { allResult }
    }

    private fun totalPage(totalCount: Int): Int =
        if (totalCount == 0) {
            0
        } else {
            (totalCount - 1) / PAGE_SIZE
        }

    private fun findCartItemByProductId(productId: Int): CartItem? = allCartItemsState.value?.values?.find { it.product.id == productId }

    private fun findCartItemByCartId(cartId: Int): CartItem? = allCartItemsState.value?.values?.find { it.id == cartId }

    companion object {
        private const val PAGE_SIZE = 5

        fun factory(
            cartRepository: CartRepository,
            recentProductRepository: RecentProductRepository,
            productRepository: ProductRepository,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    CartViewModel(cartRepository, recentProductRepository, productRepository)
                }
            }
    }
}

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
    private val _pagedCartItems = MutableStateFlow<PagedCartItems?>(null)
    private val _selectedItems = MutableStateFlow<Set<Int>>(emptySet())
    private val _recommendProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _isCartOrRecommend = MutableStateFlow(CartFlow.CART)
    private val _allCartItems = MutableStateFlow<CartItems?>(null)
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    private var currentPage = 0

    val uiState: StateFlow<CartUiState> =
        combine(
            _pagedCartItems,
            _selectedItems,
            _recommendProducts,
            _isCartOrRecommend,
            _allCartItems,
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

    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

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
            _pagedCartItems.update { null }
            refreshCartItems(page)
        }
    }

    private fun loadRecommendProduct() {
        viewModelScope.launch {
            val recommended = recentProductRepository.getMostRecentProduct() ?: return@launch
            val recommendedCategory = recommended.category
            val productList = productRepository.getCategoryProducts(recommendedCategory, 0, 100)
            val cartProducts =
                cartRepository.getAllCartItems()
                    .values
                    .map { it.product }
                    .toSet()

            val result = productList.getCategoryProductsLimit(cartProducts, recommended)
            _recommendProducts.update { result }
        }
    }

    fun removeCartItem(cartId: Int) {
        viewModelScope.launch {
            _selectedItems.update { it - cartId }
            cartRepository.remove(cartId)

            refreshCartItems()
            _uiEvent.emit(UiEvent.ShowSnackbar("장바구니에서 삭제했습니다"))
        }
    }

    fun addCartItem(product: Product) {
        viewModelScope.launch {
            val cartId = cartRepository.addProduct(product)
            _selectedItems.update { it + cartId }
            refreshCartItems()
            _uiEvent.emit(UiEvent.ShowSnackbar("장바구니에 추가했습니다"))
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
                _selectedItems.update { it - target.id }
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
                _selectedItems.update { it - cartId }
            } else {
                cartRepository.decrease(cartId, Quantity(target.quantity.value - 1))
            }
            refreshCartItems()
        }
    }

    fun toggleSelection(id: Int) {
        _selectedItems.update { current ->
            if (id in current) current - id else current + id
        }
    }

    fun toggleAllSelection() {
        val allCartItems = _allCartItems.value ?: return
        val allCartIds = allCartItems.values.map { it.id }.toSet()
        _selectedItems.update { selectedItems ->
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
        if (_pagedCartItems.value?.isLast != false) return
        loadPage(currentPage + 1)
    }

    fun goToPreviousPage() {
        if (_pagedCartItems.value?.isFirst != false) return
        loadPage(currentPage - 1)
    }

    fun onClickOrder() {
        val selectedItems = _selectedItems.value

        if (selectedItems.isEmpty()) return

        if (_isCartOrRecommend.value == CartFlow.CART) {
            _isCartOrRecommend.value = CartFlow.RECOMMEND
            return
        }

        viewModelScope.launch {
            cartRepository.order(selectedItems.toList())
            _uiEvent.emit(UiEvent.ShowSnackbar("주문이 완료되었습니다"))
            _uiEvent.emit(UiEvent.NavigateToProductList)
        }
    }

    private suspend fun refreshCartItems(page: Int = currentPage) {
        val allResult = cartRepository.getAllCartItems()
        val cartItemTypeCount = allResult.values.size
        val targetPage = page.coerceAtMost(totalPage(cartItemTypeCount))

        val pagedResult = cartRepository.getCartItems(targetPage, PAGE_SIZE)

        currentPage = targetPage
        _pagedCartItems.update { pagedResult }
        _allCartItems.update { allResult }
    }

    private fun totalPage(totalCount: Int): Int =
        if (totalCount == 0) {
            0
        } else {
            (totalCount - 1) / PAGE_SIZE
        }

    private fun findCartItemByProductId(productId: Int): CartItem? = _allCartItems.value?.values?.find { it.product.id == productId }
    private fun findCartItemByCartId(cartId: Int): CartItem? = _allCartItems.value?.values?.find { it.id == cartId }
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

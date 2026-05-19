package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.util.CoilUtils.result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.cart.CartItems
import woowacourse.shopping.domain.model.cart.Quantity
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.util.toUiModel

class CartViewModel(
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _pagedCartItems = MutableStateFlow<PagedCartItems?>(null)
    private val _selectedItems = MutableStateFlow<Set<Int>>(emptySet())
    private val _isAllSelected = MutableStateFlow(false)
    private val _recommendProducts = MutableStateFlow<List<Product>>(emptyList())

    private val _isCartOrRecommend = MutableStateFlow(CartFlow.CART)
    private var currentPage = 0

    val uiState: StateFlow<CartUiState> =
        combine(
            _pagedCartItems,
            _selectedItems,
            _isAllSelected,
            _recommendProducts,
            _isCartOrRecommend,
        ) { pagedCartItems, selectedItems, isAllSelected, recommendProducts, isCartOrRecommend ->
            pagedCartItems ?: return@combine CartUiState.Loading

            val cartItems = pagedCartItems.items

            CartUiState.Success(
                cartItems = cartItems.values.toUiModel(selectedItems, isAllSelected),
                selectedItems = selectedItems,
                isAllSelected = isAllSelected,
                currentPage = currentPage,
                totalPages = pagedCartItems.totalPages,
                hasPrevious = !pagedCartItems.isFirst,
                hasNext = !pagedCartItems.isLast,
                totalCount = cartItems.calculateQuantity(selectedItems, isAllSelected),
                totalPrice = cartItems.selectedCartItemsPrice(selectedItems, isAllSelected),
                recommendProducts = recommendProducts,
                currentFlow = isCartOrRecommend,
                quantitiesByProductId = cartItems.values.associate { it.product.id to it.quantity.value },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartUiState.Loading,
        )

    init {
        loadPage(0)
        loadRecommendProduct()
    }

    private fun loadPage(page: Int) {
        viewModelScope.launch {
            _pagedCartItems.update { null }
            val result = cartRepository.getCartItems(page, PAGE_SIZE)
            currentPage = page
            _pagedCartItems.update { result }
        }
    }

    private fun loadRecommendProduct() {
        viewModelScope.launch {
            val recommended = recentProductRepository.getMostRecentProduct() ?: return@launch
            val recommendedCategory = recommended.category.value
            val productList = productRepository.getCategoryProducts(recommendedCategory,0, 100)
            val cartProducts = cartRepository.getCartItems(0,100)
                .items
                .values
                .map { it.product }
                .toSet()
            val result = productList.getCategoryProductsLimit(cartProducts,recommended)
            _recommendProducts.update { result }
        }
    }

    fun removeCartItem(cartId: Int) {
        viewModelScope.launch {
            _pagedCartItems.update { null }
            _selectedItems.update { it - cartId }
            cartRepository.remove(cartId)
            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)
            _pagedCartItems.update { result }
        }
    }

    fun addCartItem(product: Product) {
        viewModelScope.launch {
            _pagedCartItems.update { null }
            cartRepository.addProduct(product)

            val result = cartRepository.getAllCartItems()
            _pagedCartItems.update { result }
            val cartItem =
                _pagedCartItems.value?.items?.values?.find { it.product.id == product.id } ?: return@launch

            _selectedItems.update {
                it + cartItem.id
            }
        }
    }

    fun increaseRecommendProduct(productId: Int) {
        viewModelScope.launch {
            val target =
                _pagedCartItems.value?.items?.values?.find { it.product.id == productId } ?: return@launch

            cartRepository.increase(target.id, Quantity(target.quantity.value + 1))
            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)
            _pagedCartItems.update { result }
        }
    }

    fun decreaseRecommendProduct(productId: Int) {
        viewModelScope.launch {
            val target =
                _pagedCartItems.value?.items?.values?.find { it.product.id == productId } ?: return@launch

            if (target.quantity.value == 1) {
                cartRepository.remove(target.id)
                _selectedItems.update { it - target.id }
            } else {
                cartRepository.decrease(target.id, Quantity(target.quantity.value - 1))
            }
            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)

            _pagedCartItems.update { result }
        }
    }

    fun increase(cartId: Int) {
        viewModelScope.launch {
            val target = _pagedCartItems.value?.items?.values?.find { it.id == cartId } ?: return@launch

            cartRepository.increase(cartId, Quantity(target.quantity.value + 1))
            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)

            _pagedCartItems.update { result }
        }
    }

    fun decrease(cartId: Int) {
        viewModelScope.launch {
            val target = _pagedCartItems.value?.items?.values?.find { it.id == cartId } ?: return@launch

            if (target.quantity.value == 1) {
                cartRepository.remove(cartId)
                _selectedItems.update { it - cartId }
            } else {
                cartRepository.decrease(cartId, Quantity(target.quantity.value - 1))
            }
            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)

            _pagedCartItems.update { result }
        }
    }

    fun toggleSelection(id: Int) {
        _selectedItems.update { current ->
            if (id in current) current - id else current + id
        }
    }

    fun toggleAllSelection() {
        _isAllSelected.update { !it }
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
        if (_isCartOrRecommend.value == CartFlow.CART) {
            _isCartOrRecommend.value = CartFlow.RECOMMEND
        } else {
            viewModelScope.launch {
                cartRepository.order(_selectedItems.value.toList())
            }
        }
    }

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

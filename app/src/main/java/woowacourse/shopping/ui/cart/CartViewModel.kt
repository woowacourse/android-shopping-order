package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.cart.CartItems
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.util.toUiModel

class CartViewModel(
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val cartItems = MutableStateFlow<CartItems?>(null)
    private val selectedItems = MutableStateFlow<Set<Int>>(emptySet())
    private val isAllSelected = MutableStateFlow(false)
    private val recommendProducts = MutableStateFlow<List<Product>>(emptyList())

    private val flow = MutableStateFlow(CartFlow.CART)
    private var currentPage = 0

    val uiState: StateFlow<CartUiState> =
        combine(
            cartItems,
            selectedItems,
            isAllSelected,
            recommendProducts,
            flow,
        ) { cartItems, selectedItems, isAllSelected, recommendProducts, flow ->
            cartItems ?: return@combine CartUiState.Loading
            CartUiState.Success(
                cartItems = cartItems.values.toUiModel(selectedItems, isAllSelected),
                selectedItems = selectedItems,
                isAllSelected = isAllSelected,
                currentPage = currentPage,
                totalPages = cartItems.totalPages,
                hasPrevious = !cartItems.isFirst,
                hasNext = !cartItems.isLast,
                totalCount = cartItems.calculateQuantity(selectedItems, isAllSelected),
                totalPrice = cartItems.calculatePrice(selectedItems, isAllSelected),
                recommendProducts = recommendProducts,
                currentFlow = flow,
                quantitiesByProductId = cartItems.values.associate { it.product.id to it.quantity.value },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartUiState.Loading,
        )

    init {
        init()
    }

    private fun init() {
        viewModelScope.launch {
            loadPage(currentPage)
            loadRecommendProduct()
        }
    }

    private suspend fun loadPage(page: Int) {
        cartItems.update { null }
        val result = cartRepository.getCartItems(page, PAGE_SIZE)
        currentPage = page
        cartItems.update { result }
    }

    private suspend fun loadRecommendProduct() {
        val recommended = recentProductRepository.getMostRecentProduct() ?: return
        val productList = productRepository.getProducts(0, Int.MAX_VALUE)
        val categoryProducts = productList.getCategoryProducts(recommended.category.value)

        val result =
            categoryProducts -
                (
                    cartItems.value?.values?.map { it.product }
                        ?: emptyList()
                ).toSet()
        recommendProducts.update { result }
    }

    fun removeCartItem(cartId: Int) {
        viewModelScope.launch {
            cartItems.update { null }
            selectedItems.update { it - cartId }
            cartRepository.remove(cartId)
            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)
            cartItems.update { result }
        }
    }

    fun addCartItem(product: Product) {
        viewModelScope.launch {
            cartItems.update { null }
            cartRepository.addProduct(product)

            val result = cartRepository.getAllCartItems()
            cartItems.update { result }
            val cartItem =
                cartItems.value?.values?.find { it.product.id == product.id } ?: return@launch

            selectedItems.update {
                it + cartItem.id
            }
        }
    }

    fun increaseRecommendProduct(productId: Int) {
        viewModelScope.launch {
            val target =
                cartItems.value?.values?.find { it.product.id == productId } ?: return@launch

            cartRepository.updateQuantity(target.id, target.quantity.value + 1)
            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)
            cartItems.update { result }
        }
    }

    fun decreaseRecommendProduct(productId: Int) {
        viewModelScope.launch {
            val target =
                cartItems.value?.values?.find { it.product.id == productId } ?: return@launch

            if (target.quantity.value == 1) {
                cartRepository.remove(target.id)
                selectedItems.update { it - target.id }
            } else {
                cartRepository.updateQuantity(target.id, target.quantity.value - 1)
            }
            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)

            cartItems.update { result }
        }
    }

    fun increase(cartId: Int) {
        viewModelScope.launch {
            val target = cartItems.value?.values?.find { it.id == cartId } ?: return@launch

            cartRepository.updateQuantity(cartId, target.quantity.value + 1)
            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)

            cartItems.update { result }
        }
    }

    fun decrease(cartId: Int) {
        viewModelScope.launch {
            val target = cartItems.value?.values?.find { it.id == cartId } ?: return@launch

            if (target.quantity.value == 1) {
                cartRepository.remove(cartId)
                selectedItems.update { it - cartId }
            } else {
                cartRepository.updateQuantity(cartId, target.quantity.value - 1)
            }
            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)

            cartItems.update { result }
        }
    }

    fun toggleSelection(id: Int) {
        selectedItems.update { current ->
            if (id in current) current - id else current + id
        }
    }

    fun toggleAllSelection() {
        isAllSelected.update { !it }
    }

    fun goToNextPage() {
        if (cartItems.value?.isLast != false) return
        viewModelScope.launch { loadPage(currentPage + 1) }
    }

    fun goToPreviousPage() {
        if (cartItems.value?.isFirst != false) return
        viewModelScope.launch { loadPage(currentPage - 1) }
    }

    fun onClickOrder() {
        if (flow.value == CartFlow.CART) {
            flow.value = CartFlow.RECOMMEND
        } else {
            viewModelScope.launch {
                cartRepository.order(selectedItems.value.toList())
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 5

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShoppingApplication)
                CartViewModel(
                    application.appContainer.cartRepository,
                    application.appContainer.recentProductRepository,
                    application.appContainer.productRepository,
                )
            }
        }
    }
}

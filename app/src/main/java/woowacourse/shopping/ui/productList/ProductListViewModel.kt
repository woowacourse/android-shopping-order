package woowacourse.shopping.ui.productList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.constant.Format.formatPrice
import woowacourse.shopping.domain.cart.CartItems
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.Products
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.util.LoadState

class ProductListViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel() {
    private val productsFlow = MutableStateFlow(Products(items = emptyList(), isLast = false))
    private val recentProductsFlow = MutableStateFlow<List<Product>>(emptyList())
    private val cartFlow = MutableStateFlow(CartItems())

    private val loadStateFlow = MutableStateFlow<LoadState>(LoadState.Initial)

    private var currentPage = 0

    val uiState: StateFlow<ProductListUiState> =
        combine(
            productsFlow,
            recentProductsFlow,
            cartFlow,
            loadStateFlow,
        ) { products, recents, cart, loadState ->
            ProductListUiState(
                products = products.toUiModel(cartFlow.value),
                recentProducts = recents,
                hasNextPage = !products.isLast,
                loadState = loadState,
                totalCartAmount = cart.totalQuantity.toString(),
                showCartAmountBadge = cart.totalQuantity > 0,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProductListUiState())

    fun Products.toUiModel(cartItems: CartItems): List<ProductUiModel> =
        this.items.map { product ->
            product.toUiModel(cartItems.findQuantity(product.id).value)
        }

    fun Product.toUiModel(cartAmount: Int): ProductUiModel =
        ProductUiModel(
            id = this.id,
            name = this.name.value,
            price = formatPrice(this.price.value),
            imageUrl = this.imageUrl.value,
            cartAmount = cartAmount.toString(),
            showAmountController = cartAmount > 0,
        )

    init {
        observeRecentProducts()
        getMoreProducts()
    }

    fun refresh() {
        viewModelScope.launch {
            refreshCart()
        }
    }

    fun getMoreProducts() {
        val state = uiState.value
        if (state.loadState == LoadState.Loading || !state.hasNextPage) return

        viewModelScope.launch {
            loadStateFlow.update { LoadState.Loading }
            runCatching { productRepository.getProducts(currentPage, PAGE_SIZE) }
                .onSuccess { newProducts ->
                    productsFlow.update {
                        Products(
                            items = it.items + newProducts.items,
                            isLast = newProducts.isLast,
                        )
                    }
                    currentPage++

                    refreshCart()
                    loadStateFlow.update { LoadState.Success }
                }.onFailure { throwable ->
                    loadStateFlow.update {
                        LoadState.Error(
                            throwable,
                            throwable.message ?: "다음 페이지를 불러올 수 없습니다.",
                        )
                    }
                }
        }
    }

    fun addProduct(productId: Int) {
        viewModelScope.launch {
            cartRepository.addProduct(productId, 1)
            refreshCart()
        }
    }

    fun increaseQuantity(productId: Int) {
        viewModelScope.launch {
            val target = cartFlow.value.findByProductId(productId) ?: return@launch
            cartRepository.updateQuantity(target.id, target.quantity.value + 1)
            refreshCart()
        }
    }

    fun decreaseQuantity(productId: Int) {
        viewModelScope.launch {
            val target = cartFlow.value.findByProductId(productId) ?: return@launch

            cartRepository.updateQuantity(target.id, target.quantity.value - 1)
            refreshCart()
        }
    }

    private fun observeRecentProducts() {
        recentProductRepository
            .getRecentProducts()
            .onEach { recentProductsFlow.value = it }
            .launchIn(viewModelScope)
    }

    private suspend fun refreshCart() {
        val cartItems = cartRepository.getAllCartItems()
        cartFlow.update { cartItems }
    }

    companion object {
        private const val PAGE_SIZE = 20

        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application = (this[APPLICATION_KEY] as ShoppingApplication)
                    val appContainer = application.appContainer
                    ProductListViewModel(
                        appContainer.productRepository,
                        appContainer.cartRepository,
                        appContainer.recentProductRepository,
                    )
                }
            }
    }
}

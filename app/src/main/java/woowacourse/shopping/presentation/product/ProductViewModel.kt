package woowacourse.shopping.presentation.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.usecase.AddToCartUseCase
import woowacourse.shopping.domain.usecase.DecreaseProductQuantityUseCase
import woowacourse.shopping.domain.usecase.IncreaseProductQuantityUseCase
import woowacourse.shopping.presentation.CartItemUiModel
import woowacourse.shopping.presentation.ResultState
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.cart.CartCounterClickListener
import woowacourse.shopping.presentation.toDomain
import woowacourse.shopping.presentation.toPresentation

class ProductViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val increaseProductQuantityUseCase: IncreaseProductQuantityUseCase,
    private val decreaseProductQuantityUseCase: DecreaseProductQuantityUseCase,
    private val addToCartUseCase: AddToCartUseCase,
) : ViewModel(),
    ItemClickListener,
    CartCounterClickListener {
    private val _uiState: MutableLiveData<ResultState<Unit>> = MutableLiveData()
    val uiState: LiveData<ResultState<Unit>> = _uiState
    private val _products: MutableLiveData<List<ProductItemType>> = MutableLiveData()
    val products: LiveData<List<ProductItemType>> = _products
    private val _recentProducts: MutableLiveData<List<Product>> = MutableLiveData()
    val recentProducts: LiveData<List<Product>> = _recentProducts
    private val _cartItemCount = MutableLiveData<Int>()
    val cartItemCount: LiveData<Int> = _cartItemCount
    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage
    private val _navigateTo = SingleLiveData<Long>()
    val navigateTo: LiveData<Long> = _navigateTo

    private var currentPage = FIRST_PAGE

    init {
        viewModelScope.launch {
            val cachedCartItemsInRepository = cartRepository.fetchAllCartItems()
            cachedCartItemsInRepository
                .onSuccess {
                    fetchData()
                    fetchCartItemCount()
                }.onFailure {
                    _toastMessage.value = R.string.cart_toast_cache_fail
                }
        }
    }

    fun fetchData() {
        viewModelScope.launch {
            _uiState.value = ResultState.Loading
            productRepository
                .fetchPagingProducts(currentPage, FETCH_PAGE_SIZE)
                .onSuccess { cartItems ->
                    val productItemTypes = cartItems.toProductItemTypes()
                    val isNeedUpdate = products.value?.containsAll(productItemTypes)?.not() ?: true
                    if (isNeedUpdate) {
                        val currentList =
                            _products.value?.filterIsInstance<ProductItemType.Product>()
                                ?: emptyList()
                        _products.value = currentList + productItemTypes
                    }
                    _uiState.value = ResultState.Success(Unit)
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_load_failure
                }
        }

        viewModelScope.launch {
            val recentProducts = recentProductRepository.getRecentProducts()
            recentProducts
                .onSuccess { _recentProducts.value = it }
                .onFailure { _toastMessage.value = R.string.product_toast_load_failure }
        }
    }

    fun fetchCartItemCount() {
        viewModelScope.launch {
            val totalCount = cartRepository.fetchTotalCount()
            totalCount
                .onSuccess { _cartItemCount.value = it }
                .onFailure {
                    _toastMessage.value = R.string.product_toast_load_total_cart_quantity_fail
                }
        }
    }

    fun loadMore() {
        this.currentPage++
        viewModelScope.launch {
            productRepository
                .fetchPagingProducts(currentPage, FETCH_PAGE_SIZE)
                .onSuccess { newItems ->
                    val updatedItems = newItems.toProductItemTypes()
                    val currentList =
                        _products.value?.filterIsInstance<ProductItemType.Product>() ?: emptyList()
                    val updatedList = currentList + updatedItems

                    _products.value = updatedList
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_load_failure
                }
        }
    }

    override fun onClickProductItem(productId: Long) {
        _navigateTo.value = productId
    }

    override fun onClickAddToCart(cartItemUiModel: CartItemUiModel) {
        viewModelScope.launch {
            addToCartUseCase(
                product = cartItemUiModel.product.toDomain(),
                quantity = 1,
            ).onSuccess {
                updateQuantity(productId = cartItemUiModel.product.id, 1)
                fetchCartItemCount()
            }.onFailure { _toastMessage.value = R.string.product_toast_add_cart_fail }
        }
    }

    override fun onClickMinus(id: Long) {
        viewModelScope.launch {
            decreaseProductQuantityUseCase(
                id,
            ).onSuccess {
                updateQuantity(id, -1)
                fetchCartItemCount()
            }.onFailure { _toastMessage.value = R.string.product_toast_decrease_fail }
        }
    }

    override fun onClickPlus(id: Long) {
        viewModelScope.launch {
            increaseProductQuantityUseCase(id)
                .onSuccess {
                    updateQuantity(id, 1)
                    fetchCartItemCount()
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_increase_fail
                }
        }
    }

    private fun updateQuantity(
        productId: Long,
        delta: Int,
    ) {
        val currentItems = _products.value ?: return
        val index =
            currentItems.indexOfFirst {
                it is ProductItemType.Product && it.cartItemUiModel.product.id == productId
            }

        if (index == -1) return

        val updateItems = currentItems.toMutableList()
        val updateItem = updateItems[index] as ProductItemType.Product
        val updatedItem =
            updateItem.copy(
                cartItemUiModel =
                    updateItem.cartItemUiModel.copy(
                        quantity = updateItem.cartItemUiModel.quantity + delta,
                    ),
            )
        updateItems[index] = updatedItem

        _products.value = updateItems.toList()
    }

    private fun List<CartItem>.toProductItemTypes(): List<ProductItemType> =
        if (hasLoadMore(this)) {
            this
                .take(PAGE_SIZE)
                .map { ProductItemType.Product(it.toPresentation()) } + ProductItemType.LoadMore
        } else {
            this.map { ProductItemType.Product(it.toPresentation()) }
        }

    private fun hasLoadMore(cartItems: List<CartItem>) = cartItems.size == FETCH_PAGE_SIZE

    companion object {
        private const val FIRST_PAGE = 0
        private const val PAGE_SIZE = 20
        private const val FETCH_PAGE_SIZE = 21
    }
}

package woowacourse.shopping.presentation.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.usecase.CheckLastPageUseCase
import woowacourse.shopping.domain.usecase.FetchProductsWithCartItemUseCase
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.UiState

class ProductViewModel(
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
    private val fetchProductsWithCartItemUseCase: FetchProductsWithCartItemUseCase,
    private val checkLastPageUseCase: CheckLastPageUseCase,
) : ViewModel() {
    private val _uiState: MutableLiveData<UiState<Unit>> = MutableLiveData()
    val uiState: LiveData<UiState<Unit>> = _uiState

    private val _products: MutableLiveData<List<CartItem>> = MutableLiveData()
    val products: LiveData<List<CartItem>> = _products

    private val _recentProducts: MutableLiveData<List<Product>> = MutableLiveData()
    val recentProducts: LiveData<List<Product>> = _recentProducts

    private val _cartItemCount = MutableLiveData<Int>()
    val cartItemCount: LiveData<Int> = _cartItemCount

    private val _showLoadMore: MutableLiveData<Boolean> = MutableLiveData(true)
    val showLoadMore: LiveData<Boolean> = _showLoadMore

    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    private var currentPage = FIRST_PAGE

    init {
        viewModelScope.launch {
            cartRepository
                .fetchAllCartItems()
                .onSuccess {
                    val fetchDataDeferred = async { fetchData() }
                    val fetchCountDeferred = async { fetchCartItemCount() }
                    fetchDataDeferred.await()
                    fetchCountDeferred.await()
                }.onFailure { _toastMessage.value = R.string.cart_toast_load_fail }
        }
    }

    fun fetchData(currentPage: Int = FIRST_PAGE) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val productsResult = async { fetchProductsWithCartItemUseCase(currentPage, PAGE_SIZE) }
            val recentProductsResult = async { recentProductRepository.getRecentProducts() }

            productsResult
                .await()
                .onSuccess { cartItems ->
                    _products.value = cartItems
                    _uiState.value = UiState.Success(Unit)
                }.onFailure { throwable ->
                    _uiState.value = UiState.Failure(throwable)
                }

            recentProductsResult
                .await()
                .onSuccess { recentProducts ->
                    _recentProducts.value = recentProducts
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_most_recent_load_fail
                }
        }
    }

    fun fetchCartItemCount() {
        viewModelScope.launch {
            cartRepository
                .fetchTotalCount()
                .onSuccess { count ->
                    _cartItemCount.value = count
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_load_total_cart_quantity_fail
                }
        }
    }

    fun loadMore() {
        val nextPage = currentPage + 1

        viewModelScope.launch {
            fetchProductsWithCartItemUseCase(nextPage, PAGE_SIZE)
                .onSuccess { newItems ->
                    val currentList = _products.value.orEmpty()
                    val updatedList = currentList + newItems
                    _products.value = updatedList
                    currentPage = nextPage
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_load_failure
                }

            checkLastPageUseCase(currentPage)
                .onSuccess { isLastPage ->
                    _showLoadMore.value = !isLastPage
                }.onFailure { _toastMessage.value = R.string.product_toast_get_last_page_fail }
        }
    }

    fun increaseQuantity(productId: Long) {
        viewModelScope.launch {
            cartRepository
                .increaseQuantity(productId)
                .onSuccess {
                    updateQuantity(productId, 1)
                    fetchCartItemCount()
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_increase_fail
                }
        }
    }

    fun decreaseQuantity(productId: Long) {
        viewModelScope.launch {
            cartRepository
                .decreaseQuantity(productId)
                .onSuccess {
                    updateQuantity(productId, -1)
                    fetchCartItemCount()
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_decrease_fail
                }
        }
    }

    fun addToCart(cartItem: CartItem) {
        viewModelScope.launch {
            cartRepository
                .insertProduct(cartItem.product, 1)
                .onSuccess {
                    updateQuantity(productId = cartItem.product.productId, 1)
                    fetchCartItemCount()
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_add_cart_fail
                }
        }
    }

    private fun updateQuantity(
        productId: Long,
        delta: Int,
    ) {
        val currentItems = _products.value ?: return
        val updatedItem =
            currentItems.map {
                if (it.product.productId == productId) it.copy(quantity = it.quantity + delta) else it
            }
        _products.value = updatedItem
    }

    companion object {
        private const val FIRST_PAGE = 0
        private const val PAGE_SIZE = 12
    }
}

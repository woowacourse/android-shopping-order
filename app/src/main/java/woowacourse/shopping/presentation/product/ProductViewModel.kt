package woowacourse.shopping.presentation.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.presentation.ResultState
import woowacourse.shopping.presentation.SingleLiveData

class ProductViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel() {
    private val _uiState: MutableLiveData<ResultState<Unit>> = MutableLiveData()
    val uiState: LiveData<ResultState<Unit>> = _uiState
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
        cartRepository.fetchAllCartItems {
            fetchData()
            fetchCartItemCount()
        }
    }

    fun fetchData(currentPage: Int = FIRST_PAGE) {
        _uiState.value = ResultState.Loading

        productRepository.fetchPagingProducts(currentPage, PAGE_SIZE) { result ->
            result
                .onSuccess { cartItems ->
                    this.currentPage = currentPage
                    _products.postValue(cartItems)
                    _uiState.value = ResultState.Success(Unit)
                }.onFailure {
                    _toastMessage.postValue(R.string.product_toast_load_failure)
                }
        }

        recentProductRepository.getRecentProducts { result ->
            result
                .onSuccess { products ->
                    _recentProducts.postValue(products)
                }.onFailure {
                    _toastMessage.postValue(R.string.product_toast_load_failure)
                }
        }
    }

    fun fetchCartItemCount() {
        cartRepository.getTotalCount { result ->
            result
                .onSuccess { count ->
                    _cartItemCount.postValue(count)
                }.onFailure {
                    _toastMessage.postValue(R.string.product_toast_load_total_cart_quantity_fail)
                }
        }
    }

    fun loadMore() {
        this.currentPage++
        productRepository.fetchPagingProducts(currentPage, PAGE_SIZE) { result ->
            result.fold(
                onSuccess = { newItems ->
                    val currentList = _products.value.orEmpty()
                    val updatedList = currentList + newItems
                    _products.postValue(updatedList)
                    _showLoadMore.postValue(updatedList.size < 100)
                },
                onFailure = {
                    _toastMessage.postValue(R.string.product_toast_load_failure)
                },
            )
        }
    }

    fun increaseQuantity(productId: Long) {
        cartRepository.increaseQuantity(productId) { result ->
            result
                .onSuccess {
                    updateQuantity(productId, 1)
                    fetchCartItemCount()
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_increase_fail
                }
        }
    }

    fun decreaseQuantity(productId: Long) {
        cartRepository.decreaseQuantity(productId) { result ->
            result
                .onSuccess {
                    updateQuantity(productId, -1)
                    fetchCartItemCount()
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_increase_fail
                }
        }
    }

    fun addToCart(cartItem: CartItem) {
        cartRepository.insertProduct(cartItem.product, 1) { result ->
            result
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
        _products.postValue(updatedItem)
    }

    companion object {
        private const val FIRST_PAGE = 0
        private const val PAGE_SIZE = 12
    }
}

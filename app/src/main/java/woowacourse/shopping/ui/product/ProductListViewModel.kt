package woowacourse.shopping.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.common.MutableSingleLiveData
import woowacourse.shopping.common.OnItemQuantityChangeListener
import woowacourse.shopping.common.OnProductItemClickListener
import woowacourse.shopping.common.SingleLiveData
import woowacourse.shopping.common.UniversalViewModelFactory
import woowacourse.shopping.common.currentPageIsNullException
import woowacourse.shopping.data.cart.remote.DefaultCartItemRepository
import woowacourse.shopping.data.history.local.DefaultProductHistoryRepository
import woowacourse.shopping.data.product.remote.DefaultProductRepository
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.domain.model.ProductIdsCount.Companion.DECREASE_VARIATION
import woowacourse.shopping.domain.model.ProductIdsCount.Companion.INCREASE_VARIATION
import woowacourse.shopping.domain.repository.cart.CartItemRepository
import woowacourse.shopping.domain.repository.history.ProductHistoryRepository
import woowacourse.shopping.domain.repository.product.ProductRepository
import woowacourse.shopping.ui.ResponseHandler.handleResponseResult

class ProductListViewModel(
    private val productsRepository: ProductRepository,
    private val productHistoryRepository: ProductHistoryRepository,
    private val cartItemRepository: CartItemRepository,
    private var _currentPage: MutableLiveData<Int> = MutableLiveData(FIRST_PAGE),
) : ViewModel(), OnProductItemClickListener, OnItemQuantityChangeListener {
    val currentPage: LiveData<Int> get() = _currentPage

    private val _loadedProducts: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
    val loadedProducts: LiveData<List<Product>> get() = _loadedProducts

    private val _productsHistory: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
    val productsHistory: LiveData<List<Product>> get() = _productsHistory

    private val _cartProductTotalCount: MutableLiveData<Int> = MutableLiveData()
    val cartProductTotalCount: LiveData<Int> get() = _cartProductTotalCount

    private var _isLastPage: MutableLiveData<Boolean> = MutableLiveData()
    val isLastPage: LiveData<Boolean> get() = _isLastPage

    private var _detailProductDestinationId: MutableSingleLiveData<Long> = MutableSingleLiveData()
    val detailProductDestinationId: SingleLiveData<Long> get() = _detailProductDestinationId

    private var _shoppingCartDestination: MutableSingleLiveData<Boolean> = MutableSingleLiveData()
    val shoppingCartDestination: SingleLiveData<Boolean> get() = _shoppingCartDestination

    private var _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> get() = _errorMessage

    override fun onClick(productId: Long) {
        _detailProductDestinationId.setValue(productId)
    }

    override fun onIncrease(
        productId: Long,
        quantity: Int,
    ) {
        updateQuantity(ProductIdsCount(productId, quantity), INCREASE_VARIATION)
    }

    override fun onDecrease(
        productId: Long,
        quantity: Int,
    ) {
        updateQuantity(ProductIdsCount(productId, quantity), DECREASE_VARIATION)
    }

    fun loadAll() {
        viewModelScope.launch {
            val page = currentPage.value ?: currentPageIsNullException()
            handleResponseResult(productsRepository.loadProducts(page), _errorMessage) { productsPage ->
                _loadedProducts.value = productsPage.products
                _isLoading.value = false
                _isLastPage.postValue(productsPage.isLastPage)
            }
            updateCartItemsCount()
            loadProductsHistory()
        }
    }

    fun loadNextPageProducts() {
        viewModelScope.launch {
            if (isLastPage.value == true) return@launch
            val nextPage = _currentPage.value?.plus(PAGE_MOVE_COUNT) ?: currentPageIsNullException()
            handleResponseResult(productsRepository.loadProducts(nextPage), _errorMessage) { productsPage ->
                val oldProducts = loadedProducts.value ?: emptyList()
                _loadedProducts.value = oldProducts + productsPage.products
                _isLastPage.postValue(productsPage.isLastPage)
            }
            updateCartItemsCount()
            _currentPage.postValue(nextPage)
        }
    }

    fun navigateToShoppingCart() {
        _shoppingCartDestination.setValue(true)
    }

    private suspend fun loadProductsHistory() {
       productHistoryRepository.loadProductsHistory()
            .onSuccess { productHistory ->
                _productsHistory.value = productHistory
            }.onFailure {
               _errorMessage.value = it.message
           }
    }

    private suspend fun updateCartItemsCount() {
        handleResponseResult(cartItemRepository.calculateCartItemsCount(), _errorMessage) { totalCount ->
            _cartProductTotalCount.value = totalCount
        }
    }

    private fun updateQuantity(
        productIdsCount: ProductIdsCount,
        variation: Int,
    ) {
        viewModelScope.launch {
            cartItemRepository.updateProductQuantity(productIdsCount.productId, productIdsCount.quantity)
            handleResponseResult(cartItemRepository.calculateCartItemsCount(), _errorMessage) { totalCount ->
                updateProductQuantity(productIdsCount.productId, variation, totalCount)
            }
        }
    }

    private fun updateProductQuantity(
        productId: Long,
        variation: Int,
        totalCount: Int,
    ) {
        _loadedProducts.value =
            loadedProducts.value?.map { product ->
                val quantity: Int = product.quantity + variation
                product.takeIf { it.id == productId }?.copy(quantity = quantity) ?: product
            }
        _cartProductTotalCount.value = totalCount
    }

    companion object {
        private const val TAG = "ProductListViewModel"
        private const val FIRST_PAGE = 0
        private const val PAGE_MOVE_COUNT = 1

        fun factory(
            productRepository: ProductRepository =
                DefaultProductRepository(
                    ShoppingApp.productSource,
                    ShoppingApp.cartSource,
                ),
            historyRepository: ProductHistoryRepository =
                DefaultProductHistoryRepository(
                    ShoppingApp.historySource,
                    ShoppingApp.productSource,
                ),
            cartItemRepository: CartItemRepository =
                DefaultCartItemRepository(
                    ShoppingApp.cartSource,
                ),
        ): UniversalViewModelFactory =
            UniversalViewModelFactory {
                ProductListViewModel(productRepository, historyRepository, cartItemRepository)
            }
    }
}

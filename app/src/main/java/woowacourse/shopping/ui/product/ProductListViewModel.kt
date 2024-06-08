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
import woowacourse.shopping.data.cart.DefaultCartItemRepository
import woowacourse.shopping.data.common.ResponseHandlingUtils.onError
import woowacourse.shopping.data.common.ResponseHandlingUtils.onException
import woowacourse.shopping.data.common.ResponseHandlingUtils.onSuccess
import woowacourse.shopping.data.history.DefaultProductHistoryRepository
import woowacourse.shopping.data.product.DefaultProductRepository
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.cart.CartItemRepository
import woowacourse.shopping.domain.repository.history.ProductHistoryRepository
import woowacourse.shopping.domain.repository.product.ProductRepository

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

    fun loadAll() {
        viewModelScope.launch {
            val page = currentPage.value ?: currentPageIsNullException()
            (FIRST_PAGE..page).forEach {
                productsRepository.loadProducts(it).onSuccess { products ->
                    _loadedProducts.value = products
                    _isLoading.value = false
                }.onError { code, message ->
                    // TODO: Error Handling
                }.onException {
                    // TODO: Exception Handling
                }
            }

            productsRepository.isFinalPage(page).onSuccess {
                _isLastPage.postValue(it)
            }.onError { code, message ->
                // TODO: Error Handling
            }.onException {
                // TODO: Exception Handling
            }

            cartItemRepository.calculateCartItemsCount().onSuccess { totalCartCount ->
                _cartProductTotalCount.value = totalCartCount
            }.onError { code, message ->
                // TODO: Error Handling
            }.onException {
                // TODO: Exception Handling
            }

            val productHistory = productHistoryRepository.loadProductsHistory()
            _productsHistory.value = productHistory
        }
    }

    fun loadNextPageProducts() {
        viewModelScope.launch {
            if (isLastPage.value == true) return@launch
            val nextPage = _currentPage.value?.plus(PAGE_MOVE_COUNT) ?: currentPageIsNullException()

            productsRepository.isFinalPage(nextPage).onSuccess {
                _isLastPage.postValue(it)
            }.onError { code, message ->
                // TODO: Error Handling
            }.onException {
                // TODO: Exception Handling
            }

            productsRepository.loadProducts(nextPage).onSuccess { products ->
                _loadedProducts.value = _loadedProducts.value?.toMutableList()?.apply { addAll(products) }
            }.onError { code, message ->
                // TODO: Error Handling
            }.onException {
                // TODO: Exception Handling
            }

            cartItemRepository.calculateCartItemsCount().onSuccess { totalCount ->
                _cartProductTotalCount.value = totalCount
            }.onError { code, message ->
                // TODO: Error Handling
            }.onException {
                // TODO: Exception Handling
            }

            _currentPage.postValue(nextPage)
        }
    }

    fun navigateToShoppingCart() {
        _shoppingCartDestination.setValue(true)
    }

    override fun onClick(productId: Long) {
        _detailProductDestinationId.setValue(productId)
    }

    override fun onIncrease(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            cartItemRepository.updateProductQuantity(productId, quantity)
            cartItemRepository.calculateCartItemsCount().onSuccess { totalCount ->
                updateProductQuantity(productId, INCREASE_VARIATION, totalCount)
            }.onError { code, message ->
                // TODO: Error Handling
            }.onException {
                // TODO: Exception Handling
            }
        }
    }

    override fun onDecrease(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            cartItemRepository.updateProductQuantity(productId, quantity)
            cartItemRepository.calculateCartItemsCount().onSuccess { totalCount ->
                updateProductQuantity(productId, DECREASE_VARIATION, totalCount)
            }.onError { code, message ->
                // TODO: Error Handling
            }.onException {
                // TODO: Exception Handling
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
        private const val FIRST_PAGE = 1
        private const val PAGE_MOVE_COUNT = 1
        private const val INCREASE_VARIATION = 1
        private const val DECREASE_VARIATION = -1

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

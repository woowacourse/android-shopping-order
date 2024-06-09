package woowacourse.shopping.ui.product

import android.util.Log
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
import woowacourse.shopping.domain.model.ProductIdsCount
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

            (FIRST_PAGE..page).forEach {
                productsRepository.loadProducts(it).onSuccess { productsInformation ->
                    _loadedProducts.value = productsInformation.products
                    _isLoading.value = false
                    _isLastPage.postValue(productsInformation.isLastPage)
                }.onError { code, message ->
                    // TODO: Error Handling
                }.onException {
                    // TODO: Exception Handling
                }
            }

            updateCartItemsCount()
            loadProductsHistory()
        }
    }

    fun loadNextPageProducts() {
        viewModelScope.launch {
            if (isLastPage.value == true) return@launch
            val nextPage = _currentPage.value?.plus(PAGE_MOVE_COUNT) ?: currentPageIsNullException()

            productsRepository.loadProducts(nextPage).onSuccess { productsInformation ->
                val oldProducts = loadedProducts.value ?: emptyList()
                _loadedProducts.value = oldProducts + productsInformation.products
                _isLastPage.postValue(productsInformation.isLastPage)
            }.onError { code, message ->
                // TODO: Error Handling
            }.onException {
                // TODO: Exception Handling
            }

            updateCartItemsCount()
            _currentPage.postValue(nextPage)
        }
    }

    fun navigateToShoppingCart() {
        _shoppingCartDestination.setValue(true)
    }

    private suspend fun loadProductsHistory() {
        val productHistory = productHistoryRepository.loadProductsHistory()
        _productsHistory.value = productHistory
    }

    private suspend fun updateCartItemsCount() {
        cartItemRepository.calculateCartItemsCount().onSuccess { totalCount ->
            _cartProductTotalCount.value = totalCount
        }.onError { code, message ->
            // TODO: Error Handling
        }.onException {
            // TODO: Exception Handling
        }
    }

    private fun updateQuantity(
        productIdsCount: ProductIdsCount,
        variation: Int,
    ) {
        viewModelScope.launch {
            cartItemRepository.updateProductQuantity(productIdsCount.productId, productIdsCount.quantity)
            cartItemRepository.calculateCartItemsCount().onSuccess { totalCount ->
                updateProductQuantity(productIdsCount.productId, variation, totalCount)
            }.onError { code, message ->
                Log.d("hye", "ServerError: $code - $message")
            }.onException {
                Log.d("hye", "Exception: $it")
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

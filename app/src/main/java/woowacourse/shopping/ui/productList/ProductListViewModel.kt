package woowacourse.shopping.ui.productList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.currentPageIsNullException
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.DefaultProductHistoryRepository
import woowacourse.shopping.domain.repository.DefaultShoppingCartRepository
import woowacourse.shopping.domain.repository.DefaultShoppingProductRepository
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.repository.ShoppingProductsRepository
import woowacourse.shopping.ui.OnItemQuantityChangeListener
import woowacourse.shopping.ui.OnProductItemClickListener
import woowacourse.shopping.ui.model.CartItem
import woowacourse.shopping.ui.productList.event.ProductListError
import woowacourse.shopping.ui.util.MutableSingleLiveData
import woowacourse.shopping.ui.util.SingleLiveData
import woowacourse.shopping.ui.util.UniversalViewModelFactory

class ProductListViewModel(
    private val productsRepository: ShoppingProductsRepository,
    private val productHistoryRepository: ProductHistoryRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private var _currentPage: MutableLiveData<Int> = MutableLiveData(FIRST_PAGE),
) : ViewModel(), OnProductItemClickListener, OnItemQuantityChangeListener {
    private val currentPage: LiveData<Int> get() = _currentPage

    private val _loadedProducts: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
    val loadedProducts: LiveData<List<Product>> get() = _loadedProducts

    private val _productsHistory: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
    val productsHistory: LiveData<List<Product>> get() = _productsHistory

    private val _cartProductTotalCount: MutableLiveData<Int> = MutableLiveData()
    val cartProductTotalCount: LiveData<Int> get() = _cartProductTotalCount

    private val _isLastPage: MutableLiveData<Boolean> = MutableLiveData()
    val isLastPage: LiveData<Boolean> get() = _isLastPage

    private val _detailProductDestinationId: MutableSingleLiveData<Long> = MutableSingleLiveData()
    val detailProductDestinationId: SingleLiveData<Long> get() = _detailProductDestinationId

    private val _shoppingCartDestination: MutableSingleLiveData<Boolean> = MutableSingleLiveData()
    val shoppingCartDestination: SingleLiveData<Boolean> get() = _shoppingCartDestination

    private val _cartProducts: MutableLiveData<List<CartItem>> = MutableLiveData()
    private val cartProducts: LiveData<List<CartItem>> get() = _cartProducts

    private val _error: MutableSingleLiveData<ProductListError> = MutableSingleLiveData()
    val error: SingleLiveData<ProductListError> get() = _error

    fun loadAll() {
        val page = currentPage.value ?: currentPageIsNullException()

        viewModelScope.launch {
            loadAllProducts(page)
            calculateCartProductTotalCount()
            calculateFinalPage(page)
            loadProductHistory()
            loadCartProducts()

        }
    }

    private suspend fun loadAllProducts(page: Int) {
        productsRepository.allProductsUntilPage(page)
            .onSuccess { products ->
                _loadedProducts.value = products
            }
            .onFailure {
                _error.setValue(ProductListError.LoadProducts)
            }

    }

    private suspend fun calculateCartProductTotalCount() {
        shoppingCartRepository.shoppingCartProductQuantity()
            .onSuccess { calculatedProductsQuantity ->
                _cartProductTotalCount.value = calculatedProductsQuantity
            }
            .onFailure {
                _error.setValue(ProductListError.CalculateCartProductTotalCount)
            }
    }

    private suspend fun calculateFinalPage(page: Int) {
        productsRepository.isFinalPage(page)
            .onSuccess {
                _isLastPage.value = it
            }
            .onFailure {
                _error.setValue(ProductListError.CalculateFinalPage)
            }
    }

    private suspend fun loadProductHistory() {
        productHistoryRepository.loadRecentProducts(10)
            .onSuccess {
                _productsHistory.value = it
            }
            .onFailure {
                _error.setValue(ProductListError.LoadProductHistory)
            }
    }

    private suspend fun loadCartProducts() {
        shoppingCartRepository.loadAllCartItems()
            .onSuccess { loadedCartItems ->
                _cartProducts.value = loadedCartItems
            }
            .onFailure {
                _error.setValue(ProductListError.LoadCartProducts)
            }
    }

    private suspend fun loadPagedProducts(page: Int) {
        productsRepository.pagedProducts(page)
            .onSuccess {
                _loadedProducts.value = loadedProducts.value.orEmpty().toMutableList().apply { addAll(it) }
            }
            .onFailure {
                _error.setValue(ProductListError.LoadProducts)
            }
    }

    fun loadNextPageProducts() {
        if (isLastPage.value == true) return
        val nextPage = _currentPage.value?.plus(PAGE_MOVE_COUNT) ?: currentPageIsNullException()
        viewModelScope.launch {
            calculateFinalPage(nextPage)
            loadPagedProducts(nextPage)

        }
        _currentPage.value = nextPage
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
            shoppingCartRepository.addShoppingCartProduct(productId, INCREASE_AMOUNT)
                .onSuccess {
                    loadCartProducts()
                    updateLoadedProduct(productId, INCREASE_AMOUNT)
                    updateProductsTotalCount()
                }
                .onFailure {
                    _error.setValue(ProductListError.AddShoppingCartProduct)
                }
        }
    }

    private fun findCartItemOrNull(productId: Long): CartItem? =
        cartProducts.value?.find { cartItem -> cartItem.product.id == productId }

    private fun updateLoadedProduct(
        productId: Long,
        changeAmount: Int,
    ) {
        _loadedProducts.value =
            loadedProducts.value.orEmpty().map { product ->
                if (product.id == productId) {
                    product.copy(quantity = product.quantity + changeAmount)
                } else {
                    product
                }
            }
    }

    override fun onDecrease(
        productId: Long,
        quantity: Int,
    ) {
        val foundCartItem = findCartItemOrNull(productId) ?: return
        viewModelScope.launch {
            shoppingCartRepository.updateProductQuantity(foundCartItem.id, quantity)
                .onSuccess {
                    loadCartProducts()
                    updateLoadedProduct(productId, DECREASE_AMOUNT)
                    updateProductsTotalCount()
                }
                .onFailure {
                    _error.setValue(ProductListError.UpdateProductQuantity)
                }
        }
    }

    private fun updateProductsTotalCount() {
        viewModelScope.launch {
            shoppingCartRepository.shoppingCartProductQuantity()
                .onSuccess {
                    _cartProductTotalCount.value = it
                }
                .onFailure {
                    _error.setValue(ProductListError.CalculateCartProductCount)
                }
        }
    }

    companion object {
        private const val TAG = "ProductListViewModel"
        private const val FIRST_PAGE = 1
        private const val PAGE_MOVE_COUNT = 1
        private const val INCREASE_AMOUNT = 1
        private const val DECREASE_AMOUNT = -1

        fun factory(
            productRepository: ShoppingProductsRepository =
                DefaultShoppingProductRepository(
                    ShoppingApp.productSource,
                    ShoppingApp.cartSource,
                ),
            historyRepository: ProductHistoryRepository =
                DefaultProductHistoryRepository(
                    ShoppingApp.historySource,
                    ShoppingApp.productSource,
                ),
            shoppingCartRepository: ShoppingCartRepository =
                DefaultShoppingCartRepository(
                    ShoppingApp.cartSource,
                ),
        ): UniversalViewModelFactory =
            UniversalViewModelFactory {
                ProductListViewModel(productRepository, historyRepository, shoppingCartRepository)
            }
    }
}

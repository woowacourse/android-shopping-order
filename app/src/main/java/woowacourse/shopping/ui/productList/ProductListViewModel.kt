package woowacourse.shopping.ui.productList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    fun loadAll() {
        val page = currentPage.value ?: currentPageIsNullException()

        loadAllProducts(page)
        calculateCartProductTotalCount()
        calculateFinalPage(page)
        loadProductHistory()
        loadCartProducts()
    }

    private fun loadAllProducts(page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.allProductsUntilPage2(page)
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        _loadedProducts.postValue(it)
                    }
                }
                .onFailure {
                    // TODO: 에러 처리
                    Log.e(TAG, "loadAllProducts: failure: $it")
                    throw it
                }
        }
    }

    private fun calculateCartProductTotalCount() {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingCartRepository.shoppingCartProductQuantity2()
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        _cartProductTotalCount.postValue(it)
                    }
                }
                .onFailure {
                    // TODO: 에러 처리
                    Log.e(TAG, "calculateCartProductTotalCount: failure: $it")
                    throw it
                }
        }
    }

    private fun calculateFinalPage(page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.isFinalPage2(page)
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        _isLastPage.postValue(it)
                    }
                }
                .onFailure {
                    // TODO: 에러 처리
                    Log.e(TAG, "calculateFinalPage: failure: $it")
                    throw it
                }
        }
    }

    private fun loadProductHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            productHistoryRepository.loadRecentProducts(10)
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        _productsHistory.postValue(it)
                    }
                }
                .onFailure {
                    // TODO: 에러 처리
                    Log.e(TAG, "loadProductHistory: failure: $it")
                    throw it
                }
        }
    }

    private fun loadCartProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingCartRepository.loadAllCartItems2()
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        _cartProducts.postValue(it)
                    }
                }
                .onFailure {
                    // TODO: 에러 처리
                    Log.e(TAG, "loadCarProducts: failure: $it")
                    throw it
                }
        }
    }

    private fun loadPagedProducts(page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.pagedProducts2(page)
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        _loadedProducts.postValue(_loadedProducts.value?.toMutableList()?.apply { addAll(it) } ?: it)
                    }
                }
                .onFailure {
                    // TODO 에러 처리
                    Log.e(TAG, "loadPagedProducts: failure: $it")
                    throw it
                }
        }
    }

    fun loadNextPageProducts() {
        if (isLastPage.value == true) return
        val nextPage = _currentPage.value?.plus(PAGE_MOVE_COUNT) ?: currentPageIsNullException()

        calculateFinalPage(nextPage)
        loadPagedProducts(nextPage)
        _currentPage.postValue(nextPage)
    }

    fun navigateToShoppingCart() {
        _shoppingCartDestination.setValue(true)
    }

    override fun onClick(productId: Long) {
        _detailProductDestinationId.setValue(productId)
    }

    override fun onIncrease(productId: Long, quantity: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingCartRepository.addShoppingCartProduct2(productId, quantity)
                .onSuccess {
                    loadCartProducts()
                    updateLoadedProduct(productId, INCREASE_AMOUNT)
                    updateProductsTotalCount()
                }
                .onFailure {
                    // TODO: 에러 처리
                    Log.e(TAG, "onIncrease2: failure: $it")
                    throw it
                }
        }
    }

    private fun updateCartItemQuantity(find: CartItem, productId: Long, changeAmount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingCartRepository.updateProductQuantity2(find.id, find.quantity + changeAmount)
                .onSuccess {
                    loadCartProducts()
                    updateLoadedProduct(productId, changeAmount)
                    updateProductsTotalCount()
                }
                .onFailure {
                    // TODO: 에러 처리
                    Log.e(TAG, "onIncrease2: failure: $it")
                    throw it
                }
        }
    }

    private fun findCartItemOrNull(productId: Long): CartItem? =
        cartProducts.value?.find { cartItem -> cartItem.product.id == productId }

    private fun updateLoadedProduct(
        productId: Long,
        changeAmount: Int,
    ) {
        _loadedProducts.postValue(
            loadedProducts.value?.map { product ->
                if (product.id == productId) {
                    product.copy(quantity = product.quantity + changeAmount)
                } else {
                    product
                }
            },
        )
    }

    override fun onDecrease(
        productId: Long,
        quantity: Int,
    ) {
        val find = findCartItemOrNull(productId) ?: return
        viewModelScope.launch(Dispatchers.IO) {
            shoppingCartRepository.updateProductQuantity2(find.id, quantity)
                .onSuccess {
                    loadCartProducts()
                    updateLoadedProduct(productId, DECREASE_AMOUNT)
                    updateProductsTotalCount()
                }
                .onFailure {
                    // TODO: 에러 처리
                    Log.e(TAG, "onIncrease2: failure: $it")
                    throw it
                }
        }
    }

    private fun updateProductsTotalCount() {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingCartRepository.shoppingCartProductQuantity2()
                .onSuccess {
                    _cartProductTotalCount.postValue(it)
                }
                .onFailure {
                    // TODO: 에러 처리
                    Log.e(TAG, "updateProductsTotalCount: failure: $it")
                    throw it
                }
        }
    }

    companion object {
        private const val TAG = "ProductListViewModel"
        private const val FIRST_PAGE = 1
        private const val PAGE_MOVE_COUNT = 1
        private const val INCREASE_AMOUNT = 1
        private const val DECREASE_AMOUNT = -1
        private const val FIRST_AMOUNT = 1

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

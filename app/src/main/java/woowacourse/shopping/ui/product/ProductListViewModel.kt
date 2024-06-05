package woowacourse.shopping.ui.product

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.MutableSingleLiveData
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.SingleLiveData
import woowacourse.shopping.UniversalViewModelFactory
import woowacourse.shopping.currentPageIsNullException
import woowacourse.shopping.data.cart.DefaultCartItemRepository
import woowacourse.shopping.data.history.DefaultProductHistoryRepository
import woowacourse.shopping.data.product.DefaultProductRepository
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.cart.CartItemRepository
import woowacourse.shopping.domain.repository.history.ProductHistoryRepository
import woowacourse.shopping.domain.repository.product.ProductRepository
import woowacourse.shopping.ui.OnItemQuantityChangeListener
import woowacourse.shopping.ui.OnProductItemClickListener
import kotlin.concurrent.thread

class ProductListViewModel(
    private val productsRepository: ProductRepository,
    private val productHistoryRepository: ProductHistoryRepository,
    private val cartItemRepository: CartItemRepository,
    private var _currentPage: MutableLiveData<Int> = MutableLiveData(FIRST_PAGE),
) : ViewModel(), OnProductItemClickListener, OnItemQuantityChangeListener {
    val currentPage: LiveData<Int> get() = _currentPage

    private val uiHandler = Handler(Looper.getMainLooper())

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
        thread {
            val page = currentPage.value ?: currentPageIsNullException()
            val result = (FIRST_PAGE..page).flatMap { productsRepository.loadAllProducts(it) }
            val totalCartCount = productsRepository.shoppingCartProductQuantity()
            val isLastPage = productsRepository.isFinalPage(page)
            val productHistory = productHistoryRepository.loadAllProductHistory()

            _isLastPage.postValue(isLastPage)
            uiHandler.post {
                _loadedProducts.value = result
                _cartProductTotalCount.value = totalCartCount
                _productsHistory.value = productHistory
                _isLoading.value = false
            }
        }
    }

    fun loadNextPageProducts() {
        thread {
            if (isLastPage.value == true) return@thread

            val nextPage = _currentPage.value?.plus(PAGE_MOVE_COUNT) ?: currentPageIsNullException()
            val isLastPage = productsRepository.isFinalPage(nextPage)
            val result = productsRepository.loadAllProducts(nextPage)
            val totalCount = productsRepository.shoppingCartProductQuantity()

            _currentPage.postValue(nextPage)
            _isLastPage.postValue(isLastPage)
            uiHandler.post {
                _loadedProducts.value = _loadedProducts.value?.toMutableList()?.apply { addAll(result) }
                _cartProductTotalCount.value = totalCount
            }
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
        thread {
            try {
                cartItemRepository.increaseCartProduct(productId, quantity)
            } catch (e: NoSuchElementException) {
                cartItemRepository.addCartItem(productId, quantity)
            } finally {
                val totalCount = productsRepository.shoppingCartProductQuantity()
                updateProductQuantity(productId, INCREASE_VARIATION, totalCount)
            }
        }
    }

    override fun onDecrease(
        productId: Long,
        quantity: Int,
    ) {
        thread {
            cartItemRepository.decreaseCartProduct(productId, quantity)
            val totalCount = productsRepository.shoppingCartProductQuantity()
            updateProductQuantity(productId, DECREASE_VARIATION, totalCount)
            return@thread
        }
    }

    private fun updateProductQuantity(productId: Long, variation: Int, totalCount: Int) {
        uiHandler.post {
            _loadedProducts.value =
                loadedProducts.value?.map { product ->
                    val quantity: Int = product.quantity + variation
                    product.takeIf { it.id == productId }?.copy(quantity = quantity) ?: product
                }
            _cartProductTotalCount.value = totalCount
        }
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
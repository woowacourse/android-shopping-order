package woowacourse.shopping.ui.productList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
import woowacourse.shopping.ui.model.CartItem2
import woowacourse.shopping.ui.util.MutableSingleLiveData
import woowacourse.shopping.ui.util.SingleLiveData
import woowacourse.shopping.ui.util.UniversalViewModelFactory
import kotlin.concurrent.thread

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

//    private val _cartProducts: MutableLiveData<List<CartItem>> = MutableLiveData()
//    private val cartProducts: LiveData<List<CartItem>> get() = _cartProducts

    private val _cartProducts: MutableLiveData<List<CartItem2>> = MutableLiveData()
    private val cartProducts: LiveData<List<CartItem2>> get() = _cartProducts

    fun loadAll() {
        val page = currentPage.value ?: currentPageIsNullException()

        thread {
            _loadedProducts.postValue(productsRepository.allProductsUntilPage(page))
            _cartProductTotalCount.postValue(shoppingCartRepository.shoppingCartProductQuantity())
            _isLastPage.postValue(productsRepository.isFinalPage(page))
            _productsHistory.postValue(productHistoryRepository.loadAllProductHistory())
//            _cartProducts.postValue(shoppingCartRepository.loadAllCartItems())
            _cartProducts.postValue(shoppingCartRepository.loadAllCartItems2())
        }.join()
    }

    fun loadNextPageProducts() {
        if (isLastPage.value == true) return

        val nextPage = _currentPage.value?.plus(PAGE_MOVE_COUNT) ?: currentPageIsNullException()

        thread {
            val isLastPage = productsRepository.isFinalPage(nextPage)
            val result = productsRepository.pagedProducts(nextPage)

            _currentPage.postValue(nextPage)
            _isLastPage.postValue(isLastPage)
            _loadedProducts.postValue(_loadedProducts.value?.toMutableList()?.apply { addAll(result) })
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
        try {
            val foundCartItem = foundCartItem(productId)
//            updateProductQuantity(foundCartItem, INCREASE_AMOUNT)
            updateProductQuantity2(foundCartItem, INCREASE_AMOUNT)
        } catch (e: NoSuchElementException) {
            addNewProduct(productId)
        } finally {
            thread {
//                _cartProducts.postValue(shoppingCartRepository.loadAllCartItems())
                _cartProducts.postValue(shoppingCartRepository.loadAllCartItems2())
            }.join()

            updateProductsTotalCount()

            updateLoadedProduct(productId, INCREASE_AMOUNT)
        }
    }

    private fun addNewProduct(productId: Long) {
        thread {
            shoppingCartRepository.addShoppingCartProduct(productId, FIRST_AMOUNT)
        }.join()
    }

    private fun foundCartItem(productId: Long) =
        (
            cartProducts.value?.find { cartItem -> cartItem.product.id == productId }
                ?: throw NoSuchElementException()
        )

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
        val find = foundCartItem(productId)
//        updateProductQuantity(find, DECREASE_AMOUNT)
        updateProductQuantity2(find, DECREASE_AMOUNT)

        thread {
//            _cartProducts.postValue(shoppingCartRepository.loadAllCartItems())
            _cartProducts.postValue(shoppingCartRepository.loadAllCartItems2())
        }.join()

        updateLoadedProduct(productId, DECREASE_AMOUNT)
        updateProductsTotalCount()
    }

    private fun updateProductQuantity(
        find: CartItem,
        changeAmount: Int,
    ) {
        thread {
            shoppingCartRepository.updateProductQuantity(find.id, find.quantity + changeAmount)
        }.join()
    }

    private fun updateProductQuantity2(
        find: CartItem2,
        changeAmount: Int,
    ) {
        thread {
            shoppingCartRepository.updateProductQuantity(find.id, find.quantity + changeAmount)
        }.join()
    }

    private fun updateProductsTotalCount() {
        thread {
            _cartProductTotalCount.postValue(shoppingCartRepository.shoppingCartProductQuantity())
        }.join()
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

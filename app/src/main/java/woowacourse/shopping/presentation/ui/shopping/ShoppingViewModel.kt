package woowacourse.shopping.presentation.ui.shopping

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.PageInfo
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.model.ShoppingProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.repository.ShoppingItemsRepository
import woowacourse.shopping.presentation.event.Event
import woowacourse.shopping.presentation.event.SingleLiveEvent

class ShoppingViewModel(
    val shoppingItemsRepository: ShoppingItemsRepository,
    val cartItemsRepository: CartRepository,
    val recentProductRepository: RecentProductRepository,
) : ViewModel(), ShoppingEventHandler, ShoppingItemCountHandler {
    private val _shoppingProducts: MutableLiveData<List<ShoppingProduct>> = MutableLiveData()
    val shoppingProducts: LiveData<List<ShoppingProduct>>
        get() = _shoppingProducts

    private val _pageInfo: MutableLiveData<PageInfo> = MutableLiveData(PageInfo(false, 0, 0))
    val pageInfo: LiveData<PageInfo>
        get() = _pageInfo

    private var nextPage = DEFAULT_PAGE

    private val _recentProducts: MutableLiveData<List<RecentProduct>> = MutableLiveData()
    val recentProducts: LiveData<List<RecentProduct>>
        get() = _recentProducts

    private val _changedIds: SingleLiveEvent<Set<Long>> = SingleLiveEvent()
    val changedIds: LiveData<Set<Long>>
        get() = _changedIds

    private val _totalCartItemsQuantity = MutableLiveData(DEFAULT_TOTAL_CART_ITEMS_QUANTITY)
    val totalCartItemsQuantity: LiveData<Int>
        get() = _totalCartItemsQuantity

    private val _navigateToDetail = MutableLiveData<Event<Long>>()
    val navigateToDetail: LiveData<Event<Long>>
        get() = _navigateToDetail

    private val _navigateToCart = MutableLiveData<Event<Boolean>>()
    val navigateToCart: LiveData<Event<Boolean>>
        get() = _navigateToCart

    private val _isLoading = MutableLiveData(Event(true))

    val isLoading: LiveData<Event<Boolean>>
        get() = _isLoading

    fun loadProducts() {
        val totalProducts: List<Product> = getProductListWithSettingPageInfo()
        loadCartItemsThenCombineWithProducts(totalProducts)
        updateTotalCartItemsQuantity()
    }

    private fun getProductListWithSettingPageInfo(): List<Product> {
        var products: List<Product> = shoppingProducts.value?.map { it.product }.orEmpty()
        val response = shoppingItemsRepository.fetchProductsWithPage(nextPage, PAGE_SIZE)
        response.onSuccess { productListInfo ->
            products = products + productListInfo.products
            _pageInfo.postValue(productListInfo.pageInfo)
            nextPage = productListInfo.pageInfo.currentPage + 1
        }.onFailure {
            _isLoading.postValue(Event(false))
            Log.d(this::class.java.simpleName, "$it")
        }
        return products
    }

    private fun loadCartItemsThenCombineWithProducts(products: List<Product>) {
        var cartItems: List<CartItem>?
        cartItemsRepository.fetchCartItemsInfo { result ->
            result.onSuccess { items ->
                cartItems = items
                combineProductsWithCartItems(products, cartItems)
            }.onFailure {
                combineProductsWithCartItems(products, emptyList())
                _isLoading.postValue(Event(false))
                Log.d(this::class.java.simpleName, "$it")
            }
        }
    }

    private fun combineProductsWithCartItems(
        products: List<Product>?,
        cartItems: List<CartItem>?,
    ) {
        if (products != null && cartItems != null) {
            _shoppingProducts.postValue(convertToShoppingProductList(products, cartItems))
            _changedIds.postValue(products.map { it.id }.toSet())
            _isLoading.postValue(Event(false))
        }
    }

    private fun updateTotalCartItemsQuantity() {
        cartItemsRepository.fetchTotalQuantity { result ->
            result.onSuccess { totalQuantity ->
                _totalCartItemsQuantity.postValue(totalQuantity)
            }.onFailure {
                Log.d(this::class.java.simpleName, "$it")
            }
        }
    }

    private fun convertToShoppingProductList(
        products: List<Product>,
        cartItems: List<CartItem>,
    ): List<ShoppingProduct> {
        return products.map { product ->
            val result = cartItems.find { it.productId == product.id }
            if (result != null) {
                ShoppingProduct(product, result.quantity)
            } else {
                ShoppingProduct(product)
            }
        }
    }

    fun setLoadingStart() {
        _isLoading.value = Event(true)
    }

    override fun onProductClick(productId: Long) {
        _navigateToDetail.value = Event(productId)
        val product = shoppingItemsRepository.findProductItem(productId) ?: return
        updateRecentProducts(product)
    }

    private fun updateRecentProducts(product: Product) {
        recentProductRepository.save(product)
        _recentProducts.value = recentProductRepository.loadLatestList()
    }

    override fun onLoadMoreButtonClick() {
        loadMoreProducts()
    }

    private fun loadMoreProducts() {
        loadProducts()
    }

    override fun onShoppingCartButtonClick() {
        _navigateToCart.value = Event(true)
    }

    override fun increaseCount(
        productId: Long,
        quantity: Int,
    ) {
        cartItemsRepository.addCartItem(productId, quantity.inc()) { result ->
            result.onSuccess {
                _shoppingProducts.postValue(getQuantityChangedList(productId, quantity.inc()))
                _totalCartItemsQuantity.postValue(totalCartItemsQuantity.value?.inc())
                _changedIds.postValue(setOf(productId))
            }.onFailure {
                Log.d(this::class.java.simpleName, "$it")
            }
        }
    }

    override fun decreaseCount(
        productId: Long,
        quantity: Int,
    ) {
        if (quantity > 1) {
            cartItemsRepository.updateCartItemQuantityWithProductId(
                productId,
                quantity.dec(),
            ) { result ->
                result.onSuccess {
                    _shoppingProducts.postValue(getQuantityChangedList(productId, quantity.dec()))
                    _totalCartItemsQuantity.postValue(totalCartItemsQuantity.value?.dec())
                    _changedIds.postValue(setOf(productId))
                }.onFailure {
                    Log.d(this::class.java.simpleName, "$it")
                }
            }
        } else {
            cartItemsRepository.deleteCartItemWithProductId(productId) { result ->
                result.onSuccess {
                    _shoppingProducts.postValue(getQuantityChangedList(productId, 0))
                    _totalCartItemsQuantity.postValue(totalCartItemsQuantity.value?.dec())
                    _changedIds.postValue(setOf(productId))
                }.onFailure {
                    Log.d(this::class.java.simpleName, "$it")
                }
            }
        }
    }

    private fun getQuantityChangedList(
        productId: Long,
        newQuantity: Int,
    ): List<ShoppingProduct> {
        return shoppingProducts.value?.map {
            if (it.product.id == productId) {
                it.copy(quantity = newQuantity)
            } else {
                it
            }
        }.orEmpty()
    }

    companion object {
        private const val DEFAULT_TOTAL_CART_ITEMS_QUANTITY = 0
        private const val DEFAULT_PAGE = 0
        private const val PAGE_SIZE = 10
    }
}

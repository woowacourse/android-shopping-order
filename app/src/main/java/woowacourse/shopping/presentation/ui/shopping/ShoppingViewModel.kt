package woowacourse.shopping.presentation.ui.shopping

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.model.ShoppingProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.repository.ShoppingItemsRepository
import woowacourse.shopping.presentation.event.Event
import woowacourse.shopping.presentation.state.UIState
import woowacourse.shopping.presentation.ui.counter.CounterHandler

class ShoppingViewModel(
    val shoppingItemsRepository: ShoppingItemsRepository,
    val cartItemsRepository: CartRepository,
    val recentProductRepository: RecentProductRepository,
) : ViewModel(), ShoppingEventHandler, CounterHandler {
    private val _products = MutableLiveData<List<Product>>()

    val products: LiveData<List<Product>>
        get() = _products

    private val _shoppingProducts: MutableLiveData<List<ShoppingProduct>> = MutableLiveData()
    val shoppingProducts: LiveData<List<ShoppingProduct>>
        get() = _shoppingProducts

    private val _recentProducts: MutableLiveData<List<RecentProduct>> = MutableLiveData()
    val recentProducts: LiveData<List<RecentProduct>>
        get() = _recentProducts

    private val _cartCount = MutableLiveData<Int>()
    val cartCount: LiveData<Int>
        get() = _cartCount

    private val _shoppingUiState = MutableLiveData<UIState<List<Product>>>(UIState.Empty)
    val shoppingUiState: LiveData<UIState<List<Product>>>
        get() = _shoppingUiState

    private val _navigateToDetail = MutableLiveData<Event<Long>>()
    val navigateToDetail: LiveData<Event<Long>>
        get() = _navigateToDetail

    private val _navigateToCart = MutableLiveData<Event<Boolean>>()
    val navigateToCart: LiveData<Event<Boolean>>
        get() = _navigateToCart

    private var numberOfProduct: Int = 0

    private val _showLoadMore = MutableLiveData<Boolean>(false)
    val showLoadMore: LiveData<Boolean> = _showLoadMore

    private var offset = 0

    init {
        initializeProducts()
        hideLoadMore()
    }

    private fun initializeProducts() {
        viewModelScope.launch {
            numberOfProduct = fetchNumberOfProduct()

            val nextOffSet = calculateNextOffset()
            Log.d("crong", "nextOffset: $nextOffSet")
            val initialProducts = loadProducts(nextOffSet)
            if (nextOffSet != initialProducts.size) throw IllegalStateException("Something went wrong, please try again..")
            offset = nextOffSet

            _products.value = (initialProducts)
            _shoppingProducts.value = (convertToShoppingProductList(initialProducts))
            Log.d("crong", "initializeProducts: ${_shoppingProducts.value}")
            _shoppingUiState.value = UIState.Success(products.value ?: emptyList())
            val recentProducts = recentProductRepository.loadLatestList()
            recentProducts.onSuccess {
                _recentProducts.postValue(it)
            }
        }
    }

    private suspend fun fetchNumberOfProduct(): Int {
        var loadedProducts = 0
        val transaction =
            viewModelScope.async {
                shoppingItemsRepository.fetchProductsSize()
            }
        transaction.await().onSuccess {
            loadedProducts = it
        }
        return loadedProducts
    }

    private suspend fun loadProducts(end: Int): List<Product> {
        var loadedProducts = emptyList<Product>()
        val transaction =
            viewModelScope.async {
                // Log.d("crong", "loadProducts: ${shoppingItemsRepository.fetchProductsWithIndex(end = end)}")
                shoppingItemsRepository.fetchProductsWithIndex(end = end)
            }
        transaction.await().onSuccess {
            loadedProducts = it
            Log.d("crong", "loadProducts: $it")
        }
        //
        // Log.d("crong", "loadProducts: $loadedProducts")
        return loadedProducts
    }

    fun loadNextProducts() {
        loadProducts()
        hideLoadMore()
    }

    fun reloadProducts() {
        viewModelScope.launch {
            cartItemsRepository.updateCartItems()
            _shoppingProducts.value = convertToShoppingProductList(loadProducts(end = offset))
            Log.d("crong", "reloadProducts: ${_shoppingProducts.value}")
            val totalCountOfCartItems = cartItemsRepository.sumOfQuantity()
            totalCountOfCartItems.onSuccess {
                _cartCount.value = it
            }
        }
    }

    private suspend fun loadProducts(
        start: Int,
        end: Int,
    ): List<Product> {
        var newProducts = emptyList<Product>()
        val transaction =
            viewModelScope.async {
                shoppingItemsRepository.fetchProductsWithIndex(start, end)
            }
        transaction.await().onSuccess {
            newProducts = it
        }
        return newProducts
    }

    private suspend fun convertToShoppingProductList(products: List<Product>): List<ShoppingProduct> {
        val transaction =
            viewModelScope.async {
                products.map { createShoppingProduct(it) }
            }
        return transaction.await()
    }

    private suspend fun createShoppingProduct(product: Product): ShoppingProduct {
        var shoppingProduct = ShoppingProduct(product = product, quantity = 0)
        val quantity =
            viewModelScope.async {
                cartItemsRepository.findQuantityWithProductId(product.id)
            }
        quantity.await().onSuccess {
            shoppingProduct = ShoppingProduct(product = product, quantity = it)
        }

        return shoppingProduct
    }

    fun updateItems() {
        viewModelScope.launch {
            val shoppingProducts = _shoppingProducts.value ?: return@launch
            val updatedShoppingProducts = shoppingProducts.map { createShoppingProduct(it.product) }
            _shoppingProducts.postValue(updatedShoppingProducts)
        }
    }

    suspend fun fetchQuantity(productId: Long): Int {
        var quantity = 0
        val fetchedData =
            viewModelScope.async {
                cartItemsRepository.findQuantityWithProductId(productId)
            }
        fetchedData.await().onSuccess {
            quantity = it
        }
        return quantity
    }

    private suspend fun getProducts(): List<Product> {
        val currentOffset = offset
        offset = calculateNextOffset()
        val transaction =
            viewModelScope.async {
                loadProducts(currentOffset, offset)
            }
        return transaction.await()
    }

    private fun calculateNextOffset(): Int = Integer.min(offset + PAGE_SIZE, numberOfProduct)

    private fun loadProducts() {
        viewModelScope.launch {
            val currentProducts = products.value
            val nextProducts = getProducts()

            if (currentProducts == null) return@launch
            _products.postValue(currentProducts + nextProducts)
            _shoppingProducts.postValue(convertToShoppingProductList(currentProducts + nextProducts))
        }
    }

    fun showLoadMoreByCondition() {
        if (offset != numberOfProduct) showLoadMore()
    }

    fun showLoadMore() {
        _showLoadMore.postValue(true)
    }

    fun hideLoadMore() {
        _showLoadMore.postValue(false)
    }

    override fun onProductClick(productId: Long) {
        _navigateToDetail.postValue(Event(productId))
        viewModelScope.launch {
            val product = shoppingItemsRepository.findProductItem(productId)
            product.onSuccess {
                updateRecentProducts(it!!)
            }
        }
    }

    private fun updateRecentProducts(product: Product) {
        viewModelScope.launch {
            recentProductRepository.save(product)
            val recentProducts = recentProductRepository.loadLatestList()
            // _recentProducts.value = recentProducts
            recentProducts.onSuccess {
                _recentProducts.postValue(it)
            }
        }
    }

    override fun updateCartCount() {
        viewModelScope.launch {
            val sumOfQuantity = cartItemsRepository.sumOfQuantity()
            sumOfQuantity.onSuccess {
                Log.d("crong", "updateCartCount: $it")
                _cartCount.value = it
            }
        }
    }

    override fun onLoadMoreButtonClick() {
        loadNextProducts()
    }

    override fun onShoppingCartButtonClick() {
        _navigateToCart.postValue(Event(true))
    }

    override fun increaseCount(productId: Long) {
        val shoppingProducts = _shoppingProducts.value?.map { it.copy() } ?: return
        val shoppingProduct = shoppingProducts.find { it.product.id == productId }
        shoppingProduct?.increase()
        viewModelScope.launch {
            val product = shoppingItemsRepository.findProductItem(productId)
            product.onSuccess {
                cartItemsRepository.insert(it!!, shoppingProduct?.quantity() ?: 1)
            }
        }
        _shoppingProducts.value = shoppingProducts
        updateCartCount()
        Log.d("crong", "increaseCount: ${cartCount.value}")
    }

    override fun decreaseCount(productId: Long) {
        val shoppingProducts = _shoppingProducts.value?.map { it.copy() } ?: return
        val shoppingProduct = shoppingProducts.find { it.product.id == productId }
        shoppingProduct?.decrease()
        val quantity = shoppingProduct?.quantity() ?: 0
        Log.d("crong", "decreaseCount: $quantity")

        viewModelScope.launch {
            if (quantity > 0) {
                cartItemsRepository.updateQuantityWithProductId(productId, quantity)
            } else {
                cartItemsRepository.deleteWithProductId(productId)
            }
        }
        _shoppingProducts.value = shoppingProducts
        // Log.d("crong", "decreaseCount: ${cartCount.value}")
        updateCartCount()
        Log.d("crong", "decreaseCount: ${cartCount.value}")
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}

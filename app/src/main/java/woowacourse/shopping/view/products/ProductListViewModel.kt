package woowacourse.shopping.view.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.ProductRepositoryImpl.Companion.DEFAULT_ITEM_SIZE
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentlyProduct
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentlyProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.exception.NoSuchDataException
import woowacourse.shopping.utils.livedata.MutableSingleLiveData
import woowacourse.shopping.utils.livedata.SingleLiveData
import woowacourse.shopping.view.cartcounter.OnClickCartItemCounter

class ProductListViewModel(
    private val productRepository: ProductRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val recentlyProductRepository: RecentlyProductRepository,
) : ViewModel(), OnClickCartItemCounter {
    private val _products: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
    val products: LiveData<List<Product>> get() = _products
    private val _cartItemCount: MutableLiveData<Int> = MutableLiveData(0)
    val cartItemCount: LiveData<Int> get() = _cartItemCount

    private val _recentlyProducts: MutableLiveData<List<RecentlyProduct>> =
        MutableLiveData(emptyList())
    val recentlyProducts: LiveData<List<RecentlyProduct>> get() = _recentlyProducts

    private val _productListEvent: MutableSingleLiveData<ProductListEvent.SuccessEvent> =
        MutableSingleLiveData()
    val productListEvent: SingleLiveData<ProductListEvent.SuccessEvent> get() = _productListEvent
    private val _loadingEvent: MutableSingleLiveData<ProductListEvent.LoadProductEvent> =
        MutableSingleLiveData()
    val loadingEvent: SingleLiveData<ProductListEvent.LoadProductEvent> get() = _loadingEvent
    private val _errorEvent: MutableSingleLiveData<ProductListEvent.ErrorEvent> =
        MutableSingleLiveData()
    val errorEvent: SingleLiveData<ProductListEvent.ErrorEvent> get() = _errorEvent

    init {
        updateTotalCartItemCount()
        loadPagingRecentlyProduct()
    }

    fun loadPagingProduct() {
        viewModelScope.launch {
            _loadingEvent.setValue(ProductListEvent.LoadProductEvent.Loading)
            val itemSize = products.value?.size ?: DEFAULT_ITEM_SIZE
            productRepository.loadPagingProducts(itemSize)
                .onSuccess { pagingData ->
                    _products.value = _products.value?.plus(pagingData)
                    _loadingEvent.setValue(ProductListEvent.LoadProductEvent.Success)
                    _productListEvent.setValue(ProductListEvent.LoadProductEvent.Success)
                }
                .onFailure { exception ->
                    handleException(exception)
                }
        }
    }

    fun loadPagingRecentlyProduct() {
        viewModelScope.launch {
            recentlyProductRepository.getRecentlyProductList()
                .onSuccess { pagingData ->
                    _recentlyProducts.value = pagingData
                }.onFailure { exception ->
                    handleException(exception)
                }
        }
    }

    private fun updateTotalCartItemCount() {
        viewModelScope.launch {
            shoppingCartRepository.getTotalCartItemCount()
                .onSuccess { totalItemCount ->
                    _cartItemCount.value = totalItemCount
                }.onFailure { exception ->
                    handleException(exception)
                }
        }
    }

    private fun handleException(exception: Throwable) {
        // TODO: 에러 핸들링
        when (exception) {
            is NoSuchDataException -> _errorEvent.setValue(ProductListEvent.LoadProductEvent.Fail)
            else -> _errorEvent.setValue(ProductListEvent.ErrorEvent.NotKnownError)
        }
    }

    fun updateProducts(items: Map<Long, Int>) {
        products.value?.forEach { product ->
            val count = items[product.id]
            if (count != null) {
                product.updateCartItemCount(count)
                _productListEvent.setValue(ProductListEvent.UpdateProductEvent.Success(product.id))
            }
        }
        updateTotalCartItemCount()
    }

    override fun clickIncrease(product: Product) {
        viewModelScope.launch {
            shoppingCartRepository.increaseCartItem(product)
                .onSuccess {
                    _productListEvent.setValue(ProductListEvent.UpdateProductEvent.Success(product.id))
                    updateTotalCartItemCount()
                }.onFailure {
                    handleException(it)
                }
        }
    }

    override fun clickDecrease(product: Product) {
        viewModelScope.launch {
            shoppingCartRepository.decreaseCartItem(product)
                .onSuccess {
                    _productListEvent.setValue(ProductListEvent.UpdateProductEvent.Success(product.id))
                    product.cartItemCounter.decrease()
                    updateTotalCartItemCount()
                }.onFailure {
                    handleException(it)
                }
        }
    }
}

package woowacourse.shopping.view.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.model.CartItemEntity.Companion.DEFAULT_CART_ITEM_COUNT
import woowacourse.shopping.data.repository.ProductRepositoryImpl.Companion.DEFAULT_ITEM_SIZE
import woowacourse.shopping.domain.model.CartItemCounter.Companion.DEFAULT_ITEM_COUNT
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentlyProduct
import woowacourse.shopping.domain.model.UpdateCartItemResult
import woowacourse.shopping.domain.model.UpdateCartItemType
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentlyProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.exception.NoSuchDataException
import woowacourse.shopping.utils.livedata.MutableSingleLiveData
import woowacourse.shopping.utils.livedata.SingleLiveData

class ProductListViewModel(
    private val productRepository: ProductRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val recentlyProductRepository: RecentlyProductRepository,
) : ViewModel() {
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

    fun loadPagingRecentlyProduct() {
        recentlyProductRepository.getRecentlyProductList()
            .onSuccess { pagingData ->
                _recentlyProducts.value = pagingData
            }
            .onFailure { _ ->
                _errorEvent.setValue(ProductListEvent.ErrorEvent.NotKnownError)
            }
    }

    fun increaseShoppingCart(product: Product) {
        updateCartItem(product, UpdateCartItemType.INCREASE)
    }

    fun decreaseShoppingCart(product: Product) {
        updateCartItem(product, UpdateCartItemType.DECREASE)
    }

    private fun updateCartItem(
        product: Product,
        updateCartItemType: UpdateCartItemType,
    ) {
        shoppingCartRepository.updateCartItem(product, updateCartItemType)
            .onSuccess { updateCartItemResult ->
                handleCartItemUpdate(updateCartItemResult, product, updateCartItemType)
            }
            .onFailure { exception ->
                handleException(exception)
            }
    }

    private fun handleCartItemUpdate(
        updateCartItemResult: UpdateCartItemResult,
        product: Product,
        updateCartItemType: UpdateCartItemType,
    ) {
        when (updateCartItemResult) {
            UpdateCartItemResult.ADD -> addCartItem(product)
            is UpdateCartItemResult.DELETE -> deleteCartItem(product)
            is UpdateCartItemResult.UPDATED -> {
                product.updateCartItemCount(updateCartItemResult.cartItemResult.counter.itemCount)
                when (updateCartItemType) {
                    UpdateCartItemType.DECREASE -> updateTotalCartItemCount()
                    UpdateCartItemType.INCREASE -> {
                        product.updateItemSelector(true)
                        updateTotalCartItemCount()
                    }

                    is UpdateCartItemType.UPDATE -> {}
                }
                _productListEvent.setValue(ProductListEvent.UpdateProductEvent.Success(product.id))
            }
        }
    }

    private fun addCartItem(product: Product) {
        product.updateCartItemCount(DEFAULT_CART_ITEM_COUNT)
        product.updateItemSelector(true)
        updateTotalCartItemCount()
        _productListEvent.setValue(ProductListEvent.UpdateProductEvent.Success(product.id))
    }

    private fun deleteCartItem(product: Product) {
        product.updateItemSelector(false)
        updateTotalCartItemCount()
        _productListEvent.setValue(ProductListEvent.DeleteProductEvent.Success(product.id))
    }

    private fun updateTotalCartItemCount() {
        shoppingCartRepository.getTotalCartItemCount()
            .onSuccess { totalItemCount ->
                _cartItemCount.value = totalItemCount
            }
            .onFailure { _ ->
                _errorEvent.setValue(ProductListEvent.ErrorEvent.NotKnownError)
            }
    }

    private fun handleException(exception: Throwable) {
        when (exception) {
            is NoSuchDataException -> _errorEvent.setValue(ProductListEvent.LoadProductEvent.Fail)
            else -> _errorEvent.setValue(ProductListEvent.ErrorEvent.NotKnownError)
        }
    }

    fun updateProducts(items: Map<Long, Int>) {
        products.value?.forEach { product ->
            val count = items[product.id]
            if (count != null) {
                if (count == DEFAULT_ITEM_COUNT) {
                    product.updateItemSelector(false)
                } else {
                    product.updateItemSelector(true)
                }
                product.updateCartItemCount(count)
                _productListEvent.setValue(ProductListEvent.UpdateProductEvent.Success(product.id))
            }
        }
        updateTotalCartItemCount()
    }
}

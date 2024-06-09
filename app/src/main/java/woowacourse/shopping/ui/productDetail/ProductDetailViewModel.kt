package woowacourse.shopping.ui.productDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.common.MutableSingleLiveData
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.common.SingleLiveData
import woowacourse.shopping.common.UniversalViewModelFactory
import woowacourse.shopping.data.cart.DefaultCartItemRepository
import woowacourse.shopping.data.history.DefaultProductHistoryRepository
import woowacourse.shopping.data.product.DefaultProductRepository
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.cart.CartItemRepository
import woowacourse.shopping.domain.repository.history.ProductHistoryRepository
import woowacourse.shopping.domain.repository.product.ProductRepository
import woowacourse.shopping.common.OnItemQuantityChangeListener
import woowacourse.shopping.common.OnProductItemClickListener
import woowacourse.shopping.data.common.ResponseHandlingUtils.onServerError
import woowacourse.shopping.data.common.ResponseHandlingUtils.onException
import woowacourse.shopping.data.common.ResponseHandlingUtils.onSuccess

class ProductDetailViewModel(
    private val productId: Long,
    private val shoppingProductsRepository: ProductRepository,
    private val productHistoryRepository: ProductHistoryRepository,
    private val cartItemRepository: CartItemRepository,
) : ViewModel(), OnItemQuantityChangeListener, OnProductItemClickListener {
    private val _currentProduct: MutableLiveData<Product> = MutableLiveData()
    val currentProduct: LiveData<Product> get() = _currentProduct

    private val _productCount: MutableLiveData<Int> = MutableLiveData(1)
    val productCount: LiveData<Int> get() = _productCount

    private val _latestProduct: MutableLiveData<Product> = MutableLiveData()
    val latestProduct: LiveData<Product> get() = _latestProduct

    private var _detailProductDestinationId: MutableSingleLiveData<Long> = MutableSingleLiveData()
    val detailProductDestinationId: SingleLiveData<Long> get() = _detailProductDestinationId

    private var _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun loadAll() {
        viewModelScope.launch {
            shoppingProductsRepository.loadProduct(id = productId).onSuccess { product ->
                _currentProduct.value = product
                _productCount.value = product.quantity
                _isLoading.value = false
            }.onServerError { code, message ->
                // TODO: Error Handling
            }.onException {
                // TODO: Exception Handling
            }

            val latestProduct =
                try {
                    productHistoryRepository.loadLatestProduct()
                } catch (e: NoSuchElementException) {
                    Product.NULL
                }
            _latestProduct.value = latestProduct
            productHistoryRepository.saveProductHistory(productId)
        }
    }

    fun addProductToCart() {
        val productCount = productCount.value ?: return
        viewModelScope.launch {
            cartItemRepository.updateProductQuantity(productId, productCount)
        }
    }

    override fun onIncrease(
        productId: Long,
        quantity: Int,
    ) {
        _productCount.value = _productCount.value?.plus(1)
    }

    override fun onDecrease(
        productId: Long,
        quantity: Int,
    ) {
        if (productCount.value == MINIMUM_QUANTITY) return
        _productCount.value = _productCount.value?.minus(1)
    }

    override fun onClick(productId: Long) {
        _detailProductDestinationId.setValue(productId)
    }

    companion object {
        private const val TAG = "ProductDetailViewModel"
        private const val MINIMUM_QUANTITY = 1

        fun factory(
            productId: Long,
            shoppingProductsRepository: ProductRepository =
                DefaultProductRepository(
                    productDataSource = ShoppingApp.productSource,
                    cartItemDataSource = ShoppingApp.cartSource,
                ),
            productHistoryRepository: ProductHistoryRepository =
                DefaultProductHistoryRepository(
                    productHistoryDataSource = ShoppingApp.historySource,
                    productDataSource = ShoppingApp.productSource,
                ),
            cartItemRepository: CartItemRepository =
                DefaultCartItemRepository(
                    cartItemDataSource = ShoppingApp.cartSource,
                ),
        ): UniversalViewModelFactory {
            return UniversalViewModelFactory {
                ProductDetailViewModel(
                    productId = productId,
                    shoppingProductsRepository = shoppingProductsRepository,
                    productHistoryRepository = productHistoryRepository,
                    cartItemRepository = cartItemRepository,
                )
            }
        }
    }
}

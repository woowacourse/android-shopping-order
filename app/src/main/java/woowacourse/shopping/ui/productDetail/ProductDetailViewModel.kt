package woowacourse.shopping.ui.productDetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.DefaultProductHistoryRepository
import woowacourse.shopping.domain.repository.DefaultShoppingCartRepository
import woowacourse.shopping.domain.repository.DefaultShoppingProductRepository
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.repository.ShoppingProductsRepository
import woowacourse.shopping.ui.productDetail.event.ProductDetailError
import woowacourse.shopping.ui.productDetail.event.ProductDetailEvent
import woowacourse.shopping.ui.util.MutableSingleLiveData
import woowacourse.shopping.ui.util.SingleLiveData
import woowacourse.shopping.ui.util.UniversalViewModelFactory

class ProductDetailViewModel(
    private val productId: Long,
    private val shoppingProductsRepository: ShoppingProductsRepository,
    private val productHistoryRepository: ProductHistoryRepository,
    private val cartRepository: ShoppingCartRepository,
) : ViewModel(), ProductDetailListener {
    private val _currentProduct: MutableLiveData<Product> = MutableLiveData()
    val currentProduct: LiveData<Product> get() = _currentProduct

    private val _productCount: MutableLiveData<Int> = MutableLiveData(FIRST_AMOUNT)
    val productCount: LiveData<Int> get() = _productCount

    private val _latestProduct: MutableLiveData<Product> = MutableLiveData()
    val latestProduct: LiveData<Product> get() = _latestProduct


    private var _event: MutableSingleLiveData<ProductDetailEvent> = MutableSingleLiveData()
    val event: SingleLiveData<ProductDetailEvent> get() = _event

    private var _error: MutableSingleLiveData<ProductDetailError> = MutableSingleLiveData()
    val error: SingleLiveData<ProductDetailError> get() = _error

    fun loadAll() {
        loadProduct()
        loadLatestProduct()
        saveProductHistory()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            shoppingProductsRepository.loadProduct(id = productId)
                .onSuccess { loadedProduct ->
                    _currentProduct.value = loadedProduct
                }
                .onFailure {
                    _error.setValue(ProductDetailError.LoadProduct)
                }
        }

    }

    private fun loadLatestProduct() {
        viewModelScope.launch {
            productHistoryRepository.loadLatestProduct()
                .onSuccess { loadedLatestProduct ->
                    _latestProduct.value = loadedLatestProduct
                }
                .onFailure {
                    _error.setValue(ProductDetailError.LoadLatestProduct)
                }
        }
    }

    private fun saveProductHistory() {
        viewModelScope.launch {
            productHistoryRepository.saveProductHistory(productId)
                .onSuccess {
                    _event.setValue(ProductDetailEvent.SaveProductInHistory)
                }
                .onFailure {
                    _error.setValue(ProductDetailError.SaveProductInHistory)
                }
        }
    }

    fun addProductToCart() {
        val productCount = productCount.value ?: return
        viewModelScope.launch {
            cartRepository.addShoppingCartProduct(productId, productCount)
                .onSuccess {
                    _event.setValue(ProductDetailEvent.AddProductToCart)
                }
                .onFailure {
                    _error.setValue(ProductDetailError.AddProductToCart)
                }
        }
    }

    fun onFinish() {
        _event.setValue(ProductDetailEvent.Finish)
    }

    override fun onIncrease(
        productId: Long,
        quantity: Int,
    ) {
        _productCount.value = _productCount.value?.plus(CHANGE_AMOUNT)
    }

    override fun onDecrease(
        productId: Long,
        quantity: Int,
    ) {
        if (productCount.value == FIRST_AMOUNT) {
            return
        }
        _productCount.value = _productCount.value?.minus(CHANGE_AMOUNT)
    }

    override fun navigateToProductDetail(productId: Long) {
        _event.setValue(ProductDetailEvent.NavigateToProductDetail(productId))
    }

    companion object {
        private const val TAG = "ProductDetailViewModel"

        private const val FIRST_AMOUNT = 1
        private const val CHANGE_AMOUNT = 1

        fun factory(
            productId: Long,
            shoppingProductsRepository: ShoppingProductsRepository =
                DefaultShoppingProductRepository(
                    productsSource = ShoppingApp.productSource,
                    cartSource = ShoppingApp.cartSource,
                ),
            historyRepository: ProductHistoryRepository =
                DefaultProductHistoryRepository(
                    productHistoryDataSource = ShoppingApp.historySource,
                    productDataSource = ShoppingApp.productSource,
                ),
            cartRepository: ShoppingCartRepository =
                DefaultShoppingCartRepository(
                    cartSource = ShoppingApp.cartSource,
                ),
        ): UniversalViewModelFactory {
            return UniversalViewModelFactory {
                ProductDetailViewModel(
                    productId = productId,
                    shoppingProductsRepository = shoppingProductsRepository,
                    productHistoryRepository = historyRepository,
                    cartRepository = cartRepository,
                )
            }
        }
    }
}

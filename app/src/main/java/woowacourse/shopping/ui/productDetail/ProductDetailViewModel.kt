package woowacourse.shopping.ui.productDetail

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.MutableSingleLiveData
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.SingleLiveData
import woowacourse.shopping.UniversalViewModelFactory
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.cart.CartItemRepository
import woowacourse.shopping.domain.repository.cart.DefaultCartItemRepository
import woowacourse.shopping.domain.repository.history.DefaultProductHistoryRepository
import woowacourse.shopping.domain.repository.history.ProductHistoryRepository
import woowacourse.shopping.domain.repository.product.DefaultProductRepository
import woowacourse.shopping.domain.repository.product.ProductRepository
import woowacourse.shopping.ui.OnItemQuantityChangeListener
import woowacourse.shopping.ui.OnProductItemClickListener
import kotlin.concurrent.thread

class ProductDetailViewModel(
    private val productId: Long,
    private val shoppingProductsRepository: ProductRepository,
    private val productHistoryRepository: ProductHistoryRepository,
    private val cartItemRepository: CartItemRepository,
) : ViewModel(), OnItemQuantityChangeListener, OnProductItemClickListener {
    private val uiHandler = Handler(Looper.getMainLooper())

    private val _currentProduct: MutableLiveData<Product> = MutableLiveData()
    val currentProduct: LiveData<Product> get() = _currentProduct

    private val _productCount: MutableLiveData<Int> = MutableLiveData(1)
    val productCount: LiveData<Int> get() = _productCount

    private val _latestProduct: MutableLiveData<Product> = MutableLiveData()
    val latestProduct: LiveData<Product> get() = _latestProduct

    private var _detailProductDestinationId: MutableSingleLiveData<Long> = MutableSingleLiveData()
    val detailProductDestinationId: SingleLiveData<Long> get() = _detailProductDestinationId

    fun loadAll() {
        thread {
            val currentProduct = shoppingProductsRepository.loadProduct(id = productId)
            val latestProduct =
                try {
                    productHistoryRepository.loadLatestProduct()
                } catch (e: NoSuchElementException) {
                    Product.NULL
                }

            uiHandler.post {
                _currentProduct.value = currentProduct
                _productCount.value = 1
                _latestProduct.value = latestProduct
            }

            productHistoryRepository.saveProductHistory(productId)
        }
    }

    fun addProductToCart() {
        val productCount = productCount.value ?: return
        thread {
            cartItemRepository.addCartItem(productId, productCount)
        }
    }

    override fun onIncrease(
        productId: Long,
        quantity: Int,
    ) {
        _productCount.value = _productCount.value?.plus(1)
        Log.d(TAG, "onIncrease: productCount: ${productCount.value}")
    }

    override fun onDecrease(
        productId: Long,
        quantity: Int,
    ) {
        val currentProductCount = _productCount.value
        if (currentProductCount == 1) {
            return
        }
        _productCount.value = _productCount.value?.minus(1)
    }

    override fun onClick(productId: Long) {
        _detailProductDestinationId.setValue(productId)
    }

    companion object {
        private const val TAG = "ProductDetailViewModel"

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

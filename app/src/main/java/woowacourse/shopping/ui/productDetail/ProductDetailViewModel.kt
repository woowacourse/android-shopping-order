package woowacourse.shopping.ui.productDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.DefaultProductHistoryRepository
import woowacourse.shopping.domain.repository.DefaultShoppingCartRepository
import woowacourse.shopping.domain.repository.DefaultShoppingProductRepository
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.repository.ShoppingProductsRepository
import woowacourse.shopping.ui.util.MutableSingleLiveData
import woowacourse.shopping.ui.util.SingleLiveData
import woowacourse.shopping.ui.util.UniversalViewModelFactory
import kotlin.concurrent.thread

class ProductDetailViewModel(
    private val productId: Long,
    private val shoppingProductsRepository: ShoppingProductsRepository,
    private val productHistoryRepository: ProductHistoryRepository,
    private val cartRepository: ShoppingCartRepository,
) : ViewModel(), ProductDetailListener {
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
            val latestProduct = try {
                productHistoryRepository.loadLatestProduct()
            } catch (e: NoSuchElementException) {
                Product.NULL
            }

            productHistoryRepository.saveProductHistory(productId)

            _currentProduct.postValue(currentProduct)
            _productCount.postValue(FIRST_AMOUNT)
            _latestProduct.postValue(latestProduct)
        }
    }

    fun addProductToCart() {
        val productCount = productCount.value ?: return
        thread {
            cartRepository.addShoppingCartProduct(productId, productCount)
        }
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
        _detailProductDestinationId.setValue(productId)
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
                )
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

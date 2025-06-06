package woowacourse.shopping.presentation.productdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.presentation.ResultState
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.cart.CartCounterClickListener

class ProductDetailViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel(),
    CartCounterClickListener {
    private val _product: MutableLiveData<Product> = MutableLiveData()
    val product: LiveData<Product> = _product
    private val _productCount: MutableLiveData<Int> = MutableLiveData(1)
    val productCount: LiveData<Int> = _productCount
    private val _recentProduct: MutableLiveData<Product?> = MutableLiveData()
    val recentProduct: LiveData<Product?> = _recentProduct
    private val _isRecentProduct: MutableLiveData<Boolean> = MutableLiveData()
    val isRecentProduct: LiveData<Boolean> = _isRecentProduct
    private val _insertProductResult: MutableLiveData<ResultState<Unit>> = MutableLiveData()
    val insertProductResult: LiveData<ResultState<Unit>> = _insertProductResult
    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    fun fetchData(productId: Long) {
        productRepository.fetchProductById(productId) { result ->
            result
                .onSuccess { product ->
                    _product.postValue(product)
                    handleRecentProduct(productId)
                    insertCurrentProductToRecent(product)
                }.onFailure {
                    _toastMessage.postValue(R.string.product_detail_toast_load_fail)
                }
        }
    }

    fun addToCart() {
        val product: Product = product.value ?: return
        val productCount: Int = productCount.value ?: return
        viewModelScope.launch {
            cartRepository
                .insertOrUpdate(product, productCount)
                .onSuccess {
                    _toastMessage.value = R.string.product_detail_add_cart_toast_insert_success
                }.onFailure {
                    _toastMessage.value = R.string.product_detail_add_cart_toast_insert_fail
                }
        }
    }

    override fun onClickMinus(id: Long) {
        val currentCount = _productCount.value ?: return
        if (currentCount == 1) {
            _toastMessage.value = R.string.product_detail_toast_invalid_quantity
            return
        }
        _productCount.value = currentCount - 1
    }

    override fun onClickPlus(id: Long) {
        val currentCount = _productCount.value ?: return
        _productCount.value = currentCount + 1
    }

    private fun insertCurrentProductToRecent(product: Product) {
        viewModelScope.launch {
            val result = recentProductRepository.insertRecentProduct(product)
            result.onFailure { _toastMessage.postValue(R.string.product_detail_toast_most_recent_insert_fail) }
        }
    }

    private fun handleRecentProduct(currentProductId: Long) {
        viewModelScope.launch {
            val recentProduct = recentProductRepository.getMostRecentProduct()
            recentProduct
                .onSuccess {
                    _recentProduct.postValue(it)

                    val isSame = (it == null || it.productId == currentProductId)
                    _isRecentProduct.postValue(isSame)
                }.onFailure {
                    _toastMessage.postValue(R.string.product_detail_toast_most_recent_load_fail)
                }
        }
    }
}

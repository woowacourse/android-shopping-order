package woowacourse.shopping.ui.detail.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.ApiResponse
import woowacourse.shopping.data.repository.Error
import woowacourse.shopping.data.repository.onError
import woowacourse.shopping.data.repository.onException
import woowacourse.shopping.data.repository.onSuccess
import woowacourse.shopping.data.repository.toErrorNothing
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductWithQuantity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.CountButtonClickListener
import woowacourse.shopping.ui.utils.Event
import woowacourse.shopping.ui.utils.MutableSingleLiveData
import woowacourse.shopping.ui.utils.SingleLiveData

class ProductDetailViewModel(
    private val productId: Long,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel(), CountButtonClickListener {
    private val _error: MutableSingleLiveData<Error<Nothing>> = MutableSingleLiveData()
    val error: SingleLiveData<Error<Nothing>> get() = _error

    private val _errorMsg: MutableLiveData<Event<String>> = MutableLiveData(Event(""))
    val errorMsg: LiveData<Event<String>> get() = _errorMsg

    private val _productWithQuantity: MutableLiveData<ProductWithQuantity> = MutableLiveData()
    val productWithQuantity: LiveData<ProductWithQuantity> get() = _productWithQuantity

    val isInvalidCount: LiveData<Boolean> =
        _productWithQuantity.map {
            it.quantity.value == 0
        }

    private val _mostRecentProduct: MutableLiveData<Product> = MutableLiveData()
    val mostRecentProduct: LiveData<Product> get() = _mostRecentProduct

    private val _mostRecentProductVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val mostRecentProductVisibility: MutableLiveData<Boolean> get() = _mostRecentProductVisibility

    private val _addCartComplete = MutableSingleLiveData<Unit>()
    val addCartComplete: SingleLiveData<Unit> get() = _addCartComplete

    init {
        loadProduct()
    }

    fun loadProduct() {
        viewModelScope.launch {
            productRepository.find(productId).onSuccess {
                _productWithQuantity.value = ProductWithQuantity(product = it)
            }.onError {
                _error.setValue(it)
            }.onException {
                Log.d(this.javaClass.simpleName, "${it.e}")
            }
        }
    }

    override fun plusCount(productId: Long) {
        _productWithQuantity.value?.let {
            _productWithQuantity.value = it.inc()
        }
    }

    override fun minusCount(productId: Long) {
        _productWithQuantity.value?.let {
            _productWithQuantity.value = it.dec()
        }
    }

    fun addProductToCart() {
        viewModelScope.launch {
            _productWithQuantity.value?.let { productWithQuantity ->
                with(productWithQuantity) {
                    cartRepository.addProductToCart(this.product.id, this.quantity.value)
                }
                loadProduct()
            }
            _addCartComplete.setValue(Unit)
        }
    }

    fun addToRecentProduct(lastSeenProductState: Boolean) {
        viewModelScope.launch {
            loadMostRecentProduct(productId, lastSeenProductState)
            recentProductRepository.insert(productId)
        }
    }

    private fun loadMostRecentProduct(
        productId: Long,
        lastSeenProductState: Boolean,
    ) {
        viewModelScope.launch {
            recentProductRepository.findMostRecentProduct()?.let {
                runCatching {
                    productByIdOrNull(it.productId)
                }.onSuccess {
                    if (!lastSeenProductState || it == null) return@launch
                    _mostRecentProduct.value = it
                    setMostRecentVisibility(it.id, productId)
                }.onFailure {
                    _mostRecentProductVisibility.value = false
                    _error.setValue(Error.Unknown(it.message))
                }
            }
        }
    }

    private suspend fun productByIdOrNull(productId: Long): Product? {
        val response = viewModelScope.async { productRepository.find(productId) }.await()
        return when (response) {
            is ApiResponse.Success -> response.data
            is Error -> {
                _error.setValue(response.toErrorNothing())
                null
            }

            is ApiResponse.Exception -> {
                Log.d(this.javaClass.simpleName, "${response.e}")
                null
            }
        }
    }

    private fun setMostRecentVisibility(
        mostRecentProductId: Long,
        currentProductId: Long,
    ) {
        _mostRecentProductVisibility.value = (mostRecentProductId != currentProductId)
    }
}

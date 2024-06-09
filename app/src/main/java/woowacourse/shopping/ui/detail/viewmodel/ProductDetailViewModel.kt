package woowacourse.shopping.ui.detail.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductWithQuantity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.result.Fail
import woowacourse.shopping.domain.result.Response
import woowacourse.shopping.domain.result.onException
import woowacourse.shopping.domain.result.onFail
import woowacourse.shopping.domain.result.onSuccess
import woowacourse.shopping.ui.CountButtonClickListener
import woowacourse.shopping.ui.detail.uimodel.ProductDetailError
import woowacourse.shopping.ui.utils.MutableSingleLiveData
import woowacourse.shopping.ui.utils.SingleLiveData
import woowacourse.shopping.ui.utils.viewModelLaunch

class ProductDetailViewModel(
    private val productId: Long,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel(), CountButtonClickListener {
    private val _error: MutableSingleLiveData<ProductDetailError> = MutableSingleLiveData()
    val error: SingleLiveData<ProductDetailError> get() = _error

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

    private fun currentCount(): ProductWithQuantity =
        _productWithQuantity.value ?: error("상품 데이터를 초기화 해주세요")

    init {
        loadProduct()
    }

    fun loadProduct() {
        viewModelLaunch(::productExceptionHandler) {
            productRepository.productByIdResponse(productId).onSuccess {
                _productWithQuantity.value = ProductWithQuantity(product = it)
            }.checkError { _error.setValue(it) }
        }
    }

    override fun plusCount(productId: Long) {
        _productWithQuantity.value = currentCount().inc()
    }

    override fun minusCount(productId: Long) {
        _productWithQuantity.value = currentCount().dec()
    }

    fun addProductToCart() {
        viewModelLaunch(::addCartExceptionHandler) {
            cartRepository.addProductToCart(
                currentCount().product.id,
                currentCount().quantity.value
            ).onSuccess {
                loadProduct()
                _addCartComplete.setValue(Unit)
            }.checkError {
                _error.setValue(it)
            }
        }
    }

    fun addToRecentProduct(lastSeenProductState: Boolean) {
        viewModelLaunch(::recentExceptionHandler) {
            loadMostRecentProduct(productId, lastSeenProductState)
        }

        viewModelLaunch {
            recentProductRepository.insert(productId)
        }

    }

    private fun loadMostRecentProduct(
        productId: Long,
        lastSeenProductState: Boolean,
    ) {
        viewModelLaunch(::recentExceptionHandler) {
            recentProductRepository.mostRecentProductResponse().onSuccess {
                if (!lastSeenProductState) {
                    _mostRecentProductVisibility.value = false
                } else {
                    _mostRecentProduct.value = productRepository.productById(productId)
                    setMostRecentVisibility(it.id, productId)
                }
            }.onFail {
                if (it is Fail.NotFound) {
                    _mostRecentProductVisibility.value = false
                } else {
                    _error.setValue(it.toUiError())
                }
            }.onException {
                _error.setValue(ProductDetailError.UnKnown)
            }
        }
    }

    private fun setMostRecentVisibility(
        mostRecentProductId: Long,
        currentProductId: Long,
    ) {
        _mostRecentProductVisibility.value = (mostRecentProductId != currentProductId)
    }

    private fun productExceptionHandler(throwable: Throwable) {
        _error.setValue(ProductDetailError.LoadProduct)
    }

    private fun addCartExceptionHandler(throwable: Throwable) {
        _error.setValue(ProductDetailError.AddCart)
    }

    private fun recentExceptionHandler(throwable: Throwable) {
        _mostRecentProductVisibility.value = false
    }

    private inline fun <reified T : Any?> Response<T>.checkError(execute: (ProductDetailError) -> Unit) = apply {
        when (this) {
            is Response.Success -> {}
            is Fail.InvalidAuthorized -> execute(ProductDetailError.InvalidAuthorized)
            is Fail.Network -> execute(ProductDetailError.Network)
            is Fail.NotFound -> {
                when (T::class) {
                    Product::class -> execute(ProductDetailError.LoadProduct)
                    CartWithProduct::class -> execute(ProductDetailError.AddCart)
                    else -> execute(ProductDetailError.UnKnown)
                }
            }

            is Response.Exception -> {
                Log.d(this.javaClass.simpleName, "${this.e}")
                execute(ProductDetailError.UnKnown)
            }
        }
    }

    private inline fun <reified T : Any?> Fail<T>.toUiError() =
        when (this) {
            is Fail.InvalidAuthorized -> ProductDetailError.InvalidAuthorized
            is Fail.Network -> ProductDetailError.Network
            is Fail.NotFound -> {
                when (T::class) {
                    Product::class -> ProductDetailError.LoadProduct
                    else -> ProductDetailError.UnKnown
                }
            }
        }

}

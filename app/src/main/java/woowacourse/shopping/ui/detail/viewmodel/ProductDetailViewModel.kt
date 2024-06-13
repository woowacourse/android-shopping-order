package woowacourse.shopping.ui.detail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductWithQuantity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.ShowError
import woowacourse.shopping.domain.result.getOrThrow
import woowacourse.shopping.domain.result.onError
import woowacourse.shopping.domain.result.onSuccess
import woowacourse.shopping.ui.CountButtonClickListener
import woowacourse.shopping.ui.detail.uimodel.ProductDetailError
import woowacourse.shopping.ui.utils.BaseViewModel
import woowacourse.shopping.ui.utils.MutableSingleLiveData
import woowacourse.shopping.ui.utils.SingleLiveData

class ProductDetailViewModel(
    private val productId: Long,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) : BaseViewModel(), CountButtonClickListener {
    private val _errorScope: MutableSingleLiveData<ProductDetailError> = MutableSingleLiveData()
    val errorScope: SingleLiveData<ProductDetailError> get() = _errorScope

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

    private fun loadProduct() {
        viewModelLaunch {
            productRepository.getProductById(productId).onSuccess {
                _productWithQuantity.value = ProductWithQuantity(product = it)
            }.onError {
                setError(it, ProductDetailError.LoadProduct)
            }
        }
    }

    override fun plusCount(productId: Long) {
        _productWithQuantity.value = currentCount().inc()
    }

    override fun minusCount(productId: Long) {
        _productWithQuantity.value = currentCount().dec()
    }

    fun addProductToCart() {
        viewModelLaunch {
            cartRepository.addProductToCart(
                currentCount().product.id,
                currentCount().quantity.value,
            ).onSuccess {
                loadProduct()
                _addCartComplete.setValue(Unit)
            }.onError {
                setError(it, ProductDetailError.AddCart)
            }
        }
    }

    fun addToRecentProduct(lastSeenProductState: Boolean) {
        viewModelLaunch {
            recentProductRepository.getMostRecentProduct().onSuccess {
                if (!lastSeenProductState) {
                    _mostRecentProductVisibility.value = false
                } else {
                    _mostRecentProduct.value =
                        productRepository.getProductById(it.productId).getOrThrow()
                    setMostRecentVisibility(it.id, productId)
                }
            }.onError {
                setError(it, ProductDetailError.Recent)
            }

            recentProductRepository.insert(productId)
        }
    }

    private fun setMostRecentVisibility(
        mostRecentProductId: Long,
        currentProductId: Long,
    ) {
        _mostRecentProductVisibility.value = (mostRecentProductId != currentProductId)
    }

    private fun setError(
        dataError: DataError,
        errorScope: ProductDetailError,
    ) {
        if (dataError is ShowError) {
            mutableDataError.setValue(dataError)
        } else {
            _errorScope.setValue(errorScope)
        }
    }
}

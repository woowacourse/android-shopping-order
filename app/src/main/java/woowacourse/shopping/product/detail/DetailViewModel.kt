package woowacourse.shopping.product.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.cart.ButtonEvent
import woowacourse.shopping.data.repository.CartProductRepository
import woowacourse.shopping.data.repository.RecentlyViewedProductRepository
import woowacourse.shopping.product.catalog.ProductUiModel
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData

class DetailViewModel(
    product: ProductUiModel,
    private val cartProductRepository: CartProductRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
) : ViewModel() {
    private val _product = MutableLiveData<ProductUiModel>(product)
    val product: LiveData<ProductUiModel> = _product

    private val _quantity = MutableLiveData<Int>(product.quantity)
    val quantity: LiveData<Int> = _quantity

    private val _price = MutableLiveData<Int>(product.price)
    val price: LiveData<Int> = _price

    private val _latestViewedProduct = MutableLiveData<ProductUiModel>()
    val latestViewedProduct: LiveData<ProductUiModel> = _latestViewedProduct

    private val _errorMessage = MutableSingleLiveData<String>()
    val errorMessage: SingleLiveData<String> = _errorMessage

    init {
        viewModelScope.launch {
            recentlyViewedProductRepository.insertRecentlyViewedProductId(product.id)
        }
    }

    fun updateQuantity(buttonEvent: ButtonEvent) {
        when (buttonEvent) {
            ButtonEvent.INCREASE -> increaseQuantity()
            ButtonEvent.DECREASE -> decreaseQuantity()
        }
    }

    fun addToCart() {
        viewModelScope.launch {
            val addedProduct = product.value?.copy(quantity = quantity.value ?: 0) ?: return@launch
            if (addedProduct.cartItemId != null) {
                cartProductRepository.updateProduct(addedProduct.id, addedProduct.quantity)
            } else {
                cartProductRepository.insertCartProduct(addedProduct.id, addedProduct.quantity)
            }
        }
    }

    fun setLatestViewedProduct() {
        viewModelScope.launch {
            val product =
                recentlyViewedProductRepository.getLatestViewedProduct().getOrElse {
                    _errorMessage.setValue("최근 본 상품을 가져오는 데 실패했습니다.")
                    return@launch
                }
            product?.let { _latestViewedProduct.value = it }
        }
    }

    private fun increaseQuantity() {
        _quantity.value = _quantity.value?.plus(1)
        setPriceSum()
    }

    private fun decreaseQuantity() {
        if ((_quantity.value ?: 0) <= 0) return
        _quantity.value = _quantity.value?.minus(1)
        setPriceSum()
    }

    private fun setPriceSum() {
        _price.value = (product.value?.price ?: 0) * (quantity.value ?: 0)
    }
}

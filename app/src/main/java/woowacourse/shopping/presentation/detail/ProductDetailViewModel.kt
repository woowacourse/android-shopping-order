package woowacourse.shopping.presentation.detail

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.datasource.DataResponse.Companion.NULL_BODY_ERROR_CODE
import com.example.domain.datasource.onFailure
import com.example.domain.datasource.onSuccess
import com.example.domain.model.Product
import com.example.domain.model.Quantity
import com.example.domain.repository.CartRepository
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.RecentProductRepository
import kotlinx.coroutines.launch
import woowacourse.shopping.common.Event
import woowacourse.shopping.common.emit
import woowacourse.shopping.presentation.detail.ProductDetailActivity.Companion.PRODUCT_ID_KEY

class ProductDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel(), ProductDetailActionHandler {
    private val _productUiModel = MutableLiveData<ProductDetailUiModel>()
    val productUiModel: LiveData<ProductDetailUiModel> get() = _productUiModel

    private val _price: MutableLiveData<Int> = MutableLiveData()
    val price: LiveData<Int> get() = _price

    private val _lastRecentProduct = MutableLiveData<LastRecentProductUiModel>()
    val lastRecentProduct: LiveData<LastRecentProductUiModel> get() = _lastRecentProduct

    private val _putOnCartEvent = MutableLiveData<Event<Boolean>>()
    val putOnCartEvent get() = _putOnCartEvent

    private val handler = Handler(Looper.getMainLooper())

    init {
        savedStateHandle.get<Int>(PRODUCT_ID_KEY)?.let(::loadProduct)
    }

    private fun loadProduct(productId: Int) {
        viewModelScope.launch {
            val result = productRepository.find(productId)
            result.onSuccess { product ->
                _productUiModel.value =
                    ProductDetailUiModel(
                        product,
                        Quantity(1),
                        isSuccess = true,
                        isFailure = false,
                    )
                loadRecentProduct()
                putRecentProduct(product)
            }.onFailure { code, error ->
                _productUiModel.value =
                    ProductDetailUiModel(
                        isSuccess = false,
                        isFailure = true,
                    )
            }
        }
    }

    private fun putRecentProduct(product: Product) {
        recentProductRepository.save(product)
    }

    private fun loadRecentProduct() {
        val recentProducts = recentProductRepository.findRecentProducts()
        if (recentProducts.isEmpty()) return
        val lastRecentProduct = recentProducts[0].toLastRecentProductUiModel()
        _lastRecentProduct.value = lastRecentProduct
    }

    override fun onClickPlusButton() {
        var quantity = _productUiModel.value?.quantity ?: return
        _productUiModel.value = _productUiModel.value?.copy(quantity = ++quantity)
    }

    override fun onClickMinusButton() {
        var quantity = _productUiModel.value?.quantity ?: return
        _productUiModel.value = _productUiModel.value?.copy(quantity = --quantity)
    }

    override fun onClickRecentProduct() {
        val lastProduct = lastRecentProduct.value?.product ?: return
        loadProduct(lastProduct.id)
    }

    override fun onClickAddCartProduct() {
        val product = productUiModel.value?.product ?: return
        val quantity = productUiModel.value?.quantity ?: return
        viewModelScope.launch {
            val result =
                cartRepository.find(product.id)
                    .onSuccess {
                        val newQuantity = Quantity(it.quantity.count + quantity.count)
                        cartRepository.changeQuantity(it.id, newQuantity)
                    }.onFailure { code, _ ->
                        if (code == NULL_BODY_ERROR_CODE || code == 400) {
                            cartRepository.postCartItem(product.id, quantity)
                        }
                    }
            result.onSuccess {
                _putOnCartEvent.emit(true)
            }.onFailure { code, error ->
                if (code == NULL_BODY_ERROR_CODE) {
                    _putOnCartEvent.emit(true)
                } else {
                    _putOnCartEvent.emit(false)
                }
            }
        }
    }
}

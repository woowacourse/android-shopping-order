package woowacourse.shopping.presentation.detail

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.domain.datasource.map
import com.example.domain.datasource.onFailure
import com.example.domain.datasource.onSuccess
import com.example.domain.model.Quantity
import com.example.domain.repository.CartRepository
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.RecentProductRepository
import woowacourse.shopping.common.Event
import woowacourse.shopping.common.emit
import woowacourse.shopping.presentation.detail.ProductDetailActivity.Companion.PRODUCT_ID_KEY
import kotlin.concurrent.thread

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
        thread {
            val result = productRepository.find(productId)
            handler.post {
                result.onSuccess { product ->
                    _productUiModel.postValue(
                        ProductDetailUiModel(
                            product,
                            Quantity(1),
                            isSuccess = true,
                        ),
                    )
                }.onFailure { _, _ ->
                    _productUiModel.postValue(ProductDetailUiModel(isFailure = true))
                }
            }
        }
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
        TODO("Not yet implemented")
    }

    override fun onClickAddCartProduct() {
        val product = productUiModel.value?.product ?: return
        val quantity = productUiModel.value?.quantity ?: return
        thread {
            val result =
                cartRepository.find(product.id).map {
                    repeat(quantity.count) {
                        cartRepository.increaseQuantity(product.id)
                    }
                /*
                val newQuantity = Quantity(it.quantity.count + quantity.count)
                cartRepository.changeQuantity(product.id, newQuantity)
                 */
                }
            handler.post {
                result.onSuccess {
                    _putOnCartEvent.emit(true)
                }.onFailure { _, _ ->
                    _putOnCartEvent.emit(false)
                }
            }
        }
    }
}

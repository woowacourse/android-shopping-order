package woowacourse.shopping.view.product.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.usecase.cart.AddToCartUseCase
import woowacourse.shopping.domain.usecase.cart.GetCartProductByProductIdUseCase
import woowacourse.shopping.domain.usecase.cart.UpdateCartQuantityUseCase
import woowacourse.shopping.view.util.MutableSingleLiveData
import woowacourse.shopping.view.util.SingleLiveData

class ProductDetailViewModel(
    val product: Product,
    private val recentProductRepository: RecentProductRepository,
    private val getCartProductByProductIdUseCase: GetCartProductByProductIdUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
) : ViewModel(),
    ProductDetailEventHandler {
    private val _quantity = MutableLiveData(MINIMUM_QUANTITY)
    val quantity: LiveData<Int> get() = _quantity

    val totalPrice: LiveData<Int> = _quantity.map { it * product.price }

    private val _lastViewedProduct = MutableLiveData<RecentProduct?>()
    val lastViewedProduct: LiveData<RecentProduct?> get() = _lastViewedProduct

    private val _addToCartEvent = MutableSingleLiveData<Unit>()
    val addToCartEvent: SingleLiveData<Unit> get() = _addToCartEvent

    private val _lastViewedProductClickEvent = MutableSingleLiveData<Unit>()
    val lastViewedProductClickEvent: SingleLiveData<Unit> get() = _lastViewedProductClickEvent

    init {
        loadLastViewedProduct()
        updateRecentProduct()
    }

    override fun onQuantityIncreaseClick(item: Product) {
        val newQuantity = (quantity.value ?: MINIMUM_QUANTITY) + QUANTITY_TO_ADD
        _quantity.postValue(newQuantity)
    }

    override fun onQuantityDecreaseClick(item: Product) {
        val newQuantity = (quantity.value ?: MINIMUM_QUANTITY) - QUANTITY_TO_ADD
        if (newQuantity > 0) {
            _quantity.postValue(newQuantity)
        }
    }

    override fun onAddToCartClick() {
        viewModelScope.launch {
            getCartProductByProductIdUseCase(product.id)
                .onSuccess { cartProduct ->
                    val quantityToAdd = quantity.value ?: MINIMUM_QUANTITY
                    val updateResult =
                        if (cartProduct == null) {
                            addToCartUseCase(product, quantityToAdd)
                        } else {
                            val newQuantity = cartProduct.quantity + quantityToAdd
                            updateCartQuantityUseCase(cartProduct, newQuantity)
                        }

                    updateResult
                        .onSuccess {
                            _quantity.postValue(MINIMUM_QUANTITY)
                            _addToCartEvent.postValue(Unit)
                        }.onFailure { Log.e("error", it.message.toString()) }
                }.onFailure { Log.e("error", it.message.toString()) }
        }
    }

    override fun onLastViewedProductClick() {
        _lastViewedProductClickEvent.setValue(Unit)
    }

    private fun loadLastViewedProduct() {
        viewModelScope.launch {
            recentProductRepository
                .getLastViewedProduct()
                .onSuccess {
                    _lastViewedProduct.postValue(it)
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    private fun updateRecentProduct() {
        viewModelScope.launch {
            val recentProduct = RecentProduct(product = product)
            recentProductRepository
                .replaceRecentProduct(recentProduct)
                .onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    companion object {
        private const val MINIMUM_QUANTITY = 1
        private const val QUANTITY_TO_ADD = 1
    }
}

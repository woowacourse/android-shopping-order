package woowacourse.shopping.view.cart.recommend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.CartProducts
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.usecase.cart.AddToCartUseCase
import woowacourse.shopping.domain.usecase.cart.GetCartProductsUseCase
import woowacourse.shopping.domain.usecase.cart.UpdateCartQuantityUseCase
import woowacourse.shopping.domain.usecase.product.GetRecommendedProductsUseCase
import woowacourse.shopping.view.cart.recommend.adapter.RecommendedProductItem
import woowacourse.shopping.view.util.MutableSingleLiveData
import woowacourse.shopping.view.util.SingleLiveData

class CartProductRecommendViewModel(
    selectedProducts: CartProducts,
    private val getRecommendedProductsUseCase: GetRecommendedProductsUseCase,
    private val getCartProductsUseCase: GetCartProductsUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
) : ViewModel(),
    CartProductRecommendEventHandler {
    val cartProducts = MutableLiveData(selectedProducts)

    private val _recommendedProducts = MutableLiveData<List<RecommendedProductItem>>()
    val recommendedProducts: LiveData<List<RecommendedProductItem>> get() = _recommendedProducts

    val totalPrice: LiveData<Int> = cartProducts.map { it.totalPrice }
    val totalQuantity: LiveData<Int> = cartProducts.map { it.totalQuantity }

    private val _selectedProduct = MutableSingleLiveData<Product>()
    val selectedProduct: SingleLiveData<Product> get() = _selectedProduct

    init {
        viewModelScope.launch {
            getCartProductsUseCase()
                .onSuccess { pagedResult ->
                    loadRecommendedProducts(pagedResult.items)
                }.onFailure { Log.e("error", it.message.toString()) }
        }
    }

    private fun loadRecommendedProducts(cartProducts: List<CartProduct>) {
        viewModelScope.launch {
            val cartIds = cartProducts.map { it.product.id }
            getRecommendedProductsUseCase(cartIds)
                .onSuccess { recommendedProducts ->
                    val recommendedItems =
                        recommendedProducts
                            .shuffled()
                            .map { RecommendedProductItem(it) }
                    _recommendedProducts.postValue(recommendedItems)
                }.onFailure { Log.e("error", it.message.toString()) }
        }
    }

    override fun onProductClick(item: Product) {
        _selectedProduct.setValue(item)
    }

    override fun onPlusClick(item: Product) {
        viewModelScope.launch {
            addToCartUseCase(item, QUANTITY_TO_ADD)
                .onSuccess { cartProduct ->
                    cartProducts.postValue(cartProduct?.let { cartProducts.value?.plus(it) })
                    updateProductQuantity(item, QUANTITY_TO_ADD)
                }.onFailure { Log.e("error", it.message.toString()) }
        }
    }

    override fun onQuantityIncreaseClick(item: Product) {
        updateCartQuantity(item, QUANTITY_TO_ADD)
    }

    override fun onQuantityDecreaseClick(item: Product) {
        updateCartQuantity(item, -QUANTITY_TO_ADD)
    }

    private fun updateCartQuantity(
        item: Product,
        quantityDelta: Int,
    ) {
        viewModelScope.launch {
            val existing =
                cartProducts.value?.value?.firstOrNull { it.product.id == item.id }
                    ?: return@launch
            val newQuantity = existing.quantity + quantityDelta

            updateCartQuantityUseCase(existing, newQuantity)
                .onSuccess { updated ->
                    var updatedList = cartProducts.value?.minus(existing)
                    if (newQuantity > MINIMUM_QUANTITY && updated != null) {
                        updatedList = updatedList?.plus(updated)
                    }
                    cartProducts.postValue(updatedList ?: CartProducts(emptyList()))
                    updateProductQuantity(item, quantityDelta)
                }.onFailure { Log.e("error", it.message.toString()) }
        }
    }

    private fun updateProductQuantity(
        item: Product,
        quantityDelta: Int,
    ) {
        val updatedList =
            recommendedProducts.value.orEmpty().map {
                if (it.product.id == item.id) it.copy(quantity = it.quantity + quantityDelta) else it
            }
        _recommendedProducts.postValue(updatedList)
    }

    companion object {
        private const val QUANTITY_TO_ADD = 1
        private const val MINIMUM_QUANTITY = 0
    }
}

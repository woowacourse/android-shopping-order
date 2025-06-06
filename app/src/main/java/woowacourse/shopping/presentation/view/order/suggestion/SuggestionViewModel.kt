package woowacourse.shopping.presentation.view.order.suggestion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.di.provider.RepositoryProvider
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.model.SuggestionProductUiModel
import woowacourse.shopping.presentation.model.toSuggestionUiModel
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData
import woowacourse.shopping.presentation.view.order.suggestion.event.SuggestionMessageEvent
import woowacourse.shopping.presentation.view.order.suggestion.event.SuggestionStateListener

class SuggestionViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) : ViewModel(),
    SuggestionStateListener {
    private val _toastSuggestionEvent = MutableSingleLiveData<SuggestionMessageEvent>()
    val toastSuggestionEvent: SingleLiveData<SuggestionMessageEvent> = _toastSuggestionEvent

    private val _suggestionProducts = MutableLiveData<List<SuggestionProductUiModel>>(emptyList())
    val suggestionProducts: LiveData<List<SuggestionProductUiModel>> = _suggestionProducts

    val purchaseProducts: LiveData<List<Long>> =
        _suggestionProducts.map { suggestionProducts ->
            suggestionProducts
                .filter { it.quantity > 0 }
                .mapNotNull { cartRepository.findCartIdByProductId(it.productId).getOrNull() }
        }

    val totalPurchaseProductQuantity: LiveData<Int> =
        _suggestionProducts.map { suggestionProducts -> suggestionProducts.sumOf { it.quantity } }

    val totalPurchaseProductPrice: LiveData<Int> =
        _suggestionProducts.map { suggestionProducts -> suggestionProducts.sumOf { it.price * it.quantity } }

    fun fetchSuggestionProducts() {
        val excludedProductIds =
            cartRepository
                .fetchAllCartItems()
                .getOrDefault(emptyList())
                .filter { cart ->
                    !suggestionProducts.value.orEmpty().any { it.productId == cart.product.id }
                }.map { it.product.id }
        viewModelScope.launch {
            productRepository
                .fetchSuggestionProducts(
                    SUGGESTION_LIMIT,
                    excludedProductIds,
                ).onSuccess { combine(it) }
                .onFailure { }
        }
    }

    private fun combine(suggestionProducts: List<Product>) {
        val ids = suggestionProducts.map { it.id }
        cartRepository
            .findCartProductsByProductIds(ids)
            .onFailure { postFailureCartEvent(SuggestionMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE) }
            .onSuccess {
                val updatedItems = applyCartQuantities(it, suggestionProducts)
                _suggestionProducts.postValue(updatedItems)
            }
    }

    private fun applyCartQuantities(
        cartProducts: List<CartProduct>,
        suggestionProducts: List<Product>,
    ): List<SuggestionProductUiModel> {
        val cartItemMap = cartProducts.associateBy { it.product.id }
        return suggestionProducts.map { product ->
            val found = cartItemMap[product.id]
            if (found != null) return@map product.toSuggestionUiModel(found.quantity)
            product.toSuggestionUiModel(DEFAULT_QUANTITY)
        }
    }

    override fun onQuantitySelectorOpenButtonClick(productId: Long) {
        increaseProductQuantity(productId)
    }

    override fun increaseQuantity(productId: Long) {
        increaseProductQuantity(productId)
    }

    override fun decreaseQuantity(productId: Long) {
        decreaseProductQuantity(productId)
    }

    private fun increaseProductQuantity(productId: Long) {
        viewModelScope.launch {
            cartRepository
                .insertCartProductQuantityToCart(productId, QUANTITY_STEP)
                .onSuccess { fetchSuggestionProducts() }
                .onFailure { postFailureCartEvent(SuggestionMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE) }
        }
    }

    private fun decreaseProductQuantity(productId: Long) {
        cartRepository.decreaseCartProductQuantityFromCart(productId, QUANTITY_STEP) { result ->
            result
                .onSuccess { fetchSuggestionProducts() }
                .onFailure { postFailureCartEvent(SuggestionMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE) }
        }
    }

    private fun postFailureCartEvent(event: SuggestionMessageEvent) {
        _toastSuggestionEvent.postValue(event)
    }

    companion object {
        private const val QUANTITY_STEP = 1
        private const val DEFAULT_QUANTITY = 0
        private const val SUGGESTION_LIMIT = 10

        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val cartRepository = RepositoryProvider.cartRepository
                    val productRepository = RepositoryProvider.productRepository
                    return SuggestionViewModel(cartRepository, productRepository) as T
                }
            }
    }
}

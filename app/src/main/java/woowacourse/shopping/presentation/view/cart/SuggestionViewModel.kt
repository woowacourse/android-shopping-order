package woowacourse.shopping.presentation.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.di.provider.RepositoryProvider
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.model.SuggestionProductUiModel
import woowacourse.shopping.presentation.model.toSuggestionUiModel
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData
import woowacourse.shopping.presentation.view.cart.event.SuggestionMessageEvent

class SuggestionViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _toastEvent = MutableSingleLiveData<SuggestionMessageEvent>()
    val toastEvent: SingleLiveData<SuggestionMessageEvent> = _toastEvent

    private val _suggestionProducts = MutableLiveData<List<SuggestionProductUiModel>>(emptyList())
    val suggestionProducts: LiveData<List<SuggestionProductUiModel>> = _suggestionProducts

    private val excludeCartProductIds: List<Long> by lazy { excludeCartProductIds() }

    init {
        fetchSuggestionProducts()
    }

    fun fetchSuggestionProducts() {
        productRepository.fetchSuggestionProducts(
            SUGGESTION_LIMIT,
            excludeCartProductIds,
        ) { result ->
            result
                .onSuccess { fetchSuggestionProductsSuccessHandle(it) }
                .onFailure { _toastEvent.postValue(SuggestionMessageEvent.FETCH_SUGGESTION_PRODUCT_FAILURE) }
        }
    }

    private fun excludeCartProductIds(): List<Long> =
        cartRepository
            .getAllCartProducts()
            .filter { it.quantity != 0 }
            .map { it.product.id }

    private fun fetchSuggestionProductsSuccessHandle(suggestionProducts: List<Product>) {
        val ids = suggestionProducts.map { it.id }
        cartRepository
            .findCartProductsByProductIds(ids)
            .onFailure { _toastEvent.postValue(SuggestionMessageEvent.FIND_PRODUCT_QUANTITY_FAILURE) }
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

    companion object {
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

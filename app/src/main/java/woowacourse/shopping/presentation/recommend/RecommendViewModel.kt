package woowacourse.shopping.presentation.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.domain.repository.CartItemsRepository
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.domain.repository.ViewedItemRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.presentation.product.catalog.toUiModel
import woowacourse.shopping.presentation.product.detail.CartEvent
import woowacourse.shopping.presentation.util.SingleLiveEvent

class RecommendViewModel(
    private val productsRepository: ProductsRepository,
    private val cartItemRepository: CartItemsRepository,
    private val viewedItemRepository: ViewedItemRepository,
) : ViewModel() {
    private val _recommendedProducts: MutableLiveData<List<ProductUiModel>> =
        MutableLiveData(emptyList())
    val recommendedProducts: LiveData<List<ProductUiModel>>
        get() = _recommendedProducts

    private val _cartEvent: SingleLiveEvent<CartEvent> = SingleLiveEvent()
    val cartEvent: LiveData<CartEvent>
        get() = _cartEvent

    private val _totalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val totalPrice: LiveData<Int>
        get() = _totalPrice

    private val _totalCount: MutableLiveData<Int> = MutableLiveData(0)
    val totalCount: LiveData<Int>
        get() = _totalCount

    private var checkedProducts: List<ProductUiModel> = emptyList()

    init {
        loadRecommendProducts()
    }

    fun setCheckedProducts(products: List<ProductUiModel>) {
        checkedProducts = products
        checkedProducts.apply {
            _totalPrice.value = this.sumOf { it.price * it.quantity }
            _totalCount.value = this.count()
        }
    }

    fun addProduct(productUiModel: ProductUiModel) {
        viewModelScope.launch {
            val updated =
                productUiModel.copy(quantity = productUiModel.quantity + 1, isExpanded = true)
            _recommendedProducts.value =
                _recommendedProducts.value?.map {
                    if (it.id == updated.id) {
                        updated
                    } else {
                        it
                    }
                }
            cartItemRepository.addCartItem(updated.id, updated.quantity)
                .onFailure { emitFailEvent() }
            updateOrderInfo(productUiModel.price, 1)
        }
    }

    fun increaseQuantity(productUiModel: ProductUiModel) {
        updateProducts(productUiModel) { it.copy(quantity = it.quantity + 1) }
        updateOrderInfo(productUiModel.price, 1)
    }

    fun decreaseQuantity(productUiModel: ProductUiModel) {
        updateProducts(productUiModel) {
            val updatedQuantity = (it.quantity - 1).coerceAtLeast(0)
            val updated = it.copy(quantity = updatedQuantity)
            if (updated.quantity <= 0) {
                deleteProduct(it)
                updated.copy(isExpanded = false)
            } else {
                updated
            }
        }
        updateOrderInfo(-productUiModel.price, -1)
    }

    private fun deleteProduct(productUiModel: ProductUiModel) {
        viewModelScope.launch {
            cartItemRepository.deleteCartItem(productUiModel.id)
                .onFailure { emitFailEvent() }
        }
    }

    private fun loadRecommendProducts() {
        viewModelScope.launch {
            val lastViewedItem = viewedItemRepository.getLastViewedItem()
                .mapCatching { it?.toUiModel() }
                .getOrNull()
            lastViewedItem?.let { loadProductsByCategory(it.category) }
        }
    }

    private fun loadProductsByCategory(category: String) {
        viewModelScope.launch {
            productsRepository.getRecommendProducts(category)
                .onSuccess { products ->
                    _recommendedProducts.value = products.map { it.toUiModel() }
                }
                .onFailure { emitFailEvent() }
        }
    }

    private fun updateProducts(
        target: ProductUiModel,
        transform: (ProductUiModel) -> ProductUiModel?,
    ) {
        _recommendedProducts.value =
            _recommendedProducts.value?.mapNotNull { product ->
                if (product.id == target.id) {
                    transform(product)?.also {
                        updateQuantity(it)
                    }
                } else {
                    product
                }
            }
    }

    private fun updateQuantity(productUiModel: ProductUiModel) {
        viewModelScope.launch {
            if (productUiModel.quantity <= 0) return@launch

            cartItemRepository.updateCartItemQuantity(productUiModel.id, productUiModel.quantity)
                .onFailure { emitFailEvent() }
        }
    }

    private fun updateOrderInfo(
        price: Int,
        count: Int,
    ) {
        _totalPrice.value = _totalPrice.value?.plus(price)
        _totalCount.value = _totalCount.value?.plus(count)
    }

    private fun emitFailEvent() {
        _cartEvent.value = CartEvent.ADD_TO_CART_FAILURE
    }

    companion object {
        val FACTORY: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    RecommendViewModel(
                        productsRepository = RepositoryProvider.productsRepository,
                        cartItemRepository = RepositoryProvider.cartItemRepository,
                        viewedItemRepository = RepositoryProvider.viewedItemRepository,
                    )
                }
            }
    }
}

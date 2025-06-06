package woowacourse.shopping.presentation.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.presentation.recommend.OrderEvent.OrderItemFailure
import woowacourse.shopping.presentation.recommend.OrderEvent.OrderItemSuccess
import woowacourse.shopping.presentation.util.SingleLiveEvent

class RecommendViewModel(
    private val productsRepository: ProductsRepository,
    private val cartItemRepository: CartItemRepository,
    private val orderRepository: OrderRepository,
    initialCheckedItems: List<ProductUiModel>,
) : ViewModel() {
    private val _items: MutableLiveData<List<ProductUiModel>> = MutableLiveData(emptyList())
    val items: LiveData<List<ProductUiModel>>
        get() = _items

    private val _updatedProduct = MutableLiveData<ProductUiModel>()
    val updatedProduct: LiveData<ProductUiModel>
        get() = _updatedProduct

    private val _checkedItems = MutableLiveData<List<ProductUiModel>>(initialCheckedItems)
    val checkedItems: LiveData<List<ProductUiModel>> get() = _checkedItems

    val totalOrderPrice: LiveData<Int> =
        checkedItems.map { checkedProducts ->
            checkedProducts.sumOf { it.quantity * it.price }
        }

    val totalOrderCount: LiveData<Int> =
        checkedItems.map { checkedProducts ->
            checkedProducts.sumOf { it.quantity }
        }

    private val _orderEvent = SingleLiveEvent<OrderEvent>()
    val orderEvent: LiveData<OrderEvent> = _orderEvent

    init {
        loadRecommendedProductsFromLastViewed()
    }

    private fun loadRecommendedProductsFromLastViewed() {
        viewModelScope.launch {
            val cartProductIds = cartItemRepository.getCartItemProductIds()

            val result = productsRepository.getRecommendedProductsFromLastViewed(cartProductIds)

            result.onSuccess { recommendedProducts ->
                _items.postValue(recommendedProducts)
            }
        }
    }

    fun toggleQuantity(product: ProductUiModel) {
        val toggled =
            product.copy(quantity = product.quantity + 1)

        viewModelScope.launch {
            val result = cartItemRepository.addCartItem(toggled.id, toggled.quantity)
            result.onSuccess {
                _updatedProduct.postValue(toggled)
                applyProductChange(toggled)
            }
        }
    }

    fun increaseQuantity(product: ProductUiModel) {
        val newProduct = product.copy(quantity = product.quantity + 1)

        viewModelScope.launch {
            val result =
                cartItemRepository.updateCartItemQuantity(newProduct.id, newProduct.quantity)
            result
                .onSuccess {
                    _updatedProduct.postValue(newProduct)
                    applyProductChange(newProduct)
                }
        }
    }

    fun decreaseQuantity(product: ProductUiModel) {
        val newQuantity = (product.quantity - 1).coerceAtLeast(0)
        val updated = product.copy(quantity = newQuantity)
        viewModelScope.launch {
            if (product.quantity == 0) {
                val result = cartItemRepository.deleteCartItem(product.id)

                result.onSuccess {
                    applyProductChange(product)
                }
            } else {
                val result = cartItemRepository.updateCartItemQuantity(updated.id, updated.quantity)

                result.onSuccess {
                    _updatedProduct.postValue(updated)
                    applyProductChange(updated)
                }
            }
        }
    }

    private fun applyProductChange(toggled: ProductUiModel) {
        val currentList = _items.value.orEmpty()
        val updatedList =
            currentList.map {
                if (it.id == toggled.id) toggled else it
            }
        _items.postValue(updatedList)

        val currentChecked = _checkedItems.value.orEmpty().toMutableList()
        val index = currentChecked.indexOfFirst { it.id == toggled.id }

        if (toggled.quantity > 0) {
            if (index >= 0) {
                currentChecked[index] = toggled
            } else {
                currentChecked.add(toggled)
            }
        } else {
            if (index >= 0) {
                currentChecked.removeAt(index)
            }
        }

        _checkedItems.postValue(currentChecked)
    }

    fun orderCheckedItems() {
        viewModelScope.launch {
            val cartIds = cartItemRepository.getCartItemCartIds()
            val result = orderRepository.orderItems(cartIds)
            result
                .onSuccess {
                    _orderEvent.postValue(OrderItemSuccess)
                }
                .onFailure {
                    _orderEvent.postValue(OrderItemFailure)
                }
        }
    }

    companion object {
        fun provideFactory(initialCheckedItems: List<ProductUiModel>): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    RecommendViewModel(
                        productsRepository = RepositoryProvider.productsRepository,
                        cartItemRepository = RepositoryProvider.cartItemRepository,
                        orderRepository = RepositoryProvider.orderRepository,
                        initialCheckedItems = initialCheckedItems,
                    )
                }
            }
        }
    }
}

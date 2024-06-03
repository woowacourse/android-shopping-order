package woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.presentation.base.BaseViewModel
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.base.Event
import woowacourse.shopping.presentation.base.emit
import woowacourse.shopping.presentation.common.ProductCountHandler
import woowacourse.shopping.presentation.model.ProductItemId
import kotlin.concurrent.thread

class OrderRecommendViewModel(
    private val productHistoryRepository: ProductHistoryRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val orderRepository: OrderRepository,
) : BaseViewModel(), ProductCountHandler {
    private val _uiState: MutableLiveData<OrderRecommendUiState> =
        MutableLiveData(OrderRecommendUiState())
    val uiState: LiveData<OrderRecommendUiState> get() = _uiState

    private val _navigateAction: MutableLiveData<Event<OrderRecommendNavigateAction>> =
        MutableLiveData(null)
    val navigateAction: LiveData<Event<OrderRecommendNavigateAction>> get() = _navigateAction

    private val shoppingCart: MutableMap<ProductItemId, Cart?> = mutableMapOf()

    init {
        recommendProductLoad()
    }

    private fun recommendProductLoad() {
        thread {
            productHistoryRepository.getRecommendedProducts(10).onSuccess { recommendProducts ->
                hideError()
                val state = uiState.value ?: return@onSuccess
                _uiState.postValue(state.copy(recommendProducts = recommendProducts))
            }.onFailure { e -> showError(e) }
        }
    }

    fun load(orderCarts: List<Cart>) {
        _uiState.value?.let { state ->
            _uiState.value = state.copy(orderCarts = orderCarts)
        }
    }

    override fun retry() {}

    fun order() {
        thread {
            val state = uiState.value ?: return@thread
            orderRepository.insertOrder(state.orderCarts.map { it.id }).onSuccess {
                hideError()
                _navigateAction.emit(OrderRecommendNavigateAction.NavigateToProductList)
            }.onFailure { e ->
                showError(e)
            }
        }
    }

    override fun plusProductQuantity(
        productId: Long,
        position: Int,
    ) {
        updateProductQuantity(productId, increment = true)
    }

    override fun minusProductQuantity(
        productId: Long,
        position: Int,
    ) {
        updateProductQuantity(productId, increment = false)
    }

    private fun updateProductQuantity(
        productId: Long,
        increment: Boolean,
    ) {
        val state = uiState.value ?: return

        val recommendProducts =
            calculateUpdateProducts(state.recommendProducts, productId, increment)
        _uiState.postValue(state.copy(recommendProducts = recommendProducts))
    }

    private fun calculateUpdateProducts(
        products: List<Product>,
        productId: Long,
        increment: Boolean,
    ): List<Product> {
        return products.map { product ->
            if (product.id == productId) {
                val updatedQuantity = calculateUpdateQuantity(product.quantity, increment)
                updateCart(product, updatedQuantity)
                product.copy(quantity = updatedQuantity)
            } else {
                product
            }
        }
    }

    private fun calculateUpdateQuantity(
        currentQuantity: Int,
        increment: Boolean,
    ): Int {
        return if (increment) currentQuantity + 1 else currentQuantity - 1
    }

    private fun updateCart(
        product: Product,
        updatedQuantity: Int,
    ) {
        val cartItemId = shoppingCart[ProductItemId(product.id)]
        if (cartItemId == null) {
            insertCartProduct(product, updatedQuantity)
            return
        }

        if (updatedQuantity <= 0) {
            deleteCartProduct(product.id, cartItemId.id)
        } else {
            updateCartProduct(product.id, cartItemId.id, updatedQuantity)
        }
    }

    private fun insertCartProduct(
        product: Product,
        quantity: Int,
    ) {
        thread {
            shoppingCartRepository.postCartItem(
                productId = product.id,
                quantity = quantity,
            ).onSuccess { cartItemId ->
                hideError()
                val state = uiState.value ?: return@onSuccess
                val cart = Cart(id = cartItemId.id, product = product, quantity = 1)
                shoppingCart[ProductItemId(product.id)] = cart
                _uiState.postValue(state.copy(orderCarts = state.orderCarts + cart))
            }.onFailure { e ->
                showError(e)
            }
        }
    }

    private fun deleteCartProduct(
        productId: Long,
        cartId: Int,
    ) {
        thread {
            shoppingCartRepository.deleteCartItem(
                cartId = cartId,
            ).onSuccess {
                hideError()
                val state = uiState.value ?: return@onSuccess
                val cart = shoppingCart[ProductItemId(productId)] ?: return@onSuccess
                shoppingCart[ProductItemId(productId)] = null
                _uiState.postValue(state.copy(orderCarts = state.orderCarts - cart))
            }.onFailure { e ->
                showError(e)
            }
        }
    }

    private fun updateCartProduct(
        productId: Long,
        cartId: Int,
        quantity: Int,
    ) {
        thread {
            shoppingCartRepository.patchCartItem(
                cartId = cartId,
                quantity = quantity,
            ).onSuccess {
                hideError()
                var state = uiState.value ?: return@onSuccess
                val cart = shoppingCart[ProductItemId(productId)] ?: return@onSuccess
                val updateCart = cart.copy(quantity = quantity)
                shoppingCart[ProductItemId(productId)] = updateCart
                state = state.copy(orderCarts = state.orderCarts - cart)
                state = state.copy(orderCarts = state.orderCarts + updateCart)

                _uiState.postValue(state)
            }.onFailure { e ->
                showError(e)
            }
        }
    }

    companion object {
        fun factory(
            productHistoryRepository: ProductHistoryRepository,
            shoppingCartRepository: ShoppingCartRepository,
            orderRepository: OrderRepository,
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory {
                OrderRecommendViewModel(
                    productHistoryRepository,
                    shoppingCartRepository,
                    orderRepository,
                )
            }
        }
    }
}

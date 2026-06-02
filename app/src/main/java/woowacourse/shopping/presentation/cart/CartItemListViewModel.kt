package woowacourse.shopping.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.domain.addToCartUseCase
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.cart.model.CartUiState
import woowacourse.shopping.presentation.cart.model.toUiModel
import kotlin.math.min

class CartItemListViewModel(
    private val cartRepository: CartRepository = AppContainer.cartRepository,
) : ViewModel() {
    private val cart = cartRepository.cart
    private val paymentItemIds = MutableStateFlow(emptySet<Long>())
    private val _uiState = MutableStateFlow(CartUiState())

    val uiState =
        combine(cart, paymentItemIds, _uiState) { cart, paymentItemIds, state ->
            val items =
                cart.items.map { it.toUiModel(isSelected = it.product.id in paymentItemIds) }
            val payment =
                PaymentItems(cart.items.filter { it.product.id in paymentItemIds }.toSet())
            val fromIndex = state.page * PAGE_SIZE
            val toIndex = min(fromIndex + PAGE_SIZE, items.size)
            CartUiState(
                page = state.page,
                totalPrice = payment.totalPrice,
                totalQuantity = payment.totalQuantity,
                currentCartItems = items.subList(fromIndex, toIndex),
                isCanMoveNext = toIndex < items.size,
                isSelectAll = items.isNotEmpty() && items.all { it.isSelected },
                totalCartSize = items.size,
                isShowPageSection = items.size > PAGE_SIZE,
                isLoading = state.isLoading,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.Eagerly,
            initialValue = _uiState.value,
        )

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            cartRepository.loadCart()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun getPaymentItemIds(): List<Long> = paymentItemIds.value.toList()

    fun deleteItem(productId: Long) {
        viewModelScope.launch {
            cartRepository.deleteItem(productId)
        }
    }

    fun addItemToCart(productId: Long) {
        viewModelScope.launch {
            addToCartUseCase(cartRepository, productId)
        }
    }

    fun decreaseItemFromCart(productId: Long) {
        viewModelScope.launch {
            val cartItem = cart.value.items.find { it.product.id == productId }
            if (cartItem == null) return@launch

            cartRepository.changeCartItem(productId, cartItem.decrease().quantity)
        }
    }

    fun nextPage() {
        if (!uiState.value.isCanMoveNext) return
        _uiState.update { it.copy(page = it.page + 1) }
    }

    fun previousPage() {
        if (uiState.value.page == 0) return
        _uiState.update { it.copy(page = it.page - 1) }
    }

    fun selectItem(productId: Long) {
        paymentItemIds.update {
            if (productId in it) {
                it - productId
            } else {
                it + productId
            }
        }
    }

    fun toggleSelectAll() {
        paymentItemIds.update { _ ->
            if (uiState.value.isSelectAll) {
                emptySet()
            } else {
                cart.value.items
                    .map { it.product.id }
                    .toSet()
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}

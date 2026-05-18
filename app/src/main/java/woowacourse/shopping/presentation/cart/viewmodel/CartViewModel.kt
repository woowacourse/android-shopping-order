package woowacourse.shopping.presentation.cart.viewmodel

import android.nfc.tech.MifareUltralight.PAGE_SIZE
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.di.RepositoryProvider
import woowacourse.shopping.di.RepositoryProvider.cartRepository
import woowacourse.shopping.domain.model.AddItemResult
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.model.RemoveItemResult
import woowacourse.shopping.domain.model.UpdateItemResult
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.cart.model.CartUiState
import woowacourse.shopping.presentation.cart.model.toUiModel
import woowacourse.shopping.presentation.common.addToCartUseCase
import kotlin.math.min

class CartViewModel(
    private val cartRepository: CartRepository = RepositoryProvider.cartRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private var paymentItems = PaymentItems(emptySet())

    private val _uiEvents = Channel<CartEvent>(Channel.BUFFERED)
    val uiEvents: Flow<CartEvent> = _uiEvents.receiveAsFlow()

    private val exceptionHandler =
        CoroutineExceptionHandler { _, _ ->
            viewModelScope.launch {
                _uiEvents.send(CartEvent.ShowError("알 수 없는 오류가 발생했습니다."))
            }
        }

    fun getPaymentItemIds(): List<Long> = paymentItems.getProductIds()

    fun refreshCart() {
        viewModelScope.launch(exceptionHandler) {
            loadCartItems()
        }
    }

    fun deleteItem(productId: Long) {
        viewModelScope.launch(exceptionHandler) {
            when (val result = cartRepository.deleteItem(productId)) {
                is RemoveItemResult.Success -> {
                    loadCartItems(result.cart)
                    _uiEvents.send(CartEvent.DeleteSuccess)
                }

                is RemoveItemResult.NotFoundItem -> {
                    _uiEvents.send(CartEvent.DeleteNotFound)
                }

                is RemoveItemResult.Error -> {
                    _uiEvents.send(CartEvent.ShowError(result.message))
                }
            }
        }
    }

    fun increase(productId: Long) {
        viewModelScope.launch(exceptionHandler) {
            when (val result = addToCartUseCase(cartRepository, productId)) {
                is AddItemResult.NewAdded -> {
                    loadCartItems(result.cart)
                }

                is AddItemResult.Incremented -> {
                    loadCartItems(result.cart)
                }

                is AddItemResult.Error -> {
                    _uiEvents.send(CartEvent.ShowError(result.message))
                }
            }
        }
    }

    fun decrease(productId: Long) {
        viewModelScope.launch(exceptionHandler) {
            val cartItem = cartRepository.getCart().items.find { it.product.id == productId }
            if (cartItem == null) return@launch

            when (
                val result =
                    cartRepository.changeCartItem(productId, cartItem.decrease().quantity)
            ) {
                is UpdateItemResult.Success -> loadCartItems(result.cart)
                is UpdateItemResult.Error -> {
                    _uiEvents.send(CartEvent.ShowError(result.message))
                    loadCartItems()
                }
            }
        }
    }

    fun nextPage() {
        if (!uiState.value.isCanMoveNext) return
        _uiState.update { it.copy(page = it.page + 1) }
        viewModelScope.launch(exceptionHandler) {
            refreshCart()
        }
    }

    fun previousPage() {
        if (uiState.value.page == 0) return
        _uiState.update {
            it.copy(page = it.page - 1)
        }
        viewModelScope.launch(exceptionHandler) {
            refreshCart()
        }
    }

    fun selectItem(productId: Long) {
        viewModelScope.launch(exceptionHandler) {
            val cart = cartRepository.getCart()
            val cartItem =
                cart.items
                    .find { it.product.id == productId } ?: return@launch

            paymentItems =
                if (paymentItems.isContain(productId)) {
                    paymentItems.remove(productId)
                } else {
                    paymentItems.add(cartItem)
                }

            _uiState.update {
                it.copy(
                    currentCartItems =
                        it.currentCartItems.map { item ->
                            if (item.product.id == productId) {
                                item.copy(isSelected = !item.isSelected)
                            } else {
                                item
                            }
                        },
                )
            }
            syncPaymentDerivedState(cart)
        }
    }

    fun toggleSelectAll() {
        viewModelScope.launch(exceptionHandler) {
            val cart = cartRepository.getCart()
            paymentItems =
                if (uiState.value.isSelectAll) {
                    PaymentItems(emptySet())
                } else {
                    PaymentItems(cart.items.toSet())
                }
            _uiState.update { state ->
                state.copy(
                    currentCartItems =
                        state.currentCartItems.map {
                            it.copy(isSelected = paymentItems.isContain(it.product.id))
                        },
                )
            }
            syncPaymentDerivedState(cart)
        }
    }

    private suspend fun loadCartItems(providedCart: Cart? = null) {
        if (uiState.value.isLoading) return
        val isFirstLoad = uiState.value.totalCartSize == 0
        _uiState.update { it.copy(isLoading = true) }
        try {
            val cart = providedCart ?: cartRepository.getCart()

            paymentItems =
                if (isFirstLoad) {
                    PaymentItems(cart.items.toSet())
                } else {
                    val selectedIds =
                        cart.items
                            .map { it.product.id }
                            .filter { paymentItems.isContain(it) }
                            .toSet()
                    PaymentItems(
                        cart.items.filter { it.product.id in selectedIds }.toSet(),
                    )
                }

            val items =
                cart.items.map {
                    it.toUiModel(
                        isSelected = paymentItems.isContain(it.product.id),
                    )
                }
            val maxPage = if (items.isEmpty()) 0 else (items.size - 1) / PAGE_SIZE

            _uiState.update {
                val page = it.page.coerceIn(0, maxPage)
                val fromIndex = page * PAGE_SIZE
                val toIndex = min(fromIndex + PAGE_SIZE, items.size)
                it.copy(
                    page = page,
                    totalCartSize = items.size,
                    currentCartItems = items.subList(fromIndex, toIndex),
                    isCanMoveNext = toIndex < items.size,
                    isShowPageSection = items.size > PAGE_SIZE,
                )
            }
            syncPaymentDerivedState(cart)
        } finally {
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    private fun syncPaymentDerivedState(cart: Cart) {
        _uiState.update {
            it.copy(
                totalPrice = paymentItems.totalPrice,
                totalQuantity = paymentItems.totalQuantity,
                isSelectAll =
                    cart.items.isNotEmpty() &&
                        cart.items.all { item -> paymentItems.isContain(item.product.id) },
            )
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}

sealed interface CartEvent {
    data object DeleteSuccess : CartEvent

    data object DeleteNotFound : CartEvent

    data class ShowError(
        val message: String,
    ) : CartEvent
}

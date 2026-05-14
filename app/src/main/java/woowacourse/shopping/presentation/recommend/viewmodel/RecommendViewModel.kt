package woowacourse.shopping.presentation.recommend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.di.RepositoryProvider
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.recommend.model.RecommendUiState

class RecommendViewModel(
    private val cartRepository: CartRepository = RepositoryProvider.cartRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecommendUiState())
    val uiState: StateFlow<RecommendUiState> = _uiState.asStateFlow()

    private lateinit var paymentItems: PaymentItems

    fun loadPaymentId(productIds: LongArray) {
        viewModelScope.launch {
            val cart = cartRepository.getCart()
            paymentItems =
                PaymentItems(
                    cart.items
                        .filter { cartItem ->
                            productIds.contains(cartItem.product.id)
                        }.toSet(),
                )

            _uiState.update {
                it.copy(
                    totalQuantity = paymentItems.totalQuantity,
                    totalPrice = paymentItems.totalPrice,
                )
            }
        }
    }
//
//    fun deleteItem(productId: Long) {
//        viewModelScope.launch {
//            when (val result = cartRepository.deleteItem(productId)) {
//                is RemoveItemResult.Success -> {
//                    loadCartItems(result.cart)
//                    _uiEvents.send(CartEvent.DeleteSuccess)
//                }
//
//                is RemoveItemResult.NotFoundItem -> {
//                    _uiEvents.send(CartEvent.DeleteNotFound)
//                }
//            }
//        }
//    }
//
//    fun increase(productId: Long) {
//        viewModelScope.launch {
//            loadCartItems(addToCartUseCase(cartRepository, productId))
//        }
//    }
//
//    fun decrease(productId: Long) {
//        viewModelScope.launch {
//            val cartItem = cartRepository.getCart().items.find { it.product.id == productId }
//            if (cartItem == null) return@launch
//
//            val updatedCart = cartRepository.changeCartItem(productId, cartItem.decrease().quantity)
//            loadCartItems(updatedCart)
//        }
//    }
//
//    private suspend fun loadCartItems(providedCart: Cart? = null) {
//        if (uiState.value.isLoading) return
//        _uiState.update { it.copy(isLoading = true) }
//        try {
//            val cart = providedCart ?: cartRepository.getCart()
//
//            val selectedIds =
//                cart.items
//                    .map { it.product.id }
//                    .filter { paymentItems.isContain(it) }
//                    .toSet()
//            paymentItems =
//                PaymentItems(
//                    cart.items.filter { it.product.id in selectedIds }.toSet(),
//                )
//
//            val items =
//                cart.items.map {
//                    it.toUiModel(
//                        isSelected = paymentItems.isContain(it.product.id),
//                    )
//                }
//            val maxPage = if (items.isEmpty()) 0 else (items.size - 1) / PAGE_SIZE
//
//            _uiState.update {
//                val page = it.page.coerceIn(0, maxPage)
//                val fromIndex = page * PAGE_SIZE
//                val toIndex = min(fromIndex + PAGE_SIZE, items.size)
//                it.copy(
//                    page = page,
//                    totalCartSize = items.size,
//                    currentCartItems = items.subList(fromIndex, toIndex),
//                    isCanMoveNext = toIndex < items.size,
//                    isShowPageSection = items.size > PAGE_SIZE,
//                )
//            }
//            syncPaymentDerivedState(cart)
//        } finally {
//            _uiState.update {
//                it.copy(isLoading = false)
//            }
//        }
//    }
//
//    private fun syncPaymentDerivedState(cart: Cart) {
//        _uiState.update {
//            it.copy(
//                totalPrice = paymentItems.totalPrice,
//                totalQuantity = paymentItems.totalQuantity,
//                isSelectAll =
//                    cart.items.isNotEmpty() &&
//                        cart.items.all { item -> paymentItems.isContain(item.product.id) },
//            )
//        }
//    }
//
//    companion object {
//        private const val PAGE_SIZE = 5
//    }
}

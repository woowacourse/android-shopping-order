package woowacourse.shopping.backend.retrofit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import woowacourse.shopping.backend.retrofit.awaitBody
import woowacourse.shopping.backend.retrofit.dto.CartRequest
import woowacourse.shopping.backend.retrofit.repository.ShoppingCartRetrofitRepository
import woowacourse.shopping.mapper.toCartQuantity
import woowacourse.shopping.mapper.toDomainShoppingCartItems
import woowacourse.shopping.model.ShoppingCartItem

class ShoppingCartViewModel(
    private val shoppingCartRetrofitRepository: ShoppingCartRetrofitRepository,
) : ViewModel() {
    private val _shoppingCartItems = MutableStateFlow<List<ShoppingCartItem>>(emptyList())
    val shoppingCartItems: StateFlow<List<ShoppingCartItem>> = _shoppingCartItems.asStateFlow()

    fun requestCartItems() {
        viewModelScope.launch {
            runCatching {
                loadCartItems()
            }.onSuccess { loadedItems ->
                _shoppingCartItems.value = loadedItems
            }
        }
    }

    fun addOrIncreaseByProductId(
        productId: Long,
        amount: Int = DEFAULT_QUANTITY,
    ) {
        if (amount <= 0) return
        viewModelScope.launch {
            runCatching {
                val currentItems = loadCartItems()
                val targetItem = findByProductId(currentItems, productId)
                if (targetItem == null) {
                    shoppingCartRetrofitRepository
                        .addCartItem(
                            product = CartRequest(productId = productId, quantity = amount),
                        ).awaitBody(errorPrefix = "장바구니 추가 실패")
                } else {
                    val updatedQuantity = targetItem.getQuantity() + amount
                    shoppingCartRetrofitRepository
                        .updateQuantityCartItem(
                            id = targetItem.getId().toInt(),
                            product = updatedQuantity.toCartQuantity(),
                        ).awaitBody(errorPrefix = "장바구니 수량 수정 실패")
                }
                loadCartItems()
            }.onSuccess { latestItems ->
                _shoppingCartItems.value = latestItems
            }
        }
    }

    fun decreaseByProductId(productId: Long) {
        viewModelScope.launch {
            runCatching {
                val currentItems = loadCartItems()
                val targetItem = findByProductId(currentItems, productId) ?: return@runCatching currentItems
                val updatedQuantity = targetItem.getQuantity() - 1
                if (updatedQuantity <= 0) {
                    shoppingCartRetrofitRepository
                        .deleteCartItem(
                            id = targetItem.getId().toInt(),
                        ).awaitBody(errorPrefix = "장바구니 삭제 실패")
                } else {
                    shoppingCartRetrofitRepository
                        .updateQuantityCartItem(
                            id = targetItem.getId().toInt(),
                            product = updatedQuantity.toCartQuantity(),
                        ).awaitBody(errorPrefix = "장바구니 수량 수정 실패")
                }
                loadCartItems()
            }.onSuccess { latestItems ->
                _shoppingCartItems.value = latestItems
            }
        }
    }

    fun removeShoppingItem(shoppingCartItem: ShoppingCartItem) {
        viewModelScope.launch {
            runCatching {
                shoppingCartRetrofitRepository
                    .deleteCartItem(
                        id = shoppingCartItem.getId().toInt(),
                    ).awaitBody(errorPrefix = "장바구니 삭제 실패")
                loadCartItems()
            }.onSuccess { latestItems ->
                _shoppingCartItems.value = latestItems
            }
        }
    }

    fun increaseShoppingItemQuantity(shoppingCartItem: ShoppingCartItem) {
        val productId = shoppingCartItem.product.id
        addOrIncreaseByProductId(productId = productId, amount = 1)
    }

    fun decreaseShoppingItemQuantity(shoppingCartItem: ShoppingCartItem) {
        decreaseByProductId(productId = shoppingCartItem.product.id)
    }

    fun getQuantityPrice(shoppingCartItem: ShoppingCartItem): Int = shoppingCartItem.getProductQuantityPrice()

    fun getTotalCount(): Int = _shoppingCartItems.value.sumOf { shoppingCartItem -> shoppingCartItem.getQuantity() }

    private suspend fun loadCartItems(): List<ShoppingCartItem> =
        shoppingCartRetrofitRepository
            .requestCartItems(
                page = DEFAULT_PAGE,
                size = DEFAULT_SIZE,
                sort = DEFAULT_SORT,
            ).awaitBody(errorPrefix = "장바구니 조회 실패")
            .toDomainShoppingCartItems()

    private fun findByProductId(
        shoppingCartItems: List<ShoppingCartItem>,
        productId: Long,
    ): ShoppingCartItem? =
        shoppingCartItems.firstOrNull { shoppingCartItem ->
            shoppingCartItem.product.id == productId
        }

    private companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 100
        private const val DEFAULT_QUANTITY = 1
        private val DEFAULT_SORT = listOf("id,asc")
    }
}

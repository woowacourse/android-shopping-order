package woowacourse.shopping.ui.cart

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import woowacourse.shopping.ui.model.CartItemUiModel

data class CartUiState(
    val items: ImmutableList<CartItemUiModel> = persistentListOf(),
    val page: Int = 0,
    val isCanMoveNext: Boolean = false,
    val isLoading: Boolean = true,
    val totalCartQuantity: Int = 0,
    val totalCartCount: Int = 0,
    val totalPrice: Long = 0,
    val errorMessage: String? = null,
    val isAllChecked: Boolean = false,
    val selectedCartItemCount: Int = 0,
    val selectedCartItems: ImmutableList<String> = persistentListOf(),
)

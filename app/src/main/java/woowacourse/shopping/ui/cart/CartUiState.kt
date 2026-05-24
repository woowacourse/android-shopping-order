package woowacourse.shopping.ui.cart

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import woowacourse.shopping.ui.model.CartItemUiModel
import woowacourse.shopping.ui.model.ProductUiModel

data class CartUiState(
    val items: ImmutableList<CartItemUiModel> = persistentListOf(),
    val page: Int = 0,
    val isCanMoveNext: Boolean = false,
    val isLoading: Boolean = true,
    val totalCartQuantity: Int = 0,
    val totalCartCount: Long = 0,
    val totalPrice: Long = 0,
    val errorMessage: String? = null,
    val isAllChecked: Boolean = false,
    val selectedCartItems: Map<Long, SelectedCartItem> = emptyMap(),
    val isOrder: Boolean = false,
    val recommendProducts: ImmutableList<ProductUiModel> = persistentListOf(),
)

data class SelectedCartItem(
    val totalPrice: Long,
    val quantity: Int,
)

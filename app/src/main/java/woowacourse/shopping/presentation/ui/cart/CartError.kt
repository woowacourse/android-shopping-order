package woowacourse.shopping.presentation.ui.cart

import androidx.annotation.StringRes
import woowacourse.shopping.R

enum class CartError(
    @StringRes val messageResId: Int,
) {
    CartItemsNotFound(R.string.error_cart_item_not_found),
    CartItemNotDeleted(R.string.error_cart_item_not_deleted),
    CartItemsNotModified(R.string.error_cart_item_not_modified),
}

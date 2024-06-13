package woowacourse.shopping.presentation.ui.detail

import androidx.annotation.StringRes
import woowacourse.shopping.R

enum class DetailError(
    @StringRes val messageResId: Int,
) {
    ProductItemsNotFound(R.string.error_product_detail_item_not_found),
    CartItemNotFound(R.string.error_product_not_added_to_cart),
    RecentItemNotFound(R.string.error_product_recent_item_not_found),
}

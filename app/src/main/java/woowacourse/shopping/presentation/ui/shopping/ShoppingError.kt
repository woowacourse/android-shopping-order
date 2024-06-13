package woowacourse.shopping.presentation.ui.shopping

import androidx.annotation.StringRes
import woowacourse.shopping.R

enum class ShoppingError(
    @StringRes val messageResId: Int,
) {
    AllProductsLoaded(R.string.all_products_loaded),
    ProductNotLoaded(R.string.error_products_loaded),
    RecentProductItemsNotFound(R.string.error_recent_viewed_product_item_loaded),
    CartItemsNotModified(R.string.error_cart_item_not_modified),
}

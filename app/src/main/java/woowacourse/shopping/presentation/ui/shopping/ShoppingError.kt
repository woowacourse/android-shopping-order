package woowacourse.shopping.presentation.ui.shopping

import androidx.annotation.StringRes
import woowacourse.shopping.R

enum class ShoppingError(
    @StringRes val messageResId: Int,
) {
    AllProductsLoaded(R.string.all_products_loaded),
    ProductItemsNotFound(R.string.error_product_item_not_found),
    RecentProductItemsNotFound(R.string.error_recent_viewed_product_item_loaded),
    CartItemsNotFound(R.string.error_cart_items_loaded),
    CartItemsNotModified(R.string.error_cart_item_not_modified),
}

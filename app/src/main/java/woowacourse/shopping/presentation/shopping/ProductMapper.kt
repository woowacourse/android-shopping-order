package woowacourse.shopping.presentation.shopping

import android.content.Context
import woowacourse.shopping.R
import woowacourse.shopping.domain.entity.CartProduct
import woowacourse.shopping.domain.entity.Product
import woowacourse.shopping.presentation.cart.CartProductUi
import woowacourse.shopping.presentation.shopping.detail.ProductUi
import woowacourse.shopping.presentation.shopping.product.ProductListErrorEvent
import woowacourse.shopping.presentation.shopping.product.ShoppingUiModel

fun CartProduct.toShoppingUiModel(): ShoppingUiModel.Product {
    return product.toShoppingUiModel().copy(count = count)
}

fun Product.toShoppingUiModel(): ShoppingUiModel.Product {
    return ShoppingUiModel.Product(id, name, price, imageUrl)
}

fun Product.toUiModel(): ProductUi {
    return ProductUi(id, name, price, imageUrl)
}

fun Product.toCartUiModel(initCount: Int = 1): CartProductUi {
    return CartProductUi(product = toUiModel(), count = initCount)
}

fun ProductUi.toDomain(): Product {
    return Product(id, price, name, imageUrl, category)
}

fun ProductListErrorEvent.toErrorMessageFrom(context: Context): String {
    return when (this) {
        ProductListErrorEvent.LoadRecentProducts -> {
            context.getString(R.string.error_msg_load_recent_products)
        }

        ProductListErrorEvent.LoadProducts -> {
            context.getString(R.string.error_msg_load_products)
        }

        ProductListErrorEvent.IncreaseCartCount -> {
            context.getString(R.string.error_msg_increase_cart_count)
        }

        ProductListErrorEvent.DecreaseCartCount -> {
            context.getString(R.string.error_msg_decrease_cart_count)
        }

        ProductListErrorEvent.LoadCartProducts -> {
            context.getString(R.string.error_msg_load_cart_products)
        }
    }
}

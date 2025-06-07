package woowacourse.shopping.ui.model

import woowacourse.shopping.domain.model.Coupons
import woowacourse.shopping.domain.model.Coupons.Companion.EMPTY_COUPONS
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Price.Companion.EMPTY_PRICE
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.model.Products.Companion.EMPTY_PRODUCTS

data class PaymentUiState(
    val products: Products = EMPTY_PRODUCTS,
    val coupons: Coupons = EMPTY_COUPONS,
    val price: Price = EMPTY_PRICE,
    val isOrderSuccess: Boolean? = null,
    val connectionErrorMessage: String? = null,
)

package woowacourse.shopping.view.shoppingCartRecommend

import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.view.common.ProductQuantityClickListener

interface RecommendProductListener : ProductQuantityClickListener {
    fun onBackButtonClick()

    fun onOrderButtonClick(shoppingCartProductsToOrder: List<ShoppingCartProduct>)
}

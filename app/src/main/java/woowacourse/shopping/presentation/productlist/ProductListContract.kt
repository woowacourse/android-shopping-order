package woowacourse.shopping.presentation.productlist

import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.model.ProductModel

interface ProductListContract {
    interface Presenter {
        fun loadProducts()
        fun loadRecentProducts()
        fun navigateProductDetail(productModel: ProductModel)
        fun addCartProductCount(cartProductModel: CartProductModel)
        fun subCartProductCount(cartProductModel: CartProductModel)
    }

    interface View {
        fun showCartCount(count: Int)
        fun showProductModels(cartProductModels: List<CartProductModel>, isLast: Boolean)
        fun replaceProductModel(cartProductModel: CartProductModel)
        fun showRecentProductModels(productModels: List<ProductModel>)
        fun showProductDetail(productModel: ProductModel)
        fun stopLoading()
    }
}

package woowacourse.shopping.ui.detailedProduct

import woowacourse.shopping.model.ProductUIModel

interface DetailedProductContract {
    interface View {
        fun setProductDetail(product: ProductUIModel, lastProduct: ProductUIModel?)
        fun showCartDialog(product: ProductUIModel)
        fun navigateToCart()
        fun navigateToDetailedProduct(productId: Int)
    }

    interface Presenter {
        fun fetchLastProduct()
        fun fetchProductDetail()
        fun addProductToCart(count: Int)
        fun addProductToRecent()
        fun processToCart()
        fun processToDetailedProduct()
    }
}

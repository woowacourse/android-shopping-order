package woowacourse.shopping.ui.detailedProduct

import woowacourse.shopping.model.ProductUIModel

interface DetailedProductContract {
    interface View {
        fun setProductDetail(product: ProductUIModel, lastProduct: ProductUIModel?)
        fun showProductNotFound()
        fun navigateToCart()
        fun navigateToDetailedProduct(productId: Int)
        fun navigateToAddToCartDialog(product: ProductUIModel)
    }

    interface Presenter {
        fun setUpLastProduct()
        fun setUpProductDetail()
        fun addProductToCart(count: Int)
        fun addProductToRecent()
        fun navigateToDetailedProduct()
        fun navigateToAddToCartDialog()
    }
}

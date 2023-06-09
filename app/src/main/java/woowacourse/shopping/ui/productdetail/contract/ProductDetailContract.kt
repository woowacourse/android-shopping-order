package woowacourse.shopping.ui.productdetail.contract

import woowacourse.shopping.model.ProductUIModel

interface ProductDetailContract {
    interface View {
        fun setProductDetail(product: ProductUIModel)
        fun showProductCountDialog(product: ProductUIModel)

        fun showLatestProduct(product: ProductUIModel)

        fun navigateToDetail(id: Long)
        fun setProductCount(count: Int)
    }

    interface Presenter {
        fun setUpProductDetail()
        fun addProductToCart()
        fun addProductToRecent()

        fun addProductCount(id: Long)
        fun subtractProductCount(id: Long)

        fun setProductCountDialog()

        fun setLatestProduct()
        fun clickLatestProduct()
    }
}

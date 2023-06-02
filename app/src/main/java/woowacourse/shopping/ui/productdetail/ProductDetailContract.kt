package woowacourse.shopping.ui.productdetail

import woowacourse.shopping.ui.model.ProductUiModel

interface ProductDetailContract {
    interface View {

        fun showBasket()

        fun updateBindingData(product: ProductUiModel, previousProduct: ProductUiModel?)

        fun showBasketDialog(
            currentProduct: ProductUiModel,
            minusClickListener: () -> Unit,
            plusClickListener: () -> Unit,
            updateBasketProduct: () -> Unit,
        )

        fun updateProductCount(count: Int)

        fun showErrorMessage(errorMessage: String)
    }

    interface Presenter {
        val view: View

        fun setBasketDialog()

        fun selectPreviousProduct()

        fun initProductData()
    }
}

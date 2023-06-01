package woowacourse.shopping.ui.basket

import woowacourse.shopping.domain.Basket
import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Product
import woowacourse.shopping.ui.model.BasketProductUiModel

interface BasketContract {
    interface View {

        fun updateBasketProducts(basketProducts: List<BasketProductUiModel>)

        fun updateNavigatorEnabled(previous: Boolean, next: Boolean)

        fun updateCurrentPage(currentPage: Int)

        fun updateTotalPrice(totalPrice: Int)

        fun updateCheckedProductsCount(checkedProductsCount: Int)

        fun updateTotalCheckBox(isChecked: Boolean)

        fun updateSkeletonState(isLoaded: Boolean)

        fun showPaymentView(basketProducts: List<BasketProductUiModel>)
    }

    interface Presenter {

        val view: View
        var basket: Basket

        fun fetchTotalCheckToCurrentPage(totalIsChecked: Boolean)

        fun updateBasketProductCheckState(basketProduct: BasketProduct)

        fun plusBasketProductCount(product: Product)

        fun minusBasketProductCount(product: Product)

        fun updateBasketProducts()

        fun updatePreviousPage()

        fun updateNextPage()

        fun deleteBasketProduct(product: BasketProductUiModel)

        fun startPayment()
    }
}

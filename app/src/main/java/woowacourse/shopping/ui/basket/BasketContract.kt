package woowacourse.shopping.ui.basket

import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Product
import woowacourse.shopping.ui.model.UiBasketProduct

interface BasketContract {
    interface View {

        fun updateBasketProducts(basketProducts: List<UiBasketProduct>)

        fun updateNavigatorEnabled(previous: Boolean, next: Boolean)

        fun updateCurrentPage(currentPage: Int)

        fun updateTotalPrice(totalPrice: Int)

        fun updateCheckedProductsCount(checkedProductsCount: Int)

        fun updateTotalCheckBox(isChecked: Boolean)

        fun updateSkeletonState(isLoaded: Boolean)
    }

    interface Presenter {
        val view: View

        fun fetchTotalCheckToCurrentPage(totalIsChecked: Boolean)

        fun updateBasketProductCheckState(basketProduct: BasketProduct)

        fun plusBasketProductCount(product: Product)

        fun minusBasketProductCount(product: Product)

        fun updateBasketProducts()

        fun updatePreviousPage()

        fun updateNextPage()

        fun deleteBasketProduct(product: UiBasketProduct)
    }
}

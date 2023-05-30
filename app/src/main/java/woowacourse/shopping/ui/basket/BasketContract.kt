package woowacourse.shopping.ui.basket

import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Product
import woowacourse.shopping.ui.model.UiBasketProduct
import woowacourse.shopping.ui.model.User

interface BasketContract {
    interface View {

        fun updateBasketProducts(basketProducts: List<UiBasketProduct>)

        fun updateNavigatorEnabled(previous: Boolean, next: Boolean)

        fun updateCurrentPage(currentPage: Int)

        fun updateTotalPrice(totalPrice: Int)

        fun updateCheckedProductsCount(checkedProductsCount: Int)

        fun updateTotalCheckBox(isChecked: Boolean)

        fun updateSkeletonState(isLoaded: Boolean)

        fun showUsingPointDialog(user: User)

        fun navigateToOrderDetail(orderId: Int)
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

        fun addOrder(point: Int)

        fun usePoint()
    }
}

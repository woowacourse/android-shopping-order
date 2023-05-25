package woowacourse.shopping.ui.shopping

import woowacourse.shopping.domain.Product
import woowacourse.shopping.ui.model.UiProduct
import woowacourse.shopping.ui.model.UiRecentProduct

interface ShoppingContract {
    interface View {

        fun updateProducts(products: List<UiProduct>)

        fun updateRecentProducts(recentProducts: List<UiRecentProduct>)

        fun showProductDetail(
            currentProduct: UiProduct,
            currentProductBasketId: Int?,
            previousProduct: UiProduct?,
            previousProductBasketId: Int?
        )

        fun updateMoreButtonState(isVisible: Boolean)

        fun updateTotalBasketCount(totalBasketCount: Int)

        fun updateSkeletonState(isLoaded: Boolean)
    }

    interface Presenter {

        val view: View

        fun initBasket()

        fun updateBasket()

        fun fetchTotalBasketCount()

        fun plusBasketProductCount(product: Product)

        fun minusBasketProductCount(product: Product)

        fun addBasketProduct(product: Product)

        fun updateProducts()

        fun fetchRecentProducts()

        fun inquiryProductDetail(product: UiProduct)

        fun fetchHasNext()
    }
}

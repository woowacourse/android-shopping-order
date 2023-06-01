package woowacourse.shopping.ui.shopping

import woowacourse.shopping.domain.Product
import woowacourse.shopping.ui.model.ProductUiModel
import woowacourse.shopping.ui.model.RecentProductUiModel

interface ShoppingContract {
    interface View {

        fun updateProducts(products: List<ProductUiModel>)

        fun updateRecentProducts(recentProducts: List<RecentProductUiModel>)

        fun showProductDetail(
            currentProduct: ProductUiModel,
            currentProductBasketId: Int?,
            previousProduct: ProductUiModel?,
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

        fun inquiryProductDetail(product: ProductUiModel)

        fun fetchHasNext()
    }
}

package woowacourse.shopping.ui.productdetail

import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.model.mapper.toDomain
import woowacourse.shopping.model.mapper.toUi
import woowacourse.shopping.model.UiProduct
import woowacourse.shopping.ui.productdetail.ProductDetailContract.Presenter
import woowacourse.shopping.ui.productdetail.ProductDetailContract.View

class
ProductDetailPresenter(
    view: View,
    private val product: UiProduct,
    showLastViewedProduct: Boolean,
    recentProductRepository: RecentProductRepository,
) : Presenter(view) {
    private val lastViewedProduct =
        recentProductRepository.getPartially(LAST_VIEWED_PRODUCT_SIZE).getLatest()

    init {
        recentProductRepository.add(RecentProduct(product = product.toDomain()))

        if (showLastViewedProduct) {
            view.showLastViewedProductDetail(lastViewedProduct?.product?.toUi())
        }
        view.showProductDetail(product)
    }

    override fun navigateToHome(count: Int) {
        view.navigateToHome(product, count)
    }

    override fun inquiryProductCounter() {
        view.showProductCounter(product)
    }

    override fun inquiryLastViewedProduct() {
        lastViewedProduct?.let { view.navigateToProductDetail(it.toUi()) }
    }

    companion object {
        private const val LAST_VIEWED_PRODUCT_SIZE = 1
    }
}

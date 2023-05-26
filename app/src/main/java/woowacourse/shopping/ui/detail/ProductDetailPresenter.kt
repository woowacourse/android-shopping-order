package woowacourse.shopping.ui.detail

import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.mapper.toUi
import woowacourse.shopping.model.UiProduct
import woowacourse.shopping.ui.detail.ProductDetailContract.Presenter
import woowacourse.shopping.ui.detail.ProductDetailContract.View

class
ProductDetailPresenter(
    view: View,
    private val product: UiProduct,
    showLastViewedProduct: Boolean,
    recentProductRepository: RecentProductRepository,
) : Presenter(view) {
    private val lastViewedProduct = recentProductRepository.getPartially(1).getLatest()

    init {
        recentProductRepository.add(RecentProduct(product = product.toDomain()))

        if (showLastViewedProduct) {
            view.showLastViewedProductDetail(lastViewedProduct?.product?.toUi())
        }
        view.showProductDetail(product)
    }

    override fun inquiryProductCounter() {
        view.showProductCounter(product)
    }

    override fun inquiryLastViewedProduct() {
        lastViewedProduct?.let { view.navigateToProductDetail(it.toUi()) }
    }

    override fun navigateToHome(count: Int) {
        view.navigateToHome(product, count)
    }
}

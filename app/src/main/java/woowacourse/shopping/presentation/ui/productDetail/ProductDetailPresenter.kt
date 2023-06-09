package woowacourse.shopping.presentation.ui.productDetail

import woowacourse.shopping.data.error.WoowaException
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentlyViewedRepository

class ProductDetailPresenter(
    view: ProductDetailContract.View,
    productId: Long,
    productRepository: ProductRepository,
    recentlyViewedRepository: RecentlyViewedRepository,
) : ProductDetailContract.Presenter {
    private lateinit var product: Product

    init {
        productRepository.fetchProduct(
            callback = { result ->
                result
                    .onSuccess {
                        product = it.product
                        fetchLastViewedProduct(view, recentlyViewedRepository)
                        recentlyViewedRepository.addRecentlyViewedProduct(product)
                    }
                    .onFailure {
                        when (it) {
                            is WoowaException.ServerConnectError -> view.showUnexpectedError(
                                it.message ?: "에러 메시지가 없습니다.",
                            )
                            is WoowaException.ResponseBodyNull -> view.showUnexpectedError(
                                it.message ?: "에러 메시지가 없습니다.",
                            )
                            is WoowaException.ResponseFailure -> view.showNoSuchProductError(
                                it.message ?: "에러 메시지가 없습니다.",
                            )
                        }
                    }
            },
            id = productId,
        )
    }

    private fun fetchLastViewedProduct(
        view: ProductDetailContract.View,
        recentlyViewedRepository: RecentlyViewedRepository,
    ) {
        recentlyViewedRepository.getLastViewedProduct()
            .onSuccess { view.setBindingData(product, it) }
            .onFailure { view.showNoSuchProductError(it.message ?: "에러 메시지가 없습니다.") }
    }
}

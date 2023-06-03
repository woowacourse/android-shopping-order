package woowacourse.shopping.presentation.ui.productDetail

import android.util.Log
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentlyViewedProduct
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentlyViewedRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.util.WoowaResult
import woowacourse.shopping.domain.util.WoowaResult.FAIL
import woowacourse.shopping.domain.util.WoowaResult.SUCCESS

class ProductDetailPresenter(
    view: ProductDetailContract.View,
    productId: Long,
    productRepository: ProductRepository,
    recentlyViewedRepository: RecentlyViewedRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
) : ProductDetailContract.Presenter {
    private lateinit var product: Product

    init {
        productRepository.fetchProduct(
            callback = { result ->
                when (result) {
                    is SUCCESS -> {
                        product = result.data.product
                        Log.d("asdf", "product: $product")
                        fetchLastViewedProduct(view, recentlyViewedRepository)
                        recentlyViewedRepository.addRecentlyViewedProduct(product)
                    }
                    is FAIL -> view.handleNoSuchProductError()
                }
            },
            id = productId,
        )
    }

    private fun fetchLastViewedProduct(
        view: ProductDetailContract.View,
        recentlyViewedRepository: RecentlyViewedRepository,
    ) {
        val result: WoowaResult<RecentlyViewedProduct> =
            recentlyViewedRepository.getLastViewedProduct()
        when (result) {
            is SUCCESS -> view.setBindingData(product, result.data)
            is FAIL -> view.handleNoSuchProductError()
        }
    }

    override fun addProductInCart() {
        shoppingCartRepository.insert(
            callback = {},
            productId = product.id,
            quantity = 1,
        )
    }
}

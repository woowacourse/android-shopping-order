package woowacourse.shopping.feature.detail

import com.example.domain.repository.CartRepository
import com.example.domain.repository.RecentProductRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.model.ProductUiModel
import woowacourse.shopping.model.RecentProductUiModel

class DetailPresenter(
    val view: DetailContract.View,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
    product: ProductUiModel,
    recentProductUiModel: RecentProductUiModel?,
) : DetailContract.Presenter {
    override var product: ProductUiModel = product
        private set
    override var recentProduct: RecentProductUiModel? = recentProductUiModel
        private set

    override val isRecentProduct: Boolean

    init {
        isRecentProduct = recentProduct?.let {
            if (product.id == it.product.id) return@let true
            return@let false
        } ?: false
    }

    override fun initScreen() {
        if (isRecentProduct || recentProduct == null) return view.hideRecentScreen()
        recentProduct?.let {
            view.setRecentScreen(
                it.product.name,
                it.product.toMoneyFormat(),
            )
        }
    }

    override fun updateProductCount(count: Int) {
        product.count = count
    }

    override fun handleAddCartClick() {
        cartRepository.getAll(
            onSuccess = {
                val cartProductId = it.find { it.product.id == product.id }?.cartId
                view.showSelectCartProductCountScreen(product, cartProductId)
            },
            onFailure = {},
        )
    }

    override fun navigateRecentProductDetail() {
        recentProduct?.let {
            recentProductRepository.addRecentProduct(it.product.toDomain())
            view.showRecentProductDetailScreen(it)
        }
    }

    override fun setProductCountInfo(count: Int) {
        product = product.copy(count = count)
    }

    override fun exit() {
        view.exitDetailScreen()
    }
}

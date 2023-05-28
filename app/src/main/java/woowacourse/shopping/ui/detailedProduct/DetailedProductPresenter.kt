package woowacourse.shopping.ui.detailedProduct

import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.ProductUIModel
import woowacourse.shopping.utils.ErrorHandler
import woowacourse.shopping.utils.SharedPreferenceUtils

class DetailedProductPresenter(
    private val view: DetailedProductContract.View,
    private val product: ProductUIModel,
    private val sharedPreferenceUtils: SharedPreferenceUtils,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentRepository: RecentRepository
) : DetailedProductContract.Presenter {
    private var lastProduct: ProductUIModel? = null

    override fun setUpLastProduct() {
        sharedPreferenceUtils.getLastProductId()
            .takeIf { it != product.id && it != -1 }
            ?.let {
                productRepository.findById(it) { result ->
                    when (result.isSuccess) {
                        true -> lastProduct = result.getOrNull()?.toUIModel() ?: return@findById
                        false -> ErrorHandler.printError(
                            result.exceptionOrNull() ?: Exception("알 수 없는 오류가 발생했습니다.")
                        )
                    }
                }
            }
        sharedPreferenceUtils.setLastProductId(product.id)
    }

    override fun setUpProductDetail() {
        view.setProductDetail(product, lastProduct)
    }

    override fun addProductToCart(count: Int) {
        cartRepository.insert(product.id)
        cartRepository.updateCount(product.id, count) {
            view.navigateToCart()
        }
    }

    override fun addProductToRecent() {
        recentRepository.findById(product.id)?.let {
            recentRepository.delete(it.id)
        }
        recentRepository.insert(product.toDomain())
    }

    override fun navigateToDetailedProduct() {
        lastProduct?.let {
            sharedPreferenceUtils.setLastProductId(-1)
            view.navigateToDetailedProduct(it)
        }
    }

    override fun navigateToAddToCartDialog() {
        cartRepository.insert(product.toDomain().id)
        view.navigateToAddToCartDialog(product)
    }
}

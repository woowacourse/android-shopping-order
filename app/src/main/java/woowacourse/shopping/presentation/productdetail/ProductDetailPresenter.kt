package woowacourse.shopping.presentation.productdetail

import woowacourse.shopping.CartProductInfo
import woowacourse.shopping.presentation.mapper.toDomain
import woowacourse.shopping.presentation.mapper.toPresentation
import woowacourse.shopping.presentation.model.CartProductInfoModel
import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.RecentProductRepository

class ProductDetailPresenter(
    private val view: ProductDetailContract.View,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
    private val productModel: ProductModel,
) : ProductDetailContract.Presenter {
    private lateinit var mostRecentProductModel: ProductModel
    override fun checkCurrentProductIsMostRecent() {
        recentProductRepository.getMostRecentProduct {
            mostRecentProductModel = it?.toPresentation() ?: productModel
            if (productModel == mostRecentProductModel) {
                view.setMostRecentProductVisible(
                    false,
                    mostRecentProductModel,
                )
            } else {
                view.setMostRecentProductVisible(true, mostRecentProductModel)
            }
        }
    }

    override fun showMostRecentProductDetail() {
        view.navigateToMostRecent(mostRecentProductModel)
    }

    override fun showProductCart() {
        val cartProduct = CartProductInfoModel(0, productModel, 1)
        view.showProductCart(cartProduct)
    }

    override fun updateTotalPrice(count: Int) {
        val price = CartProductInfo(0, productModel.toDomain(), count).totalPrice
        view.setTotalPrice(price)
    }

    override fun saveRecentProduct() {
        recentProductRepository.deleteRecentProductId(productModel.id)
        recentProductRepository.addRecentProductId(productModel.id)
    }

    override fun saveProductInRepository(count: Int) {
        cartRepository.addCartItem(productModel.id) {
            cartRepository.updateCartItemQuantity(productModel.id, count) {
                view.showCompleteMessage(productModel.name)
            }
        }
    }
}

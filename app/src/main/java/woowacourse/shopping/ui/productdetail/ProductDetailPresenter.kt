package woowacourse.shopping.ui.productdetail

import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Count
import woowacourse.shopping.domain.repository.BasketRepository
import woowacourse.shopping.ui.mapper.toDomain
import woowacourse.shopping.ui.model.UiProduct

class ProductDetailPresenter(
    override val view: ProductDetailContract.View,
    private val basketRepository: BasketRepository,
    private var currentProduct: UiProduct,
    private var currentProductBasketId: Int?,
    private var previousProduct: UiProduct?,
    private var previousProductBasketId: Int?
) : ProductDetailContract.Presenter {

    init {
        basketRepository.getAll { basketProducts ->
            currentProduct.basketCount =
                basketProducts.find { it.product.id == currentProduct.id }?.count?.value ?: 0
        }
        if (previousProduct != null) {
            basketRepository.getAll { basketProducts ->
                previousProduct?.basketCount =
                    basketProducts.find { it.product.id == requireNotNull(previousProduct).id }?.count?.value
                        ?: 0
            }
        }
    }

    override fun initProductData() {
        view.updateBindingData(currentProduct, previousProduct)
    }

    override fun setBasketDialog() {
        view.showBasketDialog(
            currentProduct,
            ::minusProductCount,
            ::plusProductCount,
            ::updateBasketProduct
        )
        view.updateProductCount(currentProduct.basketCount)
    }

    private fun minusProductCount() {
        if (currentProduct.basketCount - 1 >= 0) currentProduct.basketCount -= 1
        view.updateProductCount(currentProduct.basketCount)
    }

    private fun plusProductCount() {
        currentProduct.basketCount += 1
        view.updateProductCount(currentProduct.basketCount)
    }

    private fun updateBasketProduct() {
        if (currentProductBasketId != null) {
            updateCurrentProduct()
            view.showBasket()
        } else {
            basketRepository.add(currentProduct.toDomain()) {
                currentProductBasketId = it
                if (currentProduct.basketCount > 1) {
                    updateCurrentProduct()
                }
                view.showBasket()
            }
        }
    }

    private fun updateCurrentProduct() {
        basketRepository.update(
            getAddableCurrentProduct()
        )
    }

    private fun getAddableCurrentProduct() = BasketProduct(
        id = requireNotNull(currentProductBasketId),
        count = Count(currentProduct.basketCount),
        product = currentProduct.toDomain()
    )

    override fun selectPreviousProduct() {
        currentProduct = previousProduct ?: throw IllegalStateException(NO_PREVIOUS_PRODUCT_ERROR)
        currentProductBasketId = previousProductBasketId
        previousProduct = null
        previousProductBasketId = null
        view.updateBindingData(currentProduct, previousProduct)
    }

    companion object {
        private const val NO_PREVIOUS_PRODUCT_ERROR = "이전 아이템이 없는데 접근하고 있습니다."
    }
}

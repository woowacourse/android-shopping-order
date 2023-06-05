package woowacourse.shopping.ui.productdetail

import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Count
import woowacourse.shopping.domain.repository.BasketRepository
import woowacourse.shopping.ui.mapper.toDomainModel
import woowacourse.shopping.ui.model.ProductUiModel

class ProductDetailPresenter(
    override val view: ProductDetailContract.View,
    private val basketRepository: BasketRepository,
    private var currentProduct: ProductUiModel,
    private var currentProductBasketId: Int?,
    private var previousProduct: ProductUiModel?,
    private var previousProductBasketId: Int?,
) : ProductDetailContract.Presenter {

    init {
        basketRepository.getAll().thenAccept { basketProducts ->
            currentProduct.basketCount = basketProducts.getOrThrow()
                .find { it.product.id == currentProduct.id }
                ?.count
                ?.value
                ?: 0
        }.exceptionally { error ->
            error.message?.let {
                view.showErrorMessage(it)
            }
            null
        }

        if (previousProduct != null) {
            basketRepository.getAll().thenAccept { basketProducts ->
                previousProduct?.basketCount = basketProducts.getOrThrow()
                    .find { it.product.id == requireNotNull(previousProduct).id }
                    ?.count
                    ?.value
                    ?: 0
            }.exceptionally { error ->
                error.message?.let {
                    view.showErrorMessage(it)
                }
                null
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
            basketRepository.add(currentProduct.toDomainModel()).thenAccept { basketProductId ->
                currentProductBasketId = basketProductId.getOrThrow()
                if (currentProduct.basketCount > 1) {
                    updateCurrentProduct()
                }
                view.showBasket()
            }.exceptionally { error ->
                error.message?.let { view.showErrorMessage(it) }
                null
            }
        }
    }

    private fun updateCurrentProduct() {
        basketRepository.update(
            basketProduct = getAddableCurrentProduct(),
            onUpdated = {},
            onFailed = { errorMessage ->
                view.showErrorMessage(errorMessage)
            }
        )
    }

    private fun getAddableCurrentProduct() = BasketProduct(
        id = requireNotNull(currentProductBasketId),
        count = Count(currentProduct.basketCount),
        product = currentProduct.toDomainModel()
    )

    override fun selectPreviousProduct() {
        currentProduct =
            previousProduct ?: throw IllegalStateException(NO_PREVIOUS_PRODUCT_ERROR)
        currentProductBasketId = previousProductBasketId
        previousProduct = null
        previousProductBasketId = null
        view.updateBindingData(currentProduct, previousProduct)
    }

    companion object {
        private const val NO_PREVIOUS_PRODUCT_ERROR = "이전 아이템이 없는데 접근하고 있습니다."
    }
}

package woowacourse.shopping.presentation.shopping.detail

import woowacourse.shopping.domain.entity.Product
import woowacourse.shopping.presentation.cart.CartProductUi

data class ProductDetailUiState(
    val cartProduct: CartProductUi,
    val recentProduct: Product?,
) {
    val isRecentProductVisible: Boolean
        get() =
            (recentProduct != null) &&
                (cartProduct.product.id != recentProduct.id)
    companion object {
        fun init(): ProductDetailUiState =
            ProductDetailUiState(
                cartProduct =
                    CartProductUi(
                        product =
                            ProductUi(
                                id = 0,
                                name = "상품이 존재하지 않습니다",
                                price = 0,
                                imageUrl = "",
                            ),
                    ),
                recentProduct = null,
            )
    }
}

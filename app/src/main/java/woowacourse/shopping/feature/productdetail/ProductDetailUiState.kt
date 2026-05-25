package woowacourse.shopping.feature.productdetail

data class ProductDetailUiState(
    val productState: ProductDetailLoadingState = ProductDetailLoadingState.Loading,
    val recentProductState: ProductDetailLoadingState = ProductDetailLoadingState.None,
    val quantity: Int = 1,
)

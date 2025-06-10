package woowacourse.shopping.data.source

import woowacourse.shopping.product.catalog.ProductUiModel

interface CartProductDataSource {
    suspend fun insertCartProduct(
        cartProduct: ProductUiModel,
    ): ProductUiModel

    suspend fun deleteCartProduct(
        cartProduct: ProductUiModel,
    ): Boolean

    suspend fun getCartProductsInRange(
        currentPage: Int,
        pageSize: Int,
    ): List<ProductUiModel>

    suspend fun updateProduct(
        cartProduct: ProductUiModel,
        quantity: Int,
    ): Boolean

    suspend fun getCartItemSize(): Int

    suspend fun getTotalElements(): Int

    suspend fun getCartProducts(
        totalElements: Int,
    ): List<ProductUiModel>
}

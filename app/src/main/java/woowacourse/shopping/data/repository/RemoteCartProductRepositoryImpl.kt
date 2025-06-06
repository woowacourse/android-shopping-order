package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartRemoteDataSource
import woowacourse.shopping.data.dto.cartitem.toUiModel
import woowacourse.shopping.product.catalog.ProductUiModel

class RemoteCartProductRepositoryImpl(
    private val cartRemoteDataSource: CartRemoteDataSource,
) : CartProductRepository {
    override suspend fun insertCartProduct(
        productId: Long,
        quantity: Int,
    ): Long? =
        try {
            cartRemoteDataSource.insertProduct(productId = productId, quantity = quantity)
        } catch (e: Exception) {
            null
        }

    override suspend fun deleteCartProduct(cartItemId: Long): Boolean =
        try {
            true
        } catch (e: Exception) {
            false
        }

    override suspend fun getCartProductsInRange(
        currentPage: Int,
        pageSize: Int,
    ): List<ProductUiModel> =
        try {
            cartRemoteDataSource
                .fetchProducts(
                    page = currentPage,
                    size = pageSize,
                ).cartItemContent
                .map { it.toUiModel() }
        } catch (e: Exception) {
            emptyList()
        }

    override suspend fun updateProduct(
        cartItemId: Long,
        quantity: Int,
    ): Boolean =
        try {
            true
        } catch (e: Exception) {
            false
        }

    override suspend fun getCartItemSize(): Int =
        try {
            cartRemoteDataSource.fetchCartItemsCount()
        } catch (e: Exception) {
            0
        }

    override suspend fun getTotalElements(): Long =
        try {
            cartRemoteDataSource.fetchCartTotalElements()
        } catch (e: Exception) {
            0
        }

    override suspend fun getCartProducts(totalElements: Long): List<ProductUiModel> =
        try {
            cartRemoteDataSource
                .fetchProducts(
                    page = 0,
                    size = totalElements.toInt(),
                ).cartItemContent
                .map { it.toUiModel() }
        } catch (e: Exception) {
            emptyList()
        }
}

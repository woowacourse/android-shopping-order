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
    ): Result<Long?> =
        runCatching {
            cartRemoteDataSource.insertProduct(productId = productId, quantity = quantity)
        }

    override suspend fun deleteCartProduct(cartItemId: Long): Result<Boolean> =
        runCatching {
            cartRemoteDataSource.deleteProduct(cartItemId)
            true
        }

    override suspend fun getCartProductsInRange(
        currentPage: Int,
        pageSize: Int,
    ): Result<List<ProductUiModel>> =
        runCatching {
            cartRemoteDataSource
                .fetchProducts(
                    page = currentPage,
                    size = pageSize,
                ).cartItemContent
                .map { it.toUiModel() }
        }

    override suspend fun updateProduct(
        cartItemId: Long,
        quantity: Int,
    ): Result<Boolean> =
        runCatching {
            cartRemoteDataSource.updateProduct(cartItemId, quantity)
            true
        }

    override suspend fun getCartItemSize(): Result<Int> =
        runCatching {
            cartRemoteDataSource.fetchCartItemsCount()
        }

    override suspend fun getTotalElements(): Result<Long> =
        runCatching {
            cartRemoteDataSource.fetchCartTotalElements()
        }

    override suspend fun getCartProducts(totalElements: Long): Result<List<ProductUiModel>> =
        runCatching {
            cartRemoteDataSource
                .fetchProducts(
                    page = 0,
                    size = totalElements.toInt(),
                ).cartItemContent
                .map { it.toUiModel() }
        }
}

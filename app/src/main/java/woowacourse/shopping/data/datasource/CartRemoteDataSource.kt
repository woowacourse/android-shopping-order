package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.cartitem.ProductResponse

interface CartRemoteDataSource {
    suspend fun insertProduct(
        productId: Long,
        quantity: Int,
    ): Long

    suspend fun deleteProduct(cartItemId: Long)

    suspend fun fetchProducts(
        page: Int,
        size: Int,
    ): ProductResponse

    suspend fun updateProduct(
        cartItemId: Long,
        quantity: Int,
    )

    suspend fun fetchCartTotalElements(): Long

    suspend fun fetchCartItemsCount(): Int
}

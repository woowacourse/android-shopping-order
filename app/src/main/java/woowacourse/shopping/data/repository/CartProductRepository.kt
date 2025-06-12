package woowacourse.shopping.data.repository

import woowacourse.shopping.product.catalog.ProductUiModel

interface CartProductRepository {
    suspend fun insertCartProduct(
        productId: Long,
        quantity: Int,
    ): Result<Long?>
    // cartItemId 반환

    suspend fun deleteCartProduct(cartItemId: Long): Result<Boolean>

    suspend fun getCartProductsInRange(
        currentPage: Int,
        pageSize: Int,
    ): Result<List<ProductUiModel>>

    suspend fun updateProduct(
        cartItemId: Long,
        quantity: Int,
    ): Result<Boolean>

    // 장바구니에 담긴 아이템들의 개수 반환
    suspend fun getCartItemSize(): Result<Int>

    // 장바구니 상품 종류 개수 반환
    suspend fun getTotalElements(): Result<Long>

    suspend fun getCartProducts(totalElements: Long): Result<List<ProductUiModel>>
}

package woowacourse.shopping.data.repository

import woowacourse.shopping.product.catalog.ProductUiModel

interface CartProductRepository {
    suspend fun insertCartProduct(
        productId: Long,
        quantity: Int,
    ): Long?
    // cartItemId 반환

    suspend fun deleteCartProduct(cartItemId: Long): Boolean

    suspend fun getCartProductsInRange(
        currentPage: Int,
        pageSize: Int,
    ): List<ProductUiModel>

    suspend fun updateProduct(
        cartItemId: Long,
        quantity: Int,
    ): Boolean

    // 장바구니에 담긴 아이템들의 개수 반환
    suspend fun getCartItemSize(): Int

    // 장바구니 상품 종류 개수 반환
    suspend fun getTotalElements(): Long

    suspend fun getCartProducts(totalElements: Long): List<ProductUiModel>
}

package woowacourse.shopping.data.repository

import woowacourse.shopping.product.catalog.ProductUiModel

interface CartProductRepository {
    fun insertCartProduct(
        productId: Long,
        quantity: Int,
        callback: (Int?) -> Unit,
        // cartItemId 반환
    )

    fun deleteCartProduct(
        cartItemId: Long,
        callback: (Boolean) -> Unit,
    )

    fun getCartProductsInRange(
        currentPage: Int,
        pageSize: Int,
        callback: (List<ProductUiModel>) -> Unit,
    )

    fun updateProduct(
        cartItemId: Long,
        quantity: Int,
        callback: (Boolean) -> Unit,
    )

    // 장바구니에 담긴 아이템들의 개수 반환
    fun getCartItemSize(callback: (Int) -> Unit)

    // 장바구니 상품 종류 개수 반환
    fun getTotalElements(callback: (Long) -> Unit)

    fun getCartProducts(
        totalElements: Long,
        callback: (List<ProductUiModel>) -> Unit,
    )
}

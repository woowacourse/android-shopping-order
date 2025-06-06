package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.cartitem.ProductResponse

interface CartRemoteDataSource {
    fun insertProduct(
        productId: Long,
        quantity: Int,
        onSuccess: (Int) -> Unit, // cartItemId를 반환
        onFailure: (Throwable) -> Unit,
    )

    fun deleteProduct(
        cartItemId: Long,
        onSuccess: (Unit) -> Unit,
        onFailure: (Throwable) -> Unit,
    )

    fun fetchProducts(
        page: Int,
        size: Int,
        onSuccess: (ProductResponse) -> Unit,
        onFailure: (Throwable) -> Unit,
    )

    fun updateProduct(
        cartItemId: Long,
        quantity: Int,
        onSuccess: (Unit) -> Unit,
        onFailure: (Throwable) -> Unit,
    )

    fun fetchCartTotalElements(
        onSuccess: (Long) -> Unit,
        onFailure: (Throwable) -> Unit,
    )

    fun fetchCartItemsCount(
        onSuccess: (Int) -> Unit,
        onFailure: (Throwable) -> Unit,
    )
}

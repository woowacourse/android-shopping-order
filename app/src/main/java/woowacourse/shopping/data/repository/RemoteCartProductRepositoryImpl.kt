package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartRemoteDataSource
import woowacourse.shopping.data.dto.cartitem.toUiModel
import woowacourse.shopping.product.catalog.ProductUiModel

class RemoteCartProductRepositoryImpl(
    private val cartRemoteDataSource: CartRemoteDataSource,
) : CartProductRepository {
    override fun insertCartProduct(
        productId: Long,
        quantity: Int,
        callback: (Int?) -> Unit,
    ) {
        cartRemoteDataSource.insertProduct(
            productId = productId,
            quantity = quantity,
            onSuccess = { cartItemId -> callback(cartItemId) },
            onFailure = { callback(null) },
        )
    }

    override fun deleteCartProduct(
        cartItemId: Long,
        callback: (Boolean) -> Unit,
    ) {
        cartRemoteDataSource.deleteProduct(
            cartItemId = cartItemId,
            onSuccess = { callback(true) },
            onFailure = { callback(false) },
        )
    }

    override fun getCartProductsInRange(
        currentPage: Int,
        pageSize: Int,
        callback: (List<ProductUiModel>) -> Unit,
    ) {
        cartRemoteDataSource.fetchProducts(
            page = currentPage,
            size = pageSize,
            onSuccess = { response ->
                val products = response.cartItemContent.map { it.toUiModel() }
                callback(products)
            },
            onFailure = { callback(emptyList()) },
        )
    }

    override fun updateProduct(
        cartItemId: Long,
        quantity: Int,
        callback: (Boolean) -> Unit,
    ) {
        cartRemoteDataSource.updateProduct(
            cartItemId = cartItemId,
            quantity = quantity,
            onSuccess = { callback(true) },
            onFailure = { callback(false) },
        )
    }

    override fun getCartItemSize(callback: (Int) -> Unit) {
        cartRemoteDataSource.fetchCartItemsCount(
            onSuccess = { totalSize -> callback(totalSize) },
            onFailure = { callback(0) },
        )
    }

    override fun getTotalElements(callback: (Long) -> Unit) {
        cartRemoteDataSource.fetchCartTotalElements(
            onSuccess = { totalElements -> callback(totalElements) },
            onFailure = { callback(0) },
        )
    }

    override fun getCartProducts(
        totalElements: Long,
        callback: (List<ProductUiModel>) -> Unit,
    ) {
        cartRemoteDataSource.fetchProducts(
            page = 0,
            size = totalElements.toInt(),
            onSuccess = { response ->
                val products = response.cartItemContent.map { it.toUiModel() }
                callback(products)
            },
            onFailure = { callback(emptyList()) },
        )
    }
}

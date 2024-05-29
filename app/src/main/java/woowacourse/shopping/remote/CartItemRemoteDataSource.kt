package woowacourse.shopping.remote

import android.util.Log
import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.data.source.ShoppingCartProductIdDataSource

class CartItemRemoteDataSource(private val cartItemApiService: CartItemApiService) : ShoppingCartProductIdDataSource {
    override fun findByProductId(productId: Long): ProductIdsCountData? {
        return null
    }

    override fun loadPaged(page: Int): List<ProductIdsCountData> {
        val response =
            cartItemApiService.requestCartItems(page = page - 1).execute().body()?.content
                ?: throw NoSuchElementException("there is no product with page: $page")

        return response.map {
            ProductIdsCountData(
                productId = it.product.id,
                quantity = it.quantity,
            )
        }.also {
            Log.d("CartItemRemoteDataSource", "loadPaged: $it")
        }
    }

    override fun loadAll(): List<ProductIdsCountData> {
        return emptyList()
    }

    override fun isFinalPage(page: Int): Boolean {
        return true
    }

    override fun addedNewProductsId(productIdsCountData: ProductIdsCountData): Long {
        return 10
    }

    override fun removedProductsId(productId: Long): Long {
        return 10
    }

    override fun plusProductsIdCount(productId: Long) {
        TODO("Not yet implemented")
    }

    override fun minusProductsIdCount(productId: Long) {
        TODO("Not yet implemented")
    }

    override fun clearAll() {
        TODO("Not yet implemented")
    }
}

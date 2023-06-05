package woowacourse.shopping.data.datasource.product

import woowacourse.shopping.data.NetworkModule.productService
import woowacourse.shopping.data.datasource.response.ProductEntity
import java.lang.Integer.min

class ProductRemoteDataSourceImpl : ProductRemoteDataSource {

    override fun getPartially(
        size: Int,
        lastId: Int,
    ): Result<List<ProductEntity>> {
        val response = productService.requestProducts().execute()

        return response.body()?.run {
            Result.success(
                getDataProductsFromCache(
                    size = size,
                    lastId = lastId,
                    allProducts = this
                )
            )
        } ?: Result.failure(Throwable(PRODUCT_ERROR))
    }

    private fun getDataProductsFromCache(
        size: Int,
        lastId: Int,
        allProducts: List<ProductEntity>,
    ): List<ProductEntity> {
        if (lastId == -1) return allProducts.subList(0, min(allProducts.size, size))
        val startIndex = allProducts.indexOfFirst { it.id == lastId } + 1
        return allProducts.subList(startIndex, min(allProducts.size, startIndex + size))
    }

    companion object {
        private const val PRODUCT_ERROR = "상품 정보를 받아올 수 없습니다."
    }
}

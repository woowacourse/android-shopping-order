package woowacourse.shopping.data.defaultRepository

import woowacourse.shopping.data.dataSource.ProductDataSource
import woowacourse.shopping.data.mapper.ProductMapper.toCartProduct
import woowacourse.shopping.data.mapper.ProductMapper.toCartProducts
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.util.WoowaResult

class DefaultProductRepository(
    private val productDataSource: ProductDataSource,
) : ProductRepository {
    override fun fetchProduct(callback: (WoowaResult<CartProduct>) -> Unit, id: Long) {
        productDataSource.fetchProduct(id) { result ->
            when (result) {
                is WoowaResult.SUCCESS -> callback(WoowaResult.SUCCESS(result.data.toCartProduct()))
                is WoowaResult.FAIL -> callback(result)
            }
        }
    }

    override fun fetchPagedProducts(
        callback: (products: WoowaResult<List<CartProduct>>, isLast: Boolean) -> Unit,
        pageItemCount: Int,
        lastId: Long,
    ) {
        productDataSource.fetchPagedProducts(pageItemCount, lastId) { result ->
            when (result) {
                is WoowaResult.SUCCESS ->
                    callback(WoowaResult.SUCCESS(result.data.toCartProducts()), result.data.last)
                is WoowaResult.FAIL -> callback(result, false)
            }
        }
    }
}

package woowacourse.shopping.data.defaultRepository

import woowacourse.shopping.data.mapper.ProductMapper.toCartProduct
import woowacourse.shopping.data.mapper.ProductMapper.toCartProducts
import woowacourse.shopping.data.remote.ServicePool
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.util.fetchResponseBody

class DefaultProductRepository : ProductRepository {
    override fun fetchProduct(callback: (Result<CartProduct>) -> Unit, id: Long) {
        ServicePool.retrofitService.getProduct(id).fetchResponseBody(
            onSuccess = { callback(Result.success(it.toCartProduct())) },
            onFailure = { callback(Result.failure(it)) },
        )
    }

    override fun fetchPagedProducts(
        callback: (products: Result<List<CartProduct>>, isLast: Boolean) -> Unit,
        pageItemCount: Int,
        lastId: Long,
    ) {
        ServicePool.retrofitService.getPagedProducts(lastId, pageItemCount).fetchResponseBody(
            onSuccess = { callback(Result.success(it.toCartProducts()), it.last) },
            onFailure = { callback(Result.failure(it), false) },
        )
    }
}

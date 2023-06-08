package woowacourse.shopping.data.remote.product

import woowacourse.shopping.data.dataSource.ProductDataSource
import woowacourse.shopping.data.remote.ServicePool
import woowacourse.shopping.data.remote.dto.response.ProductDto
import woowacourse.shopping.data.remote.dto.response.ProductListDto
import woowacourse.shopping.domain.util.WoowaResult
import woowacourse.shopping.util.fetchResponseBody

class ProductRemoteDataSource : ProductDataSource {
    override fun fetchProduct(id: Long, callback: (WoowaResult<ProductDto>) -> Unit) {
        val service = ServicePool.retrofitService
        service.getProduct(id).fetchResponseBody(
            onSuccess = { callback(WoowaResult.SUCCESS(it)) },
            onFailure = { callback(WoowaResult.FAIL(it)) },
        )
    }

    override fun fetchPagedProducts(
        pageItemCount: Int,
        lastId: Long,
        callback: (result: WoowaResult<ProductListDto>) -> Unit,
    ) {
        val service = ServicePool.retrofitService
        service.getPagedProducts(lastId, pageItemCount).fetchResponseBody(
            onSuccess = { callback(WoowaResult.SUCCESS(it)) },
            onFailure = { callback(WoowaResult.FAIL(it)) },
        )
    }
}

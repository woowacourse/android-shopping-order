package woowacourse.shopping.data.dataSource

import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.data.service.RetrofitClient
import woowacourse.shopping.data.service.RetrofitProductService
import woowacourse.shopping.data.utils.createResponseCallback

class RemoteProductDataSource(
    private val service: RetrofitProductService = RetrofitClient.getInstance().retrofitProductService,
) : ProductDataSource {

    override fun getAll(onSuccess: (List<ProductDto>) -> Unit, onFailure: (Exception) -> Unit) {
        service.getProducts().enqueue(
            createResponseCallback<List<ProductDto>>(
                onSuccess = { onSuccess(it) },
                onFailure = { onFailure(it) },
            ),
        )
    }

    override fun findById(
        id: Int,
        onSuccess: (ProductDto) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        service.getProduct(id).enqueue(
            createResponseCallback<ProductDto>(
                onSuccess = { onSuccess(it) },
                onFailure = { onFailure(it) },
            ),
        )
    }
}

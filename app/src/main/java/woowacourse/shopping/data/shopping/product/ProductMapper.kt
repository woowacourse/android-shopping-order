package woowacourse.shopping.data.shopping.product

import woowacourse.shopping.domain.entity.Product
import woowacourse.shopping.remote.dto.response.PageConfigResponse
import woowacourse.shopping.remote.dto.response.ProductResponse
import woowacourse.shopping.remote.dto.response.ProductsResponse

fun ProductResponse.toProduct(): Product {
    return Product(
        id = id,
        price = price,
        name = name,
        imageUrl = imageUrl,
        category = category,
    )
}

fun ProductsResponse.toData(): ProductPageData {
    val pageConfig: PageConfigResponse = pageConfig
    return ProductPageData(
        products = products.map { it.toProduct() },
        pageSize = pageConfig.pageSize,
        pageNumber = pageConfig.pageNumber,
        totalPageSize = totalPageSize,
        totalProductSize = totalProductSize,
    )
}

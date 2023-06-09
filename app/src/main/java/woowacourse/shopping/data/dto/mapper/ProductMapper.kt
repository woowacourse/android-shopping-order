package woowacourse.shopping.data.dto.mapper

import woowacourse.shopping.data.product.response.GetProductResponse
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.URL

object ProductMapper: Mapper<Product, GetProductResponse> {
    override fun Product.toEntity(): GetProductResponse {
        return GetProductResponse(
            id,
            title,
            price,
            picture.value
        )
    }

    override fun GetProductResponse.toDomain(): Product {
        return Product(
            id,
            URL(imageUrl),
            name,
            price
        )
    }
}
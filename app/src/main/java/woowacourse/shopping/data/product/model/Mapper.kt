package woowacourse.shopping.data.product.model

import com.example.domain.product.Product
import woowacourse.shopping.data.product.model.dto.ProductDto

fun ProductDto.toDomain(): Product {
    return Product(id = id, imageUrl = imageUrl, name = name, price = price)
}

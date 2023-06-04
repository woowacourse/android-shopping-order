package woowacourse.shopping.data.model.product

import com.example.domain.Product

fun ProductDto.toDomain(): Product {
    return Product(id = id, imageUrl = imageUrl, name = name, price = price)
}

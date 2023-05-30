package woowacourse.shopping.mapper

import com.example.domain.model.Product
import woowacourse.shopping.data.remote.request.ProductDTO
import woowacourse.shopping.model.ProductUIModel

fun Product.toUIModel(): ProductUIModel {
    return ProductUIModel(
        id = this.id,
        name = this.name,
        price = this.price,
        imageUrl = this.imageUrl,
    )
}

fun ProductUIModel.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.name,
        price = this.price,
        imageUrl = this.imageUrl,
    )
}

fun ProductDTO.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.name,
        price = this.price,
        imageUrl = this.imageUrl,
    )
}


package woowacourse.shopping.data.source.remote.dto.product.mapper

import woowacourse.shopping.data.source.remote.dto.product.ProductContent
import woowacourse.shopping.domain.model.Money
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductName

fun ProductContent.toDomain(): Product =
    Product(
        id = this.id,
        name =
            ProductName(
                this.name,
            ),
        price =
            Money(
                this.price.toLong(),
            ),
        imageUrl = this.imageUrl,
        category = this.category,
    )

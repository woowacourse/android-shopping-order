package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.dto.response.ProductResponse
import woowacouse.shopping.model.product.Product

fun ProductResponse.toModel(): Product = Product(id, name, price, imageUrl)

package woowacourse.shopping.mapper

import com.example.domain.model.Product
import woowacourse.shopping.data.dto.ProductDto

fun ProductDto.toDomain(): Product =
    Product(id, name, imgUrl, price)

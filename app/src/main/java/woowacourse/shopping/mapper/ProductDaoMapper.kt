package woowacourse.shopping.mapper

import com.example.domain.model.Product
import woowacourse.shopping.data.dto.response.ProductDto

fun ProductDto.toDomain(): Product =
    Product(id, name, imgUrl, price)

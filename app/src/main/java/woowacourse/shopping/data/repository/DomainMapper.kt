package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.dto.response.ResponseCartItemsGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseProductsGetDto
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity


fun ResponseCartItemsGetDto.Content.toCartWithProduct() = CartWithProduct(
    id = id, product = this.product.toProduct(), quantity = Quantity(this.quantity)
)

fun ResponseCartItemsGetDto.Product.toProduct() =
    Product(
        id = this.id,
        imageUrl = this.imageUrl,
        name = this.name,
        price = this.price,
        category = this.category,
    )

fun toProductList(dto: ResponseProductsGetDto): List<Product> {
    val productDto = dto.content
    return productDto.map {
        Product(it.id, it.imageUrl, it.name, it.price, it.category)
    }
}

fun toProduct(dto: ResponseProductIdGetDto): Product =
    Product(
        dto.id,
        dto.imageUrl,
        dto.name,
        dto.price,
        dto.category,
    )

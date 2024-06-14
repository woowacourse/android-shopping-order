package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.dto.response.ResponseCartItemsGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseProductsGetDto
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity

fun ResponseCartItemsGetDto.Content.toCartWithProduct() =
    CartWithProduct(
        id = id,
        product = this.product.toProduct(),
        quantity = Quantity(this.quantity),
    )

fun ResponseCartItemsGetDto.toCartWithProduct(): List<CartWithProduct> =
    this.content.map {
        CartWithProduct(
            id = it.id,
            product = it.product.toProduct(),
            quantity = Quantity(it.quantity),
        )
    }

fun ResponseCartItemsGetDto.Product.toProduct() =
    Product(
        id = this.id,
        imageUrl = this.imageUrl,
        name = this.name,
        price = this.price,
        category = this.category,
    )

fun ResponseProductsGetDto.toProductList(): List<Product> {
    val productDto = this.content
    return productDto.map {
        Product(it.id, it.imageUrl, it.name, it.price, it.category)
    }
}

fun ResponseProductIdGetDto.toProduct(): Product =
    Product(
        id,
        imageUrl,
        name,
        price,
        category,
    )

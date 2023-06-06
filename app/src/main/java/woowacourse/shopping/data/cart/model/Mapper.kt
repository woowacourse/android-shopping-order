package woowacourse.shopping.data.cart.model

import com.example.domain.Cart
import com.example.domain.CartProduct
import com.example.domain.Pagination
import woowacourse.shopping.data.cart.model.dto.response.CartProductResponse
import woowacourse.shopping.data.cart.model.dto.response.CartResponse
import woowacourse.shopping.data.product.model.dto.PaginationDto

fun CartProductResponse.toDomain(): CartProduct {
    return CartProduct(
        id = id,
        productId = product.id,
        productImageUrl = product.imageUrl,
        productName = product.name,
        productPrice = product.price,
        quantity = quantity,
        isPicked = true
    )
}

fun CartResponse.toDomain(): Cart {
    return Cart(
        products = cartItems.map(CartProductResponse::toDomain)
    )
}

fun PaginationDto.toDomain(): Pagination {
    return Pagination(
        total = total,
        perPage = perPage,
        currentPage = currentPage,
        lastPage = lastPage
    )
}

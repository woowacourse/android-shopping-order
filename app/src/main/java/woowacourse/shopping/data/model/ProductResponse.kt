package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName
import woowacourse.shopping.domain.model.CartData
import woowacourse.shopping.domain.model.OrderableProduct
import woowacourse.shopping.domain.model.ProductDomain
import woowacourse.shopping.domain.model.ProductItemDomain

data class ProductResponse(
    @SerializedName("content")
    val products: List<Product>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: Sort,
    val totalElements: Int,
    val totalPages: Int,
)

fun Product.toProductItemDomain(): ProductItemDomain =
    ProductItemDomain(
        category = category,
        id = id,
        imageUrl = imageUrl,
        name = name,
        price = price,
    )

fun ProductResponse.toProductDomain2(cartData: List<CartData>): ProductDomain =
    ProductDomain(
        orderableProducts = products.map { product ->
            val targetCartData = cartData.firstOrNull { cart -> cart.productId == product.id }
            product.toOrderableProduct(targetCartData)
        },
        last = last
    )

fun Product.toOrderableProduct(cartData: CartData?): OrderableProduct = OrderableProduct(
    productItemDomain = this.toProductItemDomain(),
    cartData = cartData
)


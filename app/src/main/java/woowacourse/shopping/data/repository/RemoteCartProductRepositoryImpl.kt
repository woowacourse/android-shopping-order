package woowacourse.shopping.data.repository

import android.util.Log
import woowacourse.shopping.data.dto.cartitem.Content
import woowacourse.shopping.data.dto.cartitem.ProductResponse
import woowacourse.shopping.data.dto.cartitem.Quantity
import woowacourse.shopping.data.dto.cartitem.UpdateCartItemRequest
import woowacourse.shopping.data.service.CartItemService
import woowacourse.shopping.data.service.RetrofitProductService
import woowacourse.shopping.product.catalog.ProductUiModel
import woowacourse.shopping.util.toResult

class RemoteCartProductRepositoryImpl : CartProductRepository {
    val retrofitService = RetrofitProductService.INSTANCE.create(CartItemService::class.java)

    override suspend fun insertCartProduct(
        cartProduct: ProductUiModel,
    ): ProductUiModel {
        val response = retrofitService
            .postCartItems(
                request = UpdateCartItemRequest(
                    productId = cartProduct.id,
                    quantity = cartProduct.quantity,
                ),
            ).toResult().getOrThrow()

        val locationHeader = response.headers()["location"]
        val id = locationHeader?.substringAfterLast("/")?.toIntOrNull()

        return cartProduct.copy(cartItemId = id)
    }



    override suspend fun deleteCartProduct(
        cartProduct: ProductUiModel,
    ): Boolean {
        val response = retrofitService.deleteCartItem(cartItemId = cartProduct.cartItemId ?: 0).toResult().getOrThrow().let { true }
        return response
    }

    override suspend fun getCartProductsInRange(
        currentPage: Int,
        pageSize: Int,
    ): List<ProductUiModel> {
        val response = retrofitService.requestCartItems(
            page = currentPage,
            size = pageSize
        ).toResult().getOrThrow()

        val content: List<Content> = response.body()?.content ?: return emptyList()
        val products: List<ProductUiModel> =
            content.map {
                ProductUiModel(
                    id = it.product.id.toInt(),
                    imageUrl = it.product.imageUrl,
                    name = it.product.name,
                    price = it.product.price,
                    cartItemId = it.id.toInt(),
                    quantity = it.quantity,
                )
            }

        return products
    }

    override suspend fun updateProduct(
        cartProduct: ProductUiModel,
        quantity: Int,
    ): Boolean {
        val response = retrofitService
            .patchCartItemQuantity(
                cartItemId = cartProduct.cartItemId ?: 0,
                quantity = Quantity(quantity),
            ).toResult().getOrThrow().let { true }
        return response
    }

    override suspend fun getCartItemSize(): Int {
        val response = retrofitService.getCartItemsCount().toResult().getOrThrow()

        val body: Quantity = response.body() ?: return 0
        return body.value
    }

    override suspend fun getTotalElements(): Int {
        val response = retrofitService.requestCartItems(
            page = 0,
            size = 1,
        ).toResult().getOrThrow()

        val body: ProductResponse = response.body() ?: return 0
        val totalElements = body.totalElements.toInt()
        return totalElements
    }

    override suspend fun getCartProducts(
        totalElements: Int,
    ): List<ProductUiModel> {
        val response = retrofitService
            .requestCartItems(
                page = 0,
                size = totalElements,
            ).toResult().getOrThrow()

        val body: ProductResponse = response.body() ?: return emptyList()
        val content: List<Content> = body.content
        val products: List<ProductUiModel> =
            content.map {
                ProductUiModel(
                    id = it.product.id.toInt(),
                    imageUrl = it.product.imageUrl,
                    name = it.product.name,
                    price = it.product.price,
                    quantity = it.quantity,
                    cartItemId = it.id.toInt(),
                )
            }
        return products
    }
}

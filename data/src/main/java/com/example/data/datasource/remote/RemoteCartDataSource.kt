package com.example.data.datasource.remote

import com.example.data.datasource.remote.model.request.AddCartItemRequest
import com.example.data.datasource.remote.model.request.CartItemQuantityRequest
import com.example.data.datasource.remote.model.response.cart.toCartItems
import com.example.data.datasource.remote.service.CartItemService
import com.example.domain.datasource.CartDataSource
import com.example.domain.datasource.DataResponse
import com.example.domain.datasource.DataResponse.Companion.NULL_BODY_ERROR_CODE
import com.example.domain.datasource.chain
import com.example.domain.datasource.map
import com.example.domain.datasource.onFailure
import com.example.domain.model.CartItem
import com.example.domain.model.Quantity

class RemoteCartDataSource(
    private val cartItemService: CartItemService,
) : CartDataSource {
    override fun findAll(): DataResponse<List<CartItem>> =
        cartItemService.requestCartQuantityCount().executeForDataResponse().map {
            cartItemService.requestCartItems(page = 0, size = it.quantity).executeForDataResponse()
        }.chain {
            it.toCartItems()
        }

    override fun postCartItem(
        productId: Int,
        quantity: Quantity,
    ): DataResponse<Unit> {
        val request = AddCartItemRequest(productId, quantity.count)
        return cartItemService.postCartItem(addCartItemRequest = request).executeForDataResponse()
    }

    override fun increaseQuantity(productId: Int) =
        findByProductId(productId).map { cartItem ->
            changeQuantity(cartItem.id, cartItem.quantity.inc())
        }.onFailure { code, error ->
            if (code == NULL_BODY_ERROR_CODE || code == 400) {
                val request = AddCartItemRequest(productId, 1)
                cartItemService.postCartItem(addCartItemRequest = request).execute()
            }
        }.chain { it }

    override fun decreaseQuantity(productId: Int) =
        findByProductId(productId).map { cartItem ->
            changeQuantity(cartItem.id, cartItem.quantity.dec())
        }.chain { it }

    override fun changeQuantity(
        cartItemId: Int,
        quantity: Quantity,
    ) = cartItemService.patchCartItemQuantity(
        id = cartItemId,
        quantity = CartItemQuantityRequest(quantity.count),
    ).executeForDataResponse()

    override fun deleteCartItem(cartItemId: Int) = cartItemService.deleteCartItem(id = cartItemId).executeForDataResponse()

    override fun findByProductId(productId: Int): DataResponse<CartItem> =
        findAll().map { wholeList ->
            wholeList.find { it.product.id == productId }
        }

    override fun findRange(
        page: Int,
        pageSize: Int,
    ): DataResponse<List<CartItem>> =
        cartItemService.requestCartItems(page = page, size = pageSize).executeForDataResponse()
            .map { cartResponse ->
                cartResponse.toCartItems()
            }

    override fun totalCartItemCount(): DataResponse<Int> =
        cartItemService.requestCartQuantityCount().executeForDataResponse().map {
            it.quantity
        }
}

package com.example.data.datasource.remote

import com.example.data.datasource.remote.retrofit.model.request.CartItemQuantityRequest
import com.example.data.datasource.remote.retrofit.model.response.cart.toCartItems
import com.example.data.datasource.remote.retrofit.service.CartItemService
import com.example.domain.datasource.CartDataSource
import com.example.domain.datasource.DataResponse
import com.example.domain.datasource.chain
import com.example.domain.datasource.map
import com.example.domain.model.CartItem
import com.example.domain.model.Quantity

class RemoteCartDataSource(
    private val cartItemService: CartItemService,
) : CartDataSource {
    override fun findAll(): DataResponse<List<CartItem>> =
        cartItemService.requestCartQuantityCount().map {
            cartItemService.requestCartItems(page = 0, size = it.quantity)
        }.chain {
            it.toCartItems()
        }

    override fun increaseQuantity(productId: Int) {
        findByProductId(productId).map { cartItem ->
            changeQuantity(cartItem.productId, cartItem.quantity.inc())
        }
    }

    override fun decreaseQuantity(productId: Int) {
        findByProductId(productId).map { cartItem ->
            changeQuantity(cartItem.productId, cartItem.quantity.dec())
        }
    }

    override fun changeQuantity(
        productId: Int,
        quantity: Quantity,
    ) = cartItemService.patchCartItemQuantity(
        id = productId,
        quantity = CartItemQuantityRequest(quantity.count),
    )

    override fun deleteByProductIdCartItem(productId: Int) = cartItemService.deleteCartItem(id = productId)

    override fun findByProductId(productId: Int): DataResponse<CartItem> =
        findAll().map { wholeList ->
            wholeList.find { it.productId == productId }
        }

    override fun findRange(
        page: Int,
        pageSize: Int,
    ): DataResponse<List<CartItem>> =
        cartItemService.requestCartItems(page = page, size = pageSize).map { cartResponse ->
            cartResponse.toCartItems()
        }

    override fun totalCartItemCount(): DataResponse<Int> =
        cartItemService.requestCartQuantityCount().map {
            it.quantity
        }
}

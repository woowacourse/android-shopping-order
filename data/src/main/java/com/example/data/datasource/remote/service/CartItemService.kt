package com.example.data.datasource.remote.service

import com.example.data.datasource.remote.model.request.AddCartItemRequest
import com.example.data.datasource.remote.model.request.CartItemQuantityRequest
import com.example.data.datasource.remote.model.response.cart.CartResponse
import com.example.data.datasource.remote.model.response.count.CountResponse
import com.example.domain.datasource.DataResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CartItemService {
    @GET("/cart-items")
    fun requestCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
    ): Call<DataResponse<CartResponse>>

    @GET("/cart-items/counts")
    fun requestCartQuantityCount(): Call<DataResponse<CountResponse>>

    @POST("/cart-items")
    fun postCartItem(
        @Body addCartItemRequest: AddCartItemRequest,
    ): Call<DataResponse<Unit>>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Path("id") id: Int = 0,
    ): Call<DataResponse<Unit>>

    @PATCH("/cart-items/{id}")
    fun patchCartItemQuantity(
        @Path("id") id: Int = 0,
        @Body quantity: CartItemQuantityRequest,
    ): Call<DataResponse<Unit>>
}

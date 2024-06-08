package com.example.data.datasource.remote.retrofit.service

import com.example.data.datasource.remote.retrofit.model.request.AddCartItemRequest
import com.example.data.datasource.remote.retrofit.model.request.CartItemQuantityRequest
import com.example.data.datasource.remote.retrofit.model.response.cart.CartResponse
import com.example.data.datasource.remote.retrofit.model.response.count.CountResponse
import com.example.domain.datasource.DataResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CartItemService {
    @GET("/cart-items")
    fun requestCartItems(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
    ): Call<DataResponse<CartResponse>>

    @GET("/cart-items/counts")
    fun requestCartQuantityCount(
        @Header("accept") accept: String = "*/*",
    ): Call<DataResponse<CountResponse>>

    @POST("/cart-items")
    fun postCartItem(
        @Header("accept") accept: String = "*/*",
        @Body addCartItemRequest: AddCartItemRequest,
    )

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int = 0,
    )

    @PATCH("/cart-items/{id}")
    fun patchCartItemQuantity(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int = 0,
        @Body quantity: CartItemQuantityRequest,
    )
}

package com.example.data.repository

import com.example.domain.datasource.CartDataSource

class DefaultCartRepository(
    private val dataSource: CartDataSource,
) {
    /*
     fun findByProductId(
         productId: Int,
         totalItemCount: Int,
         dataCallback: DataCallback<CartItem?>,
     ) {
         productService.requestCartItems(page = 0, size = totalItemCount)
             .enqueue(
                 object : Callback<CartResponse> {
                     override fun onResponse(
                         call: Call<CartResponse>,
                         response: Response<CartResponse>,
                     ) {
                         if (response.isSuccessful) {
                             val body = response.body() ?: return
                             dataCallback.onSuccess(
                                 body.toCartItems().find {
                                     it.productId == productId
                                 },
                             )
                         }
                     }

                     override fun onFailure(
                         call: Call<CartResponse>,
                         t: Throwable,
                     ) {
                         dataCallback.onFailure(t)
                     }
                 },
             )
     }

     fun getAllCartItem(
         totalItemCount: Int,
         dataCallback: DataCallback<List<CartItem>>,
     ) {
         getCartItems(0, totalItemCount, dataCallback)
     }

     fun getCartItems(
         page: Int,
         pageSize: Int,
         dataCallback: DataCallback<List<CartItem>>,
     ) {
         productService.requestCartItems(page = page, size = pageSize)
             .enqueue(
                 object : Callback<CartResponse> {
                     override fun onResponse(
                         call: Call<CartResponse>,
                         response: Response<CartResponse>,
                     ) {
                         if (response.isSuccessful) {
                             val body = response.body() ?: return
                             dataCallback.onSuccess(body.toCartItems())
                         }
                     }

                     override fun onFailure(
                         call: Call<CartResponse>,
                         t: Throwable,
                     ) {
                         dataCallback.onFailure(t)
                     }
                 },
             )
     }

     fun deleteCartItem(
         id: Int,
         dataCallback: DataCallback<Unit>,
     ) {
         productService.deleteCartItem(id = id).enqueue(
             object : Callback<Unit> {
                 override fun onResponse(
                     call: Call<Unit>,
                     response: Response<Unit>,
                 ) {
                     if (response.isSuccessful) {
                         dataCallback.onSuccess(Unit)
                     }
                 }

                 override fun onFailure(
                     call: Call<Unit>,
                     t: Throwable,
                 ) {
                     dataCallback.onFailure(t)
                 }
             },
         )
     }

     fun setCartItemQuantity(
         id: Int,
         quantity: Quantity,
         dataCallback: DataCallback<Unit>,
     ) {
         productService.setCartItemQuantity(id = id, quantity = CartItemQuantityRequest(quantity.count))
             .enqueue(
                 object : Callback<Unit> {
                     override fun onResponse(
                         call: Call<Unit>,
                         response: Response<Unit>,
                     ) {
                         if (response.isSuccessful) {
                             dataCallback.onSuccess(Unit)
                             return
                         }
                     }

                     override fun onFailure(
                         call: Call<Unit>,
                         t: Throwable,
                     ) {
                         dataCallback.onFailure(t)
                     }
                 },
             )
     }

     fun syncFindByProductId(
         productId: Int,
         totalItemCount: Int,
     ): CartItem? {
         var cartItem: CartItem? = null
         thread {
             val response = productService.requestCartItems(page = 0, size = totalItemCount).execute()
             val body = response.body()
             cartItem = body?.toCartItems()?.firstOrNull { productId == it.productId }
         }.join()
         return cartItem
     }

     fun syncGetCartQuantityCount(): Int {
         var cartQuantityCount = 0
         thread {
             val response = productService.requestCartQuantityCount().execute()
             val body = response.body()?.quantity
             cartQuantityCount = body ?: 0
         }.join()
         return cartQuantityCount
     }

     fun getCartQuantityCount(dataCallback: DataCallback<Int>) {
         productService.requestCartQuantityCount().enqueue(
             object : Callback<CountResponse> {
                 override fun onResponse(
                     call: Call<CountResponse>,
                     response: Response<CountResponse>,
                 ) {
                     if (response.isSuccessful) {
                         val body = response.body() ?: return
                         dataCallback.onSuccess(body.quantity)
                     }
                 }

                 override fun onFailure(
                     call: Call<CountResponse>,
                     t: Throwable,
                 ) {
                     dataCallback.onFailure(t)
                 }
             },
         )
     }

     fun getCartPageAttribute(
         page: Int,
         pageSize: Int,
         dataCallback: DataCallback<CartPageAttribute>,
     ) {
         productService.requestCartItems(page = page, size = pageSize)
             .enqueue(
                 object : Callback<CartResponse> {
                     override fun onResponse(
                         call: Call<CartResponse>,
                         response: Response<CartResponse>,
                     ) {
                         if (response.isSuccessful) {
                             val body = response.body() ?: return
                             val cartPage = body.toCartPage()
                             dataCallback.onSuccess(cartPage)
                         }
                     }

                     override fun onFailure(
                         call: Call<CartResponse>,
                         t: Throwable,
                     ) {
                         dataCallback.onFailure(t)
                     }
                 },
             )
     }

     fun addCartItem(
         productId: Int,
         quantity: Quantity = Quantity(1),
         dataCallback: DataCallback<Unit>,
     ) {
         productService.requestCartQuantityCount(
             addCartItemRequest =
                 AddCartItemRequest(
                     productId,
                     quantity.count,
                 ),
         ).enqueue(
             object : Callback<Unit> {
                 override fun onResponse(
                     call: Call<Unit>,
                     response: Response<Unit>,
                 ) {
                     if (response.isSuccessful) {
                         dataCallback.onSuccess(Unit)
                     }
                 }

                 override fun onFailure(
                     call: Call<Unit>,
                     t: Throwable,
                 ) {
                     dataCallback.onFailure(t)
                 }
             },
         )
     }
     */
}

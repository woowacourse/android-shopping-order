package com.example.data.repository

import com.example.data.datasource.remote.RemoteOrderDataSource

class DefaultOrderRepository(
    private val dataSource: RemoteOrderDataSource,
) {
    /*
    fun createOrder(
        cartItemIds: List<Int>,
    ) {
        productService.requestCreateOrder(createOrderRequest = CreateOrderRequest(cartItemIds))
            .enqueue(
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

package woowacourse.shopping.data.repository

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.cartitem.Content
import woowacourse.shopping.data.dto.cartitem.ProductResponse
import woowacourse.shopping.data.dto.cartitem.Quantity
import woowacourse.shopping.data.dto.cartitem.UpdateCartItemRequest
import woowacourse.shopping.data.service.CartItemService
import woowacourse.shopping.data.service.RetrofitProductService
import woowacourse.shopping.product.catalog.ProductUiModel

class RemoteCartProductRepositoryImpl : CartProductRepository {
    val retrofitService = RetrofitProductService.INSTANCE.create(CartItemService::class.java)

    override fun insertCartProduct(
        cartProduct: ProductUiModel,
        callback: (ProductUiModel) -> Unit,
    ) {
        retrofitService
            .postCartItems(
                request = UpdateCartItemRequest(
                    productId = cartProduct.id,
                    quantity = cartProduct.quantity,
                )
            ).enqueue(
                object : Callback<Void> {
                    override fun onResponse(
                        call: Call<Void>,
                        response: Response<Void>,
                    ) {
                        if (response.isSuccessful) {
                            val locationHeader = response.headers()["location"]
                            val id = locationHeader?.substringAfterLast("/")?.toIntOrNull()
                            callback(cartProduct.copy(cartItemId = id))
                            println("생성된 cartItem id: $id")
                        } else {
                            println("실패한 응답: ${response.code()}")
                        }
                    }

                    override fun onFailure(
                        call: Call<Void>,
                        t: Throwable,
                    ) {
                        println("에러 발생: $t")
                    }
                },
            )
    }

    override fun deleteCartProduct(cartProduct: ProductUiModel) {
        retrofitService.deleteCartItem(productId = cartProduct.id)
    }

    override fun getCartProductsInRange(
        currentPage: Int,
        pageSize: Int,
        callback: (List<ProductUiModel>) -> Unit,
    ) {
        retrofitService
            .requestCartItems(
                page = currentPage,
                size = pageSize,
            ).enqueue(
                object : Callback<ProductResponse> {
                    override fun onResponse(
                        call: Call<ProductResponse>,
                        response: Response<ProductResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val body: ProductResponse? = response.body()
                            val content: List<Content> = body?.content ?: return
                            val products: List<ProductUiModel> =
                                content.map {
                                    ProductUiModel(
                                        id = it.product.id.toInt(),
                                        imageUrl = it.product.imageUrl,
                                        name = it.product.name,
                                        price = it.product.price,
                                        cartItemId = it.id.toInt(),
                                    )
                                }
                            callback(products)
                            println("body : $body")
                        }
                    }

                    override fun onFailure(
                        call: Call<ProductResponse>,
                        t: Throwable,
                    ) {
                        println("error : $t")
                    }
                },
            )
    }

    override fun updateProduct(
        cartProduct: ProductUiModel,
        quantity: Int,
        callback: (Boolean) -> Unit,
    ) {
        retrofitService
            .patchCartItemQuantity(
                cartItemId = cartProduct.cartItemId!!,
                quantity = Quantity(quantity),
            ).enqueue(
                object : Callback<Void> {
                    override fun onResponse(
                        call: Call<Void>,
                        response: Response<Void>,
                    ) {
                        Log.d("test", "api 호출됨 ${response}")
                        if (response.isSuccessful) {
                            callback(true)
                            println("✅ 요청 성공 (status=${response.code()})")
                        } else {
                            callback(false)
                            println("✅ 요청 실패 (status=${response.code()})")
                        }
                    }

                    override fun onFailure(
                        call: Call<Void>,
                        t: Throwable,
                    ) {
                        callback(false)
                        println("error : $t")
                    }
                },
            )
    }

//    override fun getProductQuantity(id: Int, callback: (Int?) -> Unit) {
//        retrofitService.patchCartItemQuantity(
//            productId = cartProduct.id,
//            quantity = quantity,
//        ).enqueue(object : Callback<Quantity> {
//            override fun onResponse(
//                call: Call<Quantity>,
//                response: Response<Quantity>
//            ) {
//                if (response.isSuccessful) {
//                    val body: Quantity = response.body() ?: return
//                    val product = cartProduct.copy(
//                        quantity = body.value
//                    )
//                    callback(product)
//                    println("body : $body")
//                }
//            }
//
//            override fun onFailure(call: Call<Quantity>, t: Throwable) {
//                println("error : $t")
//            }
//        })
//    }

    override fun getAllProductsSize(callback: (Int) -> Unit) {
//        TODO("Not yet implemented")
    }

    override fun getCartItemSize(callback: (Int) -> Unit) {
        retrofitService.getCartItemsCount().enqueue(
            object : Callback<Quantity> {
                override fun onResponse(
                    call: Call<Quantity>,
                    response: Response<Quantity>,
                ) {
                    if (response.isSuccessful) {
                        val body: Quantity = response.body() ?: return
                        callback(body.value)
                        println("body : $body")
                    }
                }

                override fun onFailure(
                    call: Call<Quantity>,
                    t: Throwable,
                ) {
                    println("error : $t")
                }
            },
        )
    }

    override fun getTotalElements(callback: (Int) -> Unit) {
        retrofitService
            .requestCartItems(
                page = 0,
                size = 1,
            ).enqueue(
                object : Callback<ProductResponse> {
                    override fun onResponse(
                        call: Call<ProductResponse>,
                        response: Response<ProductResponse>,
                    ) {
                        if (response.isSuccessful) {
                            Log.d("test", "하잇 ${response.body()}")
                            val body: ProductResponse = response.body() ?: return
                            val totalElements = body.totalElements.toInt()
                            callback(totalElements)
                            println("body : $body")
                        }
                    }

                    override fun onFailure(
                        call: Call<ProductResponse>,
                        t: Throwable,
                    ) {
                        Log.d("test", "실패 $t")
                        println("error : $t")
                    }
                },
            )
    }

    override fun getCartProducts(
        totalElements: Int,
        callback: (List<ProductUiModel>) -> Unit,
    ) {
        retrofitService
            .requestCartItems(
                page = 0,
                size = totalElements,
            ).enqueue(
                object : Callback<ProductResponse> {
                    override fun onResponse(
                        call: Call<ProductResponse>,
                        response: Response<ProductResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val body: ProductResponse = response.body() ?: return
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
                            callback(products)
                            println("body : $body")
                        }
                    }

                    override fun onFailure(
                        call: Call<ProductResponse>,
                        t: Throwable,
                    ) {
                        println("error : $t")
                    }
                },
            )
    }
}

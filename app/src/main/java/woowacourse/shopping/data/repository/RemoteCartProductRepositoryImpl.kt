package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.cartitem.Content
import woowacourse.shopping.data.dto.cartitem.ProductResponse
import woowacourse.shopping.data.dto.cartitem.Quantity
import woowacourse.shopping.data.service.CartItemService
import woowacourse.shopping.data.service.RetrofitProductService
import woowacourse.shopping.product.catalog.ProductUiModel

class RemoteCartProductRepositoryImpl : CartProductRepository {
    val retrofitService = RetrofitProductService.INSTANCE.create(CartItemService::class.java)

    override fun insertCartProduct(cartProduct: ProductUiModel) {
        retrofitService.postCartItems(productId = cartProduct.id, quantity = cartProduct.quantity)
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
        callback: (ProductUiModel?) -> Unit,
    ) {
        retrofitService
            .patchCartItemQuantity(
                productId = cartProduct.id,
                quantity = quantity,
            ).enqueue(
                object : Callback<Quantity> {
                    override fun onResponse(
                        call: Call<Quantity>,
                        response: Response<Quantity>,
                    ) {
                        if (response.isSuccessful) {
                            val body: Quantity = response.body() ?: return
                            val product =
                                cartProduct.copy(
                                    quantity = body.value,
                                )
                            callback(product)
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
                size = 0,
            ).enqueue(
                object : Callback<ProductResponse> {
                    override fun onResponse(
                        call: Call<ProductResponse>,
                        response: Response<ProductResponse>,
                    ) {
                        if (response.isSuccessful) {
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

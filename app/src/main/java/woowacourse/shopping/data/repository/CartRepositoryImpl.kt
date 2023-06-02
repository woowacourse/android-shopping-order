package woowacourse.shopping.data.repository

import android.util.Log
import com.example.domain.model.CartProduct
import com.example.domain.repository.CartRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.datasource.remote.shoppingcart.ShoppingCartDataSource
import woowacourse.shopping.data.remote.request.CartProductDTO
import woowacourse.shopping.mapper.toDomain
import java.io.IOException

class CartRepositoryImpl(
    private val shoppingCartDataSource: ShoppingCartDataSource,
) : CartRepository {
    private val _productInCart = mutableListOf<CartProduct>()
    private var isCartDataCached = false
    override fun getAllProductInCart(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        if (isCartDataCached) {
            onSuccess(_productInCart)
            return
        }

        shoppingCartDataSource.getAllProductInCart().enqueue(
            object : Callback<List<CartProductDTO>> {
                override fun onResponse(
                    call: Call<List<CartProductDTO>>,
                    response: Response<List<CartProductDTO>>,
                ) {
                    onSuccess(response.body()!!.map { it.toDomain() })
                }

                override fun onFailure(call: Call<List<CartProductDTO>>, t: Throwable) {
                    onFailure(IOException("Response unsuccessful"))
                }
            },
        )
    }

    override fun insert(
        id: Long,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        shoppingCartDataSource.postProductToCart(id).enqueue(
            object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        onFailure(IOException("Response unsuccessful"))
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    onFailure(IOException("Response unsuccessful"))
                }
            },
        )
    }

    override fun updateCount(
        id: Long,
        count: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        shoppingCartDataSource.patchProductCount(id, count).enqueue(
            object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        onFailure(IOException("Response unsuccessful"))
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFailure(IOException("Response unsuccessful"))
                }
            },
        )
    }

    override fun remove(id: Long, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        shoppingCartDataSource.deleteProductInCart(id).enqueue(
            object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        onFailure(IOException("Response unsuccessful"))
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFailure(IOException("Response unsuccessful"))
                }
            },
        )
    }

    override fun findById(id: Long, onSuccess: (CartProduct?) -> Unit) {
        if (!isCartDataCached) {
            getAllProductInCart(
                onSuccess = { cartProducts ->
                    val cartProduct = cartProducts.find { it.product.id == id }
                    onSuccess(cartProduct)
                },
                onFailure = { exception ->
                    Log.e("CartRepositoryImpl", exception.message.toString())
                },
            )
        } else {
            val cartProduct = _productInCart.find { it.product.id == id }
            onSuccess(cartProduct)
        }
    }

    override fun getSubList(offset: Int, step: Int, onSuccess: (List<CartProduct>) -> Unit) {
        if (!isCartDataCached) {
            val limitedProducts = _productInCart.subList(
                offset.coerceAtMost(_productInCart.size),
                (offset + step).coerceAtMost(_productInCart.size),
            )
            onSuccess(limitedProducts)
        } else {
            getAllProductInCart(
                onSuccess = { cartProducts ->
                    val limitedProducts = cartProducts.subList(
                        offset.coerceAtMost(cartProducts.size),
                        (offset + step).coerceAtMost(cartProducts.size),
                    )
                    onSuccess(limitedProducts)
                },
                onFailure = { exception ->
                    Log.e("ProductRepositoryImpl", "getMoreProducts: $exception")
                },
            )
        }
        getAllProductInCart(
            onSuccess = { cartProducts ->
                val limitedProducts = cartProducts.subList(
                    offset.coerceAtMost(cartProducts.size),
                    (offset + step).coerceAtMost(cartProducts.size),
                )
                onSuccess(limitedProducts)
            },
            onFailure = { exception ->
                Log.e("ProductRepositoryImpl", "getMoreProducts: $exception")
            },
        )
    }
}

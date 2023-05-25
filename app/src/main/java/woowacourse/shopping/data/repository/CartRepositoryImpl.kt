package woowacourse.shopping.data.repository

import android.os.Looper
import com.example.domain.model.CartProduct
import com.example.domain.repository.CartRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import woowacourse.shopping.data.datasource.remote.shoppingcart.ShoppingCartDataSource
import java.io.IOException

class CartRepositoryImpl(
    private val shoppingCartDataSource: ShoppingCartDataSource,
) : CartRepository {
    private val productInCart = mutableListOf<CartProduct>()
    override fun getAllProductInCart(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        shoppingCartDataSource.getAllProductInCart().enqueue(
            createResponseCallback(onSuccess, onFailure),
        )
    }

    override fun insert(
        cartProduct: CartProduct,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        shoppingCartDataSource.postProductToCart(cartProduct.product.id).enqueue(
            createResponseCallback(onSuccess, onFailure),
        )
    }

    override fun remove(
        id: Long,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        shoppingCartDataSource.deleteProductInCart(id).enqueue(
            createResponseCallback(onSuccess, onFailure),
        )
    }

    override fun updateCount(
        id: Long,
        count: Int,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        shoppingCartDataSource.patchProductCount(id, count).enqueue(
            createResponseCallback(onSuccess, onFailure),
        )
    }

    override fun findById(id: Long): CartProduct? {
        // 프로퍼티에서 가져옴
        return null
    }

    override fun getSubList(offset: Int, step: Int): List<CartProduct> {
        // 프로퍼티에서 가져옴

        return productInCart
    }

    private inline fun <reified T> createResponseCallback(
        crossinline onSuccess: (T) -> Unit,
        crossinline onFailure: (Exception) -> Unit,
    ): Callback {
        val handler = android.os.Handler(Looper.getMainLooper())
        return object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Thread {
                        val result = parseToObject<T>(response.body?.string())
                        handler.post {
                            onSuccess.invoke(result)
                        }
                    }.start()
                    return
                }
                handler.post {
                    onFailure.invoke(Exception("Response unsuccessful"))
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                handler.post {
                    onFailure.invoke(e)
                }
            }
        }
    }

    private inline fun <reified T> parseToObject(responseBody: String?): T {
        return Gson().fromJson(responseBody, object : TypeToken<T>() {}.type)
    }
}

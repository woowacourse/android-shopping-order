package woowacourse.shopping.data.repository

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import woowacourse.shopping.data.datasource.impl.HandlerProductDataSource
import woowacourse.shopping.data.remote.api.ApiResult
import woowacourse.shopping.data.remote.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseProductsGetDto
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

class HandlerProductRepository(private val dataSource: HandlerProductDataSource = HandlerProductDataSource()) :
    ProductRepository {
    override fun getProducts(
        page: Int,
        size: Int,
    ): List<Product> {
        val response: MutableLiveData<List<Product>> = MutableLiveData()

        val handler =
            Handler(Looper.getMainLooper()) { msg ->
                when (val apiResult = msg.obj) {
                    is ApiResult.Success ->
                        response.value =
                            (apiResult.result as ResponseProductsGetDto).content.map { it.toDomain() }

                    is ApiResult.Fail -> throw IllegalStateException()
                    is ApiResult.Error -> throw apiResult.e
                }
                true
            }

        thread {
            dataSource.getProductsByOffset(page, size, handler)
        }

        return response.value ?: emptyList()
    }

    override fun find(id: Long): Product {
        val response: MutableLiveData<Product> = MutableLiveData()

        val handler =
            Handler(Looper.getMainLooper()) { msg ->
                when (val apiResult = msg.obj) {
                    is ApiResult.Success -> {
                        val product = (apiResult.result as ResponseProductIdGetDto)
                        response.value =
                            Product(
                                id = product.id,
                                imageUrl = product.imageUrl,
                                name = product.name,
                                price = product.price,
                                category = product.category,
                            )
                    }

                    is ApiResult.Fail -> throw IllegalStateException()
                    is ApiResult.Error -> throw apiResult.e
                }
                true
            }

        thread {
            dataSource.getProductsById(id, handler)
        }

        return response.value ?: Product(0, "", "", 0, "")
    }

    override fun productsByCategory(category: String): List<Product> {
        val response: MutableLiveData<List<Product>> = MutableLiveData()

        thread {
            response.value = getProducts(0, 300).filter { it.category == category }.toMutableList()
        }

        return response.value ?: emptyList()
    }
    private fun ResponseProductsGetDto.Product.toDomain(): Product =
        Product(
            id = id,
            imageUrl = imageUrl,
            name = name,
            price = price,
            category = category,
        )

}

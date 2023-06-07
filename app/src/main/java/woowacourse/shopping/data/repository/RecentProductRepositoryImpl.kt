package woowacourse.shopping.data.repository

import com.example.domain.model.BaseResponse
import com.example.domain.model.Product
import com.example.domain.model.RecentProduct
import com.example.domain.repository.RecentProductRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dataSource.local.RecentDao
import woowacourse.shopping.data.dataSource.remote.ProductService
import woowacourse.shopping.data.model.WooWaError
import woowacourse.shopping.data.model.dto.response.ProductDto
import woowacourse.shopping.data.model.entity.RecentProductEntity
import woowacourse.shopping.data.util.responseParseCustomError
import java.time.LocalDateTime
import java.time.ZoneOffset

class RecentProductRepositoryImpl(
    private val recentDao: RecentDao,
    private val productService: ProductService,
) : RecentProductRepository {
    override fun fetchAllRecentProduct(callBack: (BaseResponse<List<RecentProduct>>) -> Unit) {
        kotlin.runCatching { recentDao.selectAllRecent() }
            .onSuccess { getProductsInfo(it, callBack) }
            .onFailure { responseParseError(callBack) }
    }

    private fun getProductsInfo(
        recentEntities: List<RecentProductEntity>,
        callBack: (BaseResponse<List<RecentProduct>>) -> Unit,
    ) {
        val ids = recentEntities.map { it.productId }.joinToString(",", "", "")
        productService.getProductsById(ids).enqueue(object : Callback<List<ProductDto>> {
            override fun onResponse(
                call: Call<List<ProductDto>>,
                response: Response<List<ProductDto>>
            ) {
                if (response.isSuccessful.not())
                    return responseParseCustomError(response.errorBody(), callBack)

                val products = response.body() ?: emptyList()
                val recentProducts = mutableListOf<RecentProduct>()

                recentEntities.forEach { recentProductEntity ->
                    val product =
                        products.find { it.id == recentProductEntity.productId } ?: return@forEach
                    val shownDateTime = LocalDateTime.ofEpochSecond(
                        recentProductEntity.dateTimeMills,
                        0,
                        ZoneOffset.UTC
                    )
                    recentProducts.add(RecentProduct(product.toDomain(), shownDateTime))
                }

                callBack(BaseResponse.SUCCESS(recentProducts))
            }

            override fun onFailure(call: Call<List<ProductDto>>, t: Throwable) {
                callBack(BaseResponse.NETWORK_ERROR())
            }
        })
    }

    override fun addRecentProduct(
        product: Product,
        callBack: (BaseResponse<Product>) -> Unit
    ) {
        kotlin.runCatching { recentDao.putRecentProduct(product) }
            .onSuccess { callBack(BaseResponse.SUCCESS(product)) }
            .onFailure { responseParseError(callBack) }
    }

    private inline fun <reified T> responseParseError(callBack: (BaseResponse<T>) -> Unit) {
        val error = WooWaError.findErrorByCode(null)
        callBack(BaseResponse.FAILED(error.code, error.message))
    }
}

package woowacourse.shopping.data.repository

import com.example.domain.model.Product
import com.example.domain.model.RecentProduct
import com.example.domain.repository.RecentProductRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dataSource.recent.RecentDao
import woowacourse.shopping.data.dataSource.remote.product.ProductService
import woowacourse.shopping.data.model.RecentProductEntity
import woowacourse.shopping.data.model.dto.response.ProductDto
import java.time.LocalDateTime
import java.time.ZoneOffset

class RecentProductRepositoryImpl(
    private val recentDao: RecentDao,
    private val productService: ProductService,
) : RecentProductRepository {
    override fun fetchAllRecentProduct(onSuccess: (List<RecentProduct>) -> Unit, onFailure: () -> Unit) {
        kotlin.runCatching { recentDao.selectAllRecent() }
            .onSuccess { getProductsInfo(it, onSuccess, onFailure) }
            .onFailure { onFailure() }
    }

    private fun getProductsInfo(
        recentEntities: List<RecentProductEntity>,
        onSuccess: (List<RecentProduct>) -> Unit,
        onFailure: () -> Unit
    ) {
        val ids = recentEntities.map { it.productId }.joinToString(",", "", "")
        productService.getProductsById(ids).enqueue(object : Callback<List<ProductDto>> {
            override fun onResponse(
                call: Call<List<ProductDto>>,
                response: Response<List<ProductDto>>
            ) {
                if (response.isSuccessful.not()) return onFailure()
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

                onSuccess(recentProducts)
            }

            override fun onFailure(call: Call<List<ProductDto>>, t: Throwable) {
                onFailure()
            }
        })
    }

    override fun addRecentProduct(
        product: Product,
        onSuccess: (product: Product) -> Unit,
        onFailure: () -> Unit
    ) {
        kotlin.runCatching { recentDao.putRecentProduct(product) }
            .onSuccess { onSuccess(product) }
            .onFailure { onFailure() }
    }
}

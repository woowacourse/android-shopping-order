package woowacourse.shopping.data.recentlyviewedproduct

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.util.Log
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.database.ProductContract
import woowacourse.shopping.data.product.ProductDto
import woowacourse.shopping.data.product.ProductRemoteService
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.RecentlyViewedProduct
import woowacourse.shopping.repository.RecentlyViewedProductRepository
import woowacourse.shopping.utils.ServerConfiguration
import java.time.LocalDateTime

class RecentlyViewedProductRemoteRepository(private val db: SQLiteDatabase) :
    RecentlyViewedProductRepository {

    private val productRemoteService: ProductRemoteService = Retrofit.Builder()
        .baseUrl(ServerConfiguration.host.url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ProductRemoteService::class.java)

    override fun save(
        product: Product,
        viewedTime: LocalDateTime,
        onFinish: (RecentlyViewedProduct) -> Unit
    ) {
        deleteRecentlyViewedProductIfSameProductExists(product)

        val value = ContentValues().apply {
            put(
                ProductContract.RecentlyViewedProductEntry.COLUMN_NAME_PRODUCT_ID, product.id
            )
            put(
                ProductContract.RecentlyViewedProductEntry.COLUMN_NAME_VIEWED_TIME,
                viewedTime.toString()
            )
        }
        val id = db.insert(ProductContract.RecentlyViewedProductEntry.TABLE_NAME, null, value)
        onFinish(RecentlyViewedProduct(id, product, viewedTime))
    }

    private fun deleteRecentlyViewedProductIfSameProductExists(product: Product) {
        val selection = "${ProductContract.RecentlyViewedProductEntry.COLUMN_NAME_PRODUCT_ID} = ?"
        val selectionArgs = arrayOf(product.id.toString())
        db.delete(ProductContract.RecentlyViewedProductEntry.TABLE_NAME, selection, selectionArgs)
    }

    override fun findFirst10OrderByViewedTimeDesc(onFinish: (List<RecentlyViewedProduct>) -> Unit) {
        getProducts { products ->
            val recentlyViewedProducts = mutableListOf<RecentlyViewedProduct>()
            val limit = 10
            val cursor = db.rawQuery(
                """
            SELECT * FROM ${ProductContract.RecentlyViewedProductEntry.TABLE_NAME}
            ORDER BY ${ProductContract.RecentlyViewedProductEntry.COLUMN_NAME_VIEWED_TIME} DESC
            LIMIT $limit
                """.trimIndent(),
                null
            )

            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
                val productId =
                    cursor.getLong(cursor.getColumnIndexOrThrow(ProductContract.RecentlyViewedProductEntry.COLUMN_NAME_PRODUCT_ID))
                val viewedTime =
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.RecentlyViewedProductEntry.COLUMN_NAME_VIEWED_TIME))

                recentlyViewedProducts.add(
                    RecentlyViewedProduct(
                        id, products[productId] ?: continue, LocalDateTime.parse(viewedTime)
                    )
                )
            }

            cursor.close()
            onFinish(recentlyViewedProducts)
        }
    }

    private fun getProducts(onFinish: (Map<Long, Product>) -> Unit) {
        productRemoteService.requestProducts()
            .enqueue(object : retrofit2.Callback<List<ProductDto>> {
                override fun onResponse(
                    call: Call<List<ProductDto>>,
                    response: Response<List<ProductDto>>
                ) {
                    Log.d("THOMAS", "응답 코드: ${response.code()}")
                    if (response.isSuccessful.not()) return
                    val products = response.body()!!
                        .map { it.toDomain() }
                        .associateBy { it.id }
                    onFinish(products)
                }

                override fun onFailure(call: Call<List<ProductDto>>, t: Throwable) {
                }
            })
    }
}

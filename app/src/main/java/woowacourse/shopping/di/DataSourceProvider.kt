package woowacourse.shopping.di

import woowacourse.shopping.data.datasource.CatalogRemoteDataSource
import woowacourse.shopping.data.datasource.CatalogRemoteDataSourceImpl
import woowacourse.shopping.data.service.ProductService
import woowacourse.shopping.data.service.RetrofitProductService

object DataSourceProvider {
    private val retrofitService: ProductService =
        RetrofitProductService.INSTANCE.create(ProductService::class.java)

    fun provideProductRemoteDataSource(): CatalogRemoteDataSource = CatalogRemoteDataSourceImpl(retrofitService)
}

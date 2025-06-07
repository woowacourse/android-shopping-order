package woowacourse.shopping.di

import retrofit2.create
import woowacourse.shopping.data.datasource.CartRemoteDataSource
import woowacourse.shopping.data.datasource.CartRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.CatalogRemoteDataSource
import woowacourse.shopping.data.datasource.CatalogRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.CouponDataSource
import woowacourse.shopping.data.datasource.CouponDataSourceImpl
import woowacourse.shopping.data.service.CartItemService
import woowacourse.shopping.data.service.CouponService
import woowacourse.shopping.data.service.ProductService
import woowacourse.shopping.data.service.RetrofitProductService

object DataSourceProvider {
    private val productService: ProductService =
        RetrofitProductService.INSTANCE.create(ProductService::class.java)

    private val cartItemService: CartItemService =
        RetrofitProductService.INSTANCE.create(CartItemService::class.java)

    private val couponServcie: CouponService =
        RetrofitProductService.INSTANCE.create(CouponService::class.java)

    fun provideProductRemoteDataSource(): CatalogRemoteDataSource = CatalogRemoteDataSourceImpl(productService)

    fun provideCartRemoteDataSource(): CartRemoteDataSource = CartRemoteDataSourceImpl(cartItemService)

    fun provideCouponRemoteDataSource(): CouponDataSource = CouponDataSourceImpl(couponServcie)
}

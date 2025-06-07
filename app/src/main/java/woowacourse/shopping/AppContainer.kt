package woowacourse.shopping

import android.content.Context
import woowacourse.shopping.data.NetworkResultHandler
import woowacourse.shopping.data.db.PetoMarketDatabase
import woowacourse.shopping.data.di.DataSourceModule
import woowacourse.shopping.data.di.DatabaseModule
import woowacourse.shopping.data.di.NetworkModule
import woowacourse.shopping.data.di.RepositoryModule
import woowacourse.shopping.data.di.RetrofitModule
import woowacourse.shopping.domain.coupon.CouponApplierFactory
import woowacourse.shopping.domain.coupon.CouponValidator

class AppContainer(
    context: Context,
) {
    private val networkResultHandler = NetworkResultHandler()
    private val db = PetoMarketDatabase.getInstance(context)

    private val networkModule = NetworkModule(RetrofitModule())
    private val databaseModule = DatabaseModule(db)
    private val dataSourceModule = DataSourceModule(databaseModule, networkModule, networkResultHandler)

    val repositoryModule = RepositoryModule(dataSourceModule)

    val couponValidator: CouponValidator = CouponValidator()

    val couponApplierFactory: CouponApplierFactory = CouponApplierFactory()
}

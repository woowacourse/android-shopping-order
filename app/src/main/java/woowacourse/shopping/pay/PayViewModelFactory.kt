package woowacourse.shopping.pay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.repository.PayRepositoryImpl
import woowacourse.shopping.data.source.CatalogProductDataSourceRemoteDataSource
import woowacourse.shopping.data.source.CouponRemoteDataSource
import woowacourse.shopping.data.source.OrderRemoteDataSource

class PayViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PayViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PayViewModel(
                payRepository = PayRepositoryImpl.initialize(
                    orderDataSource = OrderRemoteDataSource(),
                    couponDataSource = CouponRemoteDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

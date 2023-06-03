package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.userpointdata.UserPointInfoDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.UserPointInfo
import woowacourse.shopping.domain.repository.PointRepository

class PointRepositoryImpl(private val remoteUserPointInfoDataSource: UserPointInfoDataSource.Remote) :
    PointRepository {
    override fun getUserPointInfo(onReceived: (UserPointInfo) -> Unit) {
        remoteUserPointInfoDataSource.getUserPointInfo { onReceived(it.toDomain()) }
    }
}

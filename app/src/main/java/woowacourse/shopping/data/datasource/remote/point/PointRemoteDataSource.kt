package woowacourse.shopping.data.datasource.remote.point

import com.example.domain.model.point.PointInfo

interface PointRemoteDataSource {

    fun loadPoint(
        onSuccess: (PointInfo) -> Unit,
        onFailure: () -> Unit
    )
}

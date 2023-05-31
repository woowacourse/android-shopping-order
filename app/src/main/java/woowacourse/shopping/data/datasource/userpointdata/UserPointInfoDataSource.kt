package woowacourse.shopping.data.datasource.userpointdata

import woowacourse.shopping.data.model.DataUserPointInfo

interface UserPointInfoDataSource {
    interface Local
    interface Remote {
        fun getUserPointInfo(onReceived: (DataUserPointInfo) -> Unit)
    }
}

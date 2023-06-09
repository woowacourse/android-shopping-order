package woowacourse.shopping.data.database

import woowacourse.shopping.data.model.Server

fun getTableName(url: Server.Url): String =
    when (url) {
        Server.Url.BASE_URL_JENNA -> {
            url.name.substringAfterLast("URL_").lowercase()
        }
        Server.Url.BASE_URL_POI -> {
            url.name.substringAfterLast("URL_").lowercase()
        }
        Server.Url.BASE_URL_TORI -> {
            url.name.substringAfterLast("URL_").lowercase()
        }
    }

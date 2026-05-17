package woowacourse.shopping

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.data.local.database.DataBase
import woowacourse.shopping.data.local.database.MIGRATION_5_6
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        DataBase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate5To6() {
        // given
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dbFile = context.getDatabasePath(TEST_DB)
        if (dbFile.exists()) dbFile.delete()

        val factory = FrameworkSQLiteOpenHelperFactory()
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(TEST_DB)
            .callback(object : SupportSQLiteOpenHelper.Callback(5) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL("CREATE TABLE IF NOT EXISTS `purchase_products` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
                    db.execSQL("CREATE TABLE IF NOT EXISTS `recently_viewed_products` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
                }

                override fun onUpgrade(
                    db: SupportSQLiteDatabase,
                    oldVersion: Int,
                    newVersion: Int
                ) {
                }
            })
            .build()
        
        factory.create(config).writableDatabase.close()

        // when
        helper.runMigrationsAndValidate(TEST_DB, 6, true, MIGRATION_5_6)
    }
}

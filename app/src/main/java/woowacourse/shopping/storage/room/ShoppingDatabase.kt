package woowacourse.shopping.storage.room

import android.content.Context
import android.database.Cursor
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import woowacourse.shopping.storage.room.shoppingItem.ShoppingItemDao
import woowacourse.shopping.storage.room.shoppingItem.ShoppingItemEntity
import woowacourse.shopping.storage.room.shoppingcart.ShoppingCartDao
import woowacourse.shopping.storage.room.shoppingcart.ShoppingCartEntity

@Database(
    entities = [ShoppingItemEntity::class, ShoppingCartEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class ShoppingDatabase : RoomDatabase() {
    abstract fun shoppingItemDao(): ShoppingItemDao

    abstract fun shoppingCartDao(): ShoppingCartDao

    companion object {
        private const val DATABASE_NAME = "shopping.db"
        private val migration1To3 =
            object : Migration(1, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    rebuildRoomTables(db)
                }
            }
        private val migration2To3 =
            object : Migration(2, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    rebuildRoomTables(db)
                }
            }

        fun create(context: Context): ShoppingDatabase =
            Room
                .databaseBuilder(context, ShoppingDatabase::class.java, DATABASE_NAME)
                .addMigrations(migration1To3, migration2To3)
                .build()

        private fun rebuildRoomTables(db: SupportSQLiteDatabase) {
            db.execSQL("DROP TABLE IF EXISTS `visited_products`")
            rebuildShoppingItemsTable(db)
            rebuildShoppingCartItemsTable(db)
        }

        private fun rebuildShoppingItemsTable(db: SupportSQLiteDatabase) {
            val backupTableName = "shopping_items_backup"
            val oldColumns = renameTableToBackupIfExists(db, "shopping_items", backupTableName)

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `shopping_items` (
                    `product_id` INTEGER NOT NULL,
                    `title` TEXT NOT NULL,
                    `price` INTEGER NOT NULL,
                    `image_url` TEXT NOT NULL,
                    `quantity` INTEGER NOT NULL,
                    PRIMARY KEY(`product_id`)
                )
                """.trimIndent(),
            )

            if ("product_id" in oldColumns) {
                val titleExpression = oldColumns.stringExpression(columnName = "title", defaultValue = "''")
                val priceExpression = oldColumns.stringExpression(columnName = "price", defaultValue = "0")
                val imageUrlExpression = oldColumns.stringExpression(columnName = "image_url", defaultValue = "''")
                val quantityExpression = oldColumns.stringExpression(columnName = "quantity", defaultValue = "0")

                db.execSQL(
                    """
                    INSERT OR REPLACE INTO `shopping_items` (`product_id`, `title`, `price`, `image_url`, `quantity`)
                    SELECT `product_id`, $titleExpression, $priceExpression, $imageUrlExpression, $quantityExpression
                    FROM `$backupTableName`
                    """.trimIndent(),
                )
            }

            db.execSQL("DROP TABLE IF EXISTS `$backupTableName`")
        }

        private fun rebuildShoppingCartItemsTable(db: SupportSQLiteDatabase) {
            val backupTableName = "shopping_cart_items_backup"
            val oldColumns = renameTableToBackupIfExists(db, "shopping_cart_items", backupTableName)

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `shopping_cart_items` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `product_id` INTEGER NOT NULL,
                    FOREIGN KEY(`product_id`) REFERENCES `shopping_items`(`product_id`) ON DELETE CASCADE
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE UNIQUE INDEX IF NOT EXISTS `index_shopping_cart_items_product_id`
                ON `shopping_cart_items` (`product_id`)
                """.trimIndent(),
            )

            if ("product_id" in oldColumns) {
                val idExpression = oldColumns.stringExpression(columnName = "id", defaultValue = "NULL")
                db.execSQL(
                    """
                    INSERT OR IGNORE INTO `shopping_cart_items` (`id`, `product_id`)
                    SELECT $idExpression, `product_id`
                    FROM `$backupTableName`
                    WHERE `product_id` IN (SELECT `product_id` FROM `shopping_items`)
                    """.trimIndent(),
                )
            }

            db.execSQL("DROP TABLE IF EXISTS `$backupTableName`")
        }

        private fun renameTableToBackupIfExists(
            db: SupportSQLiteDatabase,
            tableName: String,
            backupTableName: String,
        ): Set<String> {
            if (!db.hasTable(tableName)) return emptySet()

            db.execSQL("DROP TABLE IF EXISTS `$backupTableName`")
            db.execSQL("ALTER TABLE `$tableName` RENAME TO `$backupTableName`")
            return db.getColumnNames(backupTableName)
        }

        private fun SupportSQLiteDatabase.hasTable(tableName: String): Boolean =
            query("SELECT name FROM sqlite_master WHERE type = 'table' AND name = '$tableName'").use(Cursor::moveToFirst)

        private fun SupportSQLiteDatabase.getColumnNames(tableName: String): Set<String> =
            query("PRAGMA table_info(`$tableName`)").use { cursor ->
                val nameIndex = cursor.getColumnIndexOrThrow("name")
                val columnNames = mutableSetOf<String>()
                while (cursor.moveToNext()) {
                    columnNames += cursor.getString(nameIndex)
                }
                columnNames
            }

        private fun Set<String>.stringExpression(
            columnName: String,
            defaultValue: String,
        ): String =
            if (columnName in this) {
                "COALESCE(`$columnName`, $defaultValue)"
            } else {
                defaultValue
            }
    }
}

package com.comtax.app.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE cuentas ADD COLUMN colorHex TEXT NOT NULL DEFAULT '#1565C0'")
    }
}

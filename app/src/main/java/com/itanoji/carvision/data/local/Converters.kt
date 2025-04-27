package com.itanoji.carvision.data.local

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter fun fromDate(date: Date?): Long? = date?.time
    @TypeConverter fun toDate(ts: Long?): Date? = ts?.let { Date(it) }
}

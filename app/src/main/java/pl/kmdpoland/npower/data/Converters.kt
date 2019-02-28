package pl.kmdpoland.npower.data

import androidx.room.TypeConverter
import java.util.*


class DateConverter {

    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return if (dateLong == null) null else Date(dateLong)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return (if (date == null) null else date.getTime())!!.toLong()
    }
}

class CoordsConverter {

    @TypeConverter
    fun toCoords(coords: String): Array<Double>? {
        return coords.split(";").map { it.toDouble() }.toTypedArray()
    }

    @TypeConverter
    fun fromCoords(coords: Array<Double>): String {
        return coords.joinToString(separator=";")
    }
}
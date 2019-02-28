package pl.kmdpoland.npower.data

import androidx.room.*
import io.reactivex.Flowable
import io.reactivex.Maybe
import java.util.*

@Entity
data class Visit (
    @PrimaryKey var avatar: String,
    @ColumnInfo(name = "first_name") var firstName: String,
    @ColumnInfo(name = "last_name") var lastName: String,
    @ColumnInfo(name = "start_date") var startTime: Date,
    @ColumnInfo(name = "address") var address: String,
    @ColumnInfo(name = "coordinates") var coordinates: Array<Double>){
}

@Dao
interface VisitDao {
    @Query("SELECT * FROM visit")
    fun getAll(): Maybe<Array<Visit>>

    @Query("SELECT * FROM visit WHERE avatar = :id ")
    fun loadSingleAsync(id: String): Maybe<Visit>

    @Query("SELECT * FROM visit WHERE avatar = :id ")
    fun loadSingle(id: String): Visit

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg users: Visit)

    @Delete
    fun delete(user: Visit)
}

@Database(entities = arrayOf(Visit::class), version = 1)
@TypeConverters(DateConverter::class, CoordsConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun visitDao(): VisitDao
}
package com.epmus.mobile.MongoDbService

// Base Realm Packages
import com.epmus.mobile.poseestimation.ExerciceStatistique
import com.epmus.mobile.ui.login.realmApp
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
// Realm Authentication Packages
import io.realm.mongodb.User
import io.realm.mongodb.Credentials

// MongoDB Service Packages
import io.realm.mongodb.mongo.MongoClient
import io.realm.mongodb.mongo.MongoDatabase
import io.realm.mongodb.mongo.MongoCollection
import io.realm.mongodb.sync.SyncConfiguration
// Utility Packages
import org.bson.Document
import org.bson.types.ObjectId
import java.lang.Math.abs
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask

open class historique(
    _patientId: String = "test",
    _programme_id: String = "Statistics",
    _date: String = "My Project",
    _duree: String = "defaultTime",
    _activite: RealmList<activite>? = null
) :
    RealmObject() {
    @PrimaryKey
    var _id: ObjectId = ObjectId()

    var patient_id = _patientId

    var date: String = _date

    var duree: String = _duree

    var programme_id: String = _programme_id

    var activite = _activite
}

@RealmClass(embedded = true)
open class activite(
    var nom: String? = null,
    var resultat: Int? = null,
    var repetition: Int? = null,
): RealmObject() {}


class MongoTransactions {

    lateinit var uiThreadRealm: Realm

    private val statsColl = realmApp.currentUser()?.getMongoClient("mongodb-atlas")?.getDatabase("iphysioBD-dev")?.getCollection("statistics")
    private val exercicesColl = realmApp.currentUser()?.getMongoClient("mongodb-atlas")?.getDatabase("iphysioBD-dev")?.getCollection("exercices")


    fun insertHistoryEntry(stats: ExerciceStatistique) {
        val dates = SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
        val timeEnd: Date? = dates.parse(stats.exerciceEndTime)
        val timeStart: Date? = dates.parse(stats.exerciceStartTime)
        val timeDiff: Long = abs(timeEnd!!.time - timeStart!!.time)

        var exerName = "NA"
        val tmpId = "5e728b24fa1bf23c20bdb3a4"

        val partitionValue: String = "My Project"
        val config = SyncConfiguration.Builder(realmApp.currentUser()!!, partitionValue)
            .build()

        uiThreadRealm = Realm.getInstance(config)

        val histoEntry : historique = historique(tmpId, exerName, stats.initStartTime!!, formatTime(timeDiff))

        val task : FutureTask<String> = FutureTask(BackgroundInsertEntry(uiThreadRealm, histoEntry), "test")
        val executorService: ExecutorService = Executors.newFixedThreadPool(2)
        executorService.execute(task)



        // val queryFilter: Document = Document("_id", stats.exerciceID)
        // val queryFilter: Document = Document("_id", tmpId)
//        val queryFilter: Document = Document("name", "Mouvement bras")
//        exercicesColl?.findOne(queryFilter)?.getAsync() {
//            if(it.isSuccess) {
//                exerName = it.get().toString()
//            }
//        }
//
//        val plant : Document = Document("name", exerName)
//            .append("date", stats.initStartTime)
//            .append("time", formatTime(timeDiff))
//
//
//
//        statsColl?.insertOne(plant)?.getAsync() {
//            if (it.isSuccess) {
//
//            } else {
//
//            }
//        }
    }

    class BackgroundInsertEntry(val thread: Realm, val histoEntry: historique) : Runnable {
        override fun run() {
            thread.executeTransaction { transactionRealm ->
                transactionRealm.insert(histoEntry)
            }
        }
    }

    private fun formatTime(time: Long): String {
        var cleanTime: String = ""
        var seconds: Long = time/1000

        if(seconds > 59) {
            val df = DecimalFormat("##")
            df.roundingMode = RoundingMode.FLOOR

            var minutes = df.format(seconds / 60)
            seconds %= 60

            cleanTime = minutes.toString() + "m " + seconds.toString() + "s"
        }

        else {
            cleanTime = seconds.toString() + "s"
        }

        return cleanTime
    }

}
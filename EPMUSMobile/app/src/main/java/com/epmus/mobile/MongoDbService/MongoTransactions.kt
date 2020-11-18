package com.epmus.mobile.MongoDbService

// Base Realm Packages
import com.epmus.mobile.HistoryActivity
import com.epmus.mobile.historyView
import com.epmus.mobile.poseestimation.ExerciceStatistique
import com.epmus.mobile.statistics
import com.epmus.mobile.ui.login.realmApp
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.kotlin.where
import io.realm.mongodb.User

import io.realm.mongodb.sync.SyncConfiguration

import org.bson.types.ObjectId
import java.lang.Math.abs
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask

class MongoTransactions {
    companion object {
        val config: SyncConfiguration
        lateinit var historyListener: RealmResults<historique>

        init {
            val user: User? = realmApp.currentUser()
            val partitionValue: String? = user?.id
            config = SyncConfiguration.Builder(user, partitionValue).build()
        }

        fun insertHistoryEntry(stats: ExerciceStatistique) {
            val tmpId = "5e728b24fa1bf23c20bdb3a4"
            var exerName = "NA"

            val dates = SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
            val timeEnd: Date? = dates.parse(stats.exerciceEndTime)
            val timeStart: Date? = dates.parse(stats.exerciceStartTime)
            val timeDiff: Long = abs(timeEnd!!.time - timeStart!!.time)

            val histoEntry =
                historique(tmpId, exerName, stats.initStartTime!!, formatTime(timeDiff))

            val task: FutureTask<String> =
                FutureTask(BackgroundInsertEntry(config, histoEntry), "test")
            val executorService: ExecutorService = Executors.newFixedThreadPool(2)
            executorService.execute(task)
        }

        fun addChangeListenerToRealm(realm: Realm) {
            historyListener = realm.where<historique>().findAllAsync()

            historyListener.addChangeListener { collection, changeSet ->

                statistics = realm.copyFromRealm(collection);

                if (historyView != null) {
                    historyView!!.adapter = HistoryActivity.SimpleItemRecyclerViewAdapter(statistics)
                }
            }
        }

        private fun formatTime(time: Long): String {
            var cleanTime: String = ""
            var seconds: Long = time / 1000

            if (seconds > 59) {
                val df = DecimalFormat("##")
                df.roundingMode = RoundingMode.FLOOR

                var minutes = df.format(seconds / 60)
                seconds %= 60

                cleanTime = minutes.toString() + "m " + seconds.toString() + "s"
            } else {
                cleanTime = seconds.toString() + "s"
            }

            return cleanTime
        }
    }

    class BackgroundInsertEntry(val config: SyncConfiguration, val histoEntry: historique) :
        Runnable {
        override fun run() {
            val realmInstance = Realm.getInstance(config)

            realmInstance.executeTransaction { transactionRealm ->
                transactionRealm.insert(histoEntry)
            }

            realmInstance.close()
        }
    }
}

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
) : RealmObject() {}
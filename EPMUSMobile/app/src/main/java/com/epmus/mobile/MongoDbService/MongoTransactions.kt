package com.epmus.mobile.MongoDbService

// Base Realm Packages
import com.epmus.mobile.HistoryActivity
import com.epmus.mobile.historyView
import com.epmus.mobile.poseestimation.ExerciceStatistique
import com.epmus.mobile.poseestimation.ExerciceType
import com.epmus.mobile.historic
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
        val user: User?
        lateinit var historyListener: RealmResults<historique>

        init {
            user = realmApp.currentUser()
            val partitionValue: String? = user?.id
            config = SyncConfiguration.Builder(user, partitionValue).build()
        }

        fun insertHistoryEntry(stats: ExerciceStatistique) {
            var exerName = stats.exerciceName
            val exerciceType = stats.exerciceType

            val exerciceTypeEnum = ExerciceType.getEnumValue(exerciceType)

            var nbrRepetitionOrHoldTime = stats.numberOfRepetition.last().toString()

            if (exerciceTypeEnum == ExerciceType.HOLD) {
                // TODO: Fix value
                nbrRepetitionOrHoldTime = stats.holdTime.last().toString()
            }


            val dates = SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
            val timeEnd: Date? = dates.parse(stats.exerciceEndTime)
            val timeStart: Date? = dates.parse(stats.exerciceStartTime)
            val timeDiff: Long = abs(timeEnd!!.time - timeStart!!.time)

            var initStartTime: String =""
            if(stats.initStartTime != null){
                initStartTime = stats.initStartTime!!
            }

            val histoEntry =
                historique(
                    exerName,
                    exerciceType,
                    initStartTime,
                    formatTime(timeDiff),
                    nbrRepetitionOrHoldTime
                )

            val task: FutureTask<String> =
                FutureTask(BackgroundInsertEntry(config, histoEntry), "test")
            val executorService: ExecutorService = Executors.newFixedThreadPool(2)
            executorService.execute(task)
        }

        fun addChangeListenerToRealm(realm: Realm) {
            historyListener = realm.where<historique>().findAllAsync()

            historyListener.addChangeListener { collection, changeSet ->

                historic = realm.copyFromRealm(collection);

                if (historyView != null) {
                    historyView!!.adapter =
                        HistoryActivity.SimpleItemRecyclerViewAdapter(historic)
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
    _exerciceName: String = "",
    _exerciceType: String = "",
    _date: String = "",
    _duree: String = "",
    _nbrRepetitionOrHoldTime: String = ""
) :
    RealmObject() {
    @PrimaryKey
    var _id: ObjectId = ObjectId()

    var date: String = _date

    var duree: String = _duree

    var exerciceName: String = _exerciceName

    var exerciceType: String = _exerciceType

    var nbrRepetitionOrHoldTime = _nbrRepetitionOrHoldTime
}

package com.epmus.mobile.MongoDbService

// Base Realm Packages
import com.epmus.mobile.*
import com.epmus.mobile.poseestimation.BodyPart
import com.epmus.mobile.poseestimation.ExerciceStatistique
import com.epmus.mobile.poseestimation.ExerciceType
import com.epmus.mobile.poseestimation.Movement
import com.epmus.mobile.program.ExerciceData
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
        var programmesList: MutableList<programmes> = mutableListOf()
        var exercicesList: MutableList<exercices> = mutableListOf()
        val configUserId: SyncConfiguration
        val configTempId: SyncConfiguration
        val configExercices: SyncConfiguration
        val user: User?
        lateinit var historyListener: RealmResults<historique>
        lateinit var programListener: RealmResults<programmes>
        lateinit var exercicesListener: RealmResults<exercices>

        init {
            user = realmApp.currentUser()
            val partitionValue: String? = user?.id
            configUserId = SyncConfiguration.Builder(user, partitionValue).build()
            configTempId = SyncConfiguration.Builder(user, "5e70fbbd362c587b9cc296e2").build()
            configExercices = SyncConfiguration.Builder(user, "exercices").build()
        }

        fun insertHistoryEntry(stats: ExerciceStatistique) {
            val exerciceType = stats.exerciceType
            val exerciceTypeEnum = ExerciceType.getEnumValue(exerciceType)

            var nbrRepetitionOrHoldTime = stats.numberOfRepetition.last().toString()

            if (exerciceTypeEnum == ExerciceType.HOLD) {
                nbrRepetitionOrHoldTime = stats.holdTime.last().toString()
            }

            val dates = SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
            val timeEnd: Date? = dates.parse(stats.exerciceEndTime)
            val timeStart: Date? = dates.parse(stats.exerciceStartTime)
            val timeDiff: Long = abs(timeEnd!!.time - timeStart!!.time)

            val histoEntry =
                historique(
                    stats.exerciceName,
                    exerciceType,
                    stats.initStartTime,
                    formatTime(timeDiff),
                    nbrRepetitionOrHoldTime
                )

            val task: FutureTask<String> =
                FutureTask(BackgroundInsertEntry(configUserId, histoEntry), "test")
            val executorService: ExecutorService = Executors.newFixedThreadPool(2)
            executorService.execute(task)
        }

        fun insertStatistics(stats: ExerciceStatistique) {
            val HEAD: RealmList<pointPos> = RealmList<pointPos>()
            val NECK: RealmList<pointPos> = RealmList<pointPos>()
            val L_SHOULDER: RealmList<pointPos> = RealmList<pointPos>()
            val L_ELBOW: RealmList<pointPos> = RealmList<pointPos>()
            val L_WRIST: RealmList<pointPos> = RealmList<pointPos>()
            val R_SHOULDER: RealmList<pointPos> = RealmList<pointPos>()
            val R_ELBOW: RealmList<pointPos> = RealmList<pointPos>()
            val R_WRIST: RealmList<pointPos> = RealmList<pointPos>()
            val L_HIP: RealmList<pointPos> = RealmList<pointPos>()
            val L_KNEE: RealmList<pointPos> = RealmList<pointPos>()
            val L_ANKLE: RealmList<pointPos> = RealmList<pointPos>()
            val R_HIP: RealmList<pointPos> = RealmList<pointPos>()
            val R_KNEE: RealmList<pointPos> = RealmList<pointPos>()
            val R_ANKLE: RealmList<pointPos> = RealmList<pointPos>()

            stats.bodyPartPos.HEAD.forEach {
                HEAD.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.NECK.forEach {
                NECK.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.L_SHOULDER.forEach {
                L_SHOULDER.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.L_ELBOW.forEach {
                L_ELBOW.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.L_WRIST.forEach {
                L_WRIST.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.R_SHOULDER.forEach {
                R_SHOULDER.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.R_ELBOW.forEach {
                R_ELBOW.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.R_WRIST.forEach {
                R_WRIST.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.L_HIP.forEach {
                L_HIP.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.L_KNEE.forEach {
                L_KNEE.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.L_ANKLE.forEach {
                L_ANKLE.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.R_HIP.forEach {
                R_HIP.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.R_KNEE.forEach {
                R_KNEE.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.R_ANKLE.forEach {
                R_ANKLE.add(pointPos(it.X, it.Y))
            }

            val bodypartObj = bodyPartPos(
                HEAD,
                NECK,
                L_SHOULDER,
                L_ELBOW,
                L_WRIST,
                R_SHOULDER,
                R_ELBOW,
                R_WRIST,
                L_HIP,
                L_KNEE,
                L_ANKLE,
                R_HIP,
                R_KNEE,
                R_ANKLE
            )

            val numberOfRepetitionRealm = RealmList<simpleInt>()
            stats.numberOfRepetition.forEach {
                val value: Int
                if (it == null) {
                    value = -1
                } else {
                    value = it
                }
                numberOfRepetitionRealm.add(simpleInt(value))
            }
            val speedOfRepetitionRealm = RealmList<simpleDouble>()
            stats.speedOfRepetition.forEach {
                val value: Double
                if (it == null) {
                    value = (-1).toDouble()
                } else {
                    value = it.toDouble()
                }
                speedOfRepetitionRealm.add(simpleDouble(value))
            }

            val holdTimeRealm = RealmList<simpleLong>()
            stats.holdTime.forEach {
                val value: Long
                if (it == null) {
                    value = -1
                } else {
                    value = it
                }
                holdTimeRealm.add(simpleLong(value))
            }
            val timeStampRealm = RealmList<simpleString>()
            stats.timeStamp.forEach {
                val value: String
                if (it == null) {
                    value = ""
                } else {
                    value = it
                }
                timeStampRealm.add(simpleString(value))
            }

            val movementRealm = RealmList<movement>()
            stats.movements.forEach {
                val angleAvg = RealmList<simpleInt>()
                val state = RealmList<simpleString>()
                it.angleAvg.forEach {
                    val value: Int
                    if (it == null) {
                        value = -1
                    } else {
                        value = it
                    }
                    angleAvg.add(simpleInt(value))
                }
                it.state.forEach {
                    val value: String
                    if (it == null) {
                        value = ""
                    } else {
                        value = it.toString()
                    }
                    state.add(simpleString(value))
                }
                movementRealm.add(movement(angleAvg, state))
            }

            var initStartTime = ""
            if (stats.initStartTime != null) {
                initStartTime = stats.initStartTime!!
            }

            var exerciceStartTime = ""
            if (stats.exerciceStartTime != null) {
                exerciceStartTime = stats.exerciceStartTime!!
            }

            var exerciceEndTime = ""
            if (stats.exerciceEndTime != null) {
                exerciceEndTime = stats.exerciceEndTime!!
            }

            val statistics = statistics(
                stats.exerciceName,
                stats.exerciceType,
                numberOfRepetitionRealm,
                speedOfRepetitionRealm,
                holdTimeRealm,
                timeStampRealm,
                initStartTime,
                exerciceStartTime,
                exerciceEndTime,
                movementRealm,
                bodypartObj,
                stats.patientID,
                stats.exerciceID
            )

            val task: FutureTask<String> =
                FutureTask(BackgroundInsertStatsEntry(configUserId, statistics), "test")
            val executorService: ExecutorService = Executors.newFixedThreadPool(2)
            executorService.execute(task)
        }

        fun addChangeListenerToRealm(
            realmUserId: Realm,
            realmTempId: Realm,
            realmExercices: Realm
        ) {
            historyListener = realmUserId.where<historique>().findAllAsync()
            historyListener.addChangeListener { collection, changeSet ->

                historic = realmUserId.copyFromRealm(collection);

                if (historyView != null) {
                    historyView!!.adapter =
                        HistoryActivity.SimpleItemRecyclerViewAdapter(historic)
                }
            }

            programListener = realmTempId.where<programmes>().findAllAsync()
            programListener.addChangeListener { collection, changeSet ->
                programmesList = realmTempId.copyFromRealm(collection);
                globalExerciceList = ExerciceList()
            }

            exercicesListener = realmExercices.where<exercices>().findAllAsync()
            exercicesListener.addChangeListener { collection, changeSet ->
                exercicesList = realmExercices.copyFromRealm(collection);
                globalExerciceList = ExerciceList()
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

        private fun ExerciceList(): MutableList<ExerciceData> {
            var exerciceDataList: MutableList<ExerciceData> = mutableListOf()
            val currentProgrammesList = programmesList
            val currentExerciceList = exercicesList
            currentProgrammesList.forEach { programme ->
                programme.exercices2?.forEach { exerciceProgram ->
                    currentExerciceList.forEach { exercice ->
                        var exerciceData = ExerciceData()
                        if (exerciceProgram.exerciceId == exercice._id.toString()) {
                            exerciceData.exercice.minExecutionTime =
                                exerciceProgram.tempo?.min?.toFloat()
                            exerciceData.exercice.maxExecutionTime =
                                exerciceProgram.tempo?.max?.toFloat()
                            exerciceData.id = exerciceProgram.exerciceId
                            exerciceData.name = exercice.name
                            exerciceData.description = exercice.description
                            exerciceData.exercice.exerciceType =
                                ExerciceType.getEnumValue(exercice.type.toUpperCase(Locale.ROOT))
                            exerciceData.exercice.numberOfRepetitionToDo =
                                exerciceProgram.repetition
                            var i = 0
                            exercice.movements?.forEach {
                                val movement = Movement(
                                    BodyPart.getEnumValue(it.bodyPart0)?.ordinal!!,
                                    BodyPart.getEnumValue(it.bodyPart1)?.ordinal!!,
                                    BodyPart.getEnumValue(it.bodyPart2)?.ordinal!!
                                )
                                if (i == 0) {
                                    movement.startingAngle = exerciceProgram.angle?.debut
                                    movement.endingAngle = exerciceProgram.angle?.fin
                                    movement.isAngleAntiClockWise =
                                        exerciceProgram.angle?.isAntiClockWise
                                } else {
                                    movement.startingAngle = exerciceProgram.angle2?.debut
                                    movement.endingAngle = exerciceProgram.angle2?.fin
                                    movement.isAngleAntiClockWise =
                                        exerciceProgram.angle2?.isAntiClockWise
                                }
                                exerciceData.exercice.movementList.add(movement)
                                i++
                            }
                            exerciceDataList.add(exerciceData)
                        }
                    }
                }
            }
            return exerciceDataList
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

    class BackgroundInsertStatsEntry(val config: SyncConfiguration, val histoEntry: statistics) :
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
    _date: String? = null,
    _duree: String = "",
    _nbrRepetitionOrHoldTime: String = ""
) :
    RealmObject() {
    @PrimaryKey
    var _id: ObjectId = ObjectId()

    var date = _date

    var duree = _duree

    var exerciceName = _exerciceName

    var exerciceType = _exerciceType

    var nbrRepetitionOrHoldTime = _nbrRepetitionOrHoldTime
}

open class statistics(
    _exerciceName: String = "",
    _exerciceType: String = "",
    _numberOfRepetition: RealmList<simpleInt> = RealmList<simpleInt>(),
    _speedOfRepetition: RealmList<simpleDouble> = RealmList<simpleDouble>(),
    _holdTime: RealmList<simpleLong> = RealmList<simpleLong>(),
    _timeStamp: RealmList<simpleString> = RealmList<simpleString>(),
    _initStartTime: String = "",
    _exerciceStartTime: String = "",
    _exerciceEndTime: String = "",
    _movements: RealmList<movement> = RealmList<movement>(),
    _bodyPartPos: bodyPartPos? = bodyPartPos(),
    _patientID: String = "",
    _exerciceID: String? = "",
) :
    RealmObject() {
    @PrimaryKey
    var _id: ObjectId = ObjectId()

    var exerciceName = _exerciceName
    var exerciceType = _exerciceType

    var numberOfRepetition = _numberOfRepetition
    var speedOfRepetition = _speedOfRepetition
    var holdTime = _holdTime
    var timeStamp = _timeStamp

    var initStartTime = _initStartTime
    var exerciceStartTime = _exerciceStartTime
    var exerciceEndTime = _exerciceEndTime

    var movements = _movements

    var bodyPartPos = _bodyPartPos

    var patientID: String = _patientID
    var exerciceID = _exerciceID
}

@RealmClass(embedded = true)
open class movement(
    _angleAvg: RealmList<simpleInt> = RealmList<simpleInt>(),
    _state: RealmList<simpleString> = RealmList<simpleString>(),
) :
    RealmObject() {
    var angleAvg = _angleAvg
    var state = _state
}

@RealmClass(embedded = true)
open class bodyPartPos(
    _HEAD: RealmList<pointPos>? = null,
    _NECK: RealmList<pointPos>? = null,
    _L_SHOULDER: RealmList<pointPos>? = null,
    _L_ELBOW: RealmList<pointPos>? = null,
    _L_WRIST: RealmList<pointPos>? = null,
    _R_SHOULDER: RealmList<pointPos>? = null,
    _R_ELBOW: RealmList<pointPos>? = null,
    _R_WRIST: RealmList<pointPos>? = null,
    _L_HIP: RealmList<pointPos>? = null,
    _L_KNEE: RealmList<pointPos>? = null,
    _L_ANKLE: RealmList<pointPos>? = null,
    _R_HIP: RealmList<pointPos>? = null,
    _R_KNEE: RealmList<pointPos>? = null,
    _R_ANKLE: RealmList<pointPos>? = null
) :
    RealmObject() {
    var HEAD = _HEAD
    var NECK = _NECK
    var L_SHOULDER = _L_SHOULDER
    var L_ELBOW = _L_ELBOW
    var L_WRIST = _L_WRIST
    var R_SHOULDER = _R_SHOULDER
    var R_ELBOW = _R_ELBOW
    var R_WRIST = _R_WRIST
    var L_HIP = _L_HIP
    var L_KNEE = _L_KNEE
    var L_ANKLE = _L_ANKLE
    var R_HIP = _R_HIP
    var R_KNEE = _R_KNEE
    var R_ANKLE = _R_ANKLE
}

@RealmClass(embedded = true)
open class pointPos(
    _X: Int = 0,
    _Y: Int = 0,
) :
    RealmObject() {
    var X = _X
    var Y = _Y
}

@RealmClass(embedded = true)
open class simpleInt(
    _value: Int = 0,
) :
    RealmObject() {
    var value = _value
}

@RealmClass(embedded = true)
open class simpleString(
    _value: String = "",
) :
    RealmObject() {
    var value = _value
}

@RealmClass(embedded = true)
open class simpleLong(
    _value: Long = 0,
) :
    RealmObject() {
    var value = _value
}

@RealmClass(embedded = true)
open class simpleDouble(
    _value: Double = 0.toDouble(),
) :
    RealmObject() {
    var value = _value
}

open class programmes(
    _nom: String = "",
    _exercices2: RealmList<exercices2>? = null
) : RealmObject() {
    @PrimaryKey
    var _id: ObjectId = ObjectId()

    var nom = _nom
    var exercices2 = _exercices2
}

@RealmClass(embedded = true)
open class exercices2(
    _nom: String = "",
    _angle: angle? = null,
    _angle2: angle? = null,
    _description: String = "",
    _exerciceId: String = "",
    _repetition: Int? = null,
    _tenir: Int? = null,
    _tempo: tempo? = null,
    _dimanche: Boolean? = null,
    _lundi: Boolean? = null,
    _mardi: Boolean? = null,
    _mercredi: Boolean? = null,
    _jeudi: Boolean? = null,
    _vendredi: Boolean? = null,
    _samedi: Boolean? = null,
) : RealmObject() {
    var nom = _nom
    var angle = _angle
    var angle2 = _angle2
    var description = _description
    var exerciceId = _exerciceId
    var repetition = _repetition
    var tenir = _tenir
    var tempo = _tempo
    var dimanche = _dimanche
    var lundi = _lundi
    var mardi = _mardi
    var mercredi = _mercredi
    var jeudi = _jeudi
    var vendredi = _vendredi
    var samedi = _samedi
}

@RealmClass(embedded = true)
open class angle(
    _debut: Int = 0,
    _fin: Int = 0,
    _hold: Int? = null,
    _isAntiClockWise: Boolean = false,
) : RealmObject() {
    var debut = _debut
    var fin = _fin
    var hold = _hold
    var isAntiClockWise = _isAntiClockWise
}

@RealmClass(embedded = true)
open class tempo(
    _max: Int = 0,
    _min: Int = 0,
    _value: Int = 0
) : RealmObject() {
    var max = _max
    var min = _min
    var value = _value
}

open class exercices(
    _name: String = "",
    _description: String = "",
    _options: options? = null,
    _imagePath: String = "",
    _isActive: Boolean? = null,
    _type: String = "",
    _movements: RealmList<movements>? = null
) : RealmObject() {
    @PrimaryKey
    var _id: ObjectId = ObjectId()

    var name = _name
    var description = _description
    var options = _options
    var imagePath = _imagePath
    var isActive = _isActive
    var type = _type
    var movements = _movements
}

@RealmClass(embedded = true)
open class movements(
    _bodyPart0: String = "",
    _bodyPart1: String = "",
    _bodyPart2: String = ""
) : RealmObject() {
    var bodyPart0 = _bodyPart0
    var bodyPart1 = _bodyPart1
    var bodyPart2 = _bodyPart2
}

@RealmClass(embedded = true)
open class options(
    _repetition: Boolean = false,
    _compteur: Boolean = false,
    _angle: Boolean = false
) : RealmObject() {
    var repetition = _repetition
    var compteur = _compteur
    var angle = _angle
}




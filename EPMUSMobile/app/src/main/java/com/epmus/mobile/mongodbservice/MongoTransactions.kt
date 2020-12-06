package com.epmus.mobile.mongodbservice

import com.epmus.mobile.*
import com.epmus.mobile.messaging.MessagingUser
import com.epmus.mobile.poseestimation.BodyPart
import com.epmus.mobile.poseestimation.ExerciseStatistics
import com.epmus.mobile.poseestimation.ExerciseType
import com.epmus.mobile.poseestimation.Movement
import com.epmus.mobile.program.ExerciseData
import com.epmus.mobile.ui.login.realmApp
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.kotlin.where
import io.realm.mongodb.User
import io.realm.mongodb.mongo.MongoClient
import io.realm.mongodb.mongo.MongoCollection
import io.realm.mongodb.mongo.MongoDatabase
import io.realm.mongodb.sync.SyncConfiguration
import org.bson.Document
import org.bson.types.ObjectId
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask

class MongoTransactions {
    companion object {

        var exerciseHistory: MutableList<HistoryData> = mutableListOf()
        var exerciseList: MutableList<ExerciseData> = mutableListOf()
        var physioList: MutableList<MessagingUser> = mutableListOf()
        var uiThreadRealmUserId: Realm
        var uiThreadRealmExercises: Realm
        private val configUserId: SyncConfiguration
        private val configExercises: SyncConfiguration
        private var programmesList: MutableList<programmes> = mutableListOf()
        private var exercicesPhysiotecList: MutableList<exercicesPhysiotec> = mutableListOf()
        private lateinit var historyListener: RealmResults<historique>
        private lateinit var programListener: RealmResults<programmes>
        private lateinit var exercicesPhysiotecListener: RealmResults<exercicesPhysiotec>

        init {
            val user: User? = realmApp.currentUser()
            configUserId =
                    SyncConfiguration.Builder(user, user?.customData?.get("_id").toString()).build()
            configExercises = SyncConfiguration.Builder(user, "exercices").build()
            uiThreadRealmUserId = Realm.getInstance(configUserId)
            uiThreadRealmExercises = Realm.getInstance(configExercises)
            addChangeListenerToRealm()

            val physios = user?.customData?.get("physio_associe") as List<String>

            val mongoClient: MongoClient = user?.getMongoClient("mongodb-atlas")!!
            val mongoDatabase: MongoDatabase = mongoClient.getDatabase("iphysioBD-dev")!!
            val mongoCollection: MongoCollection<Document> =
                    mongoDatabase.getCollection("physios")!!

            physios.forEach { physio ->
                mongoCollection.findOne(
                        Document("_id", ObjectId(physio))
                ).getAsync { findResult ->
                    if (findResult.isSuccess) {
                        val name = findResult.get()["name"].toString()
                        val messagingUser = MessagingUser(physio, name)
                        physioList.add(messagingUser)
                    }
                }
            }
        }

        fun historyEntry(stats: ExerciseStatistics) {
            val exerciseType = stats.exerciseType
            val exerciseTypeEnum = ExerciseType.getEnumValue(exerciseType)

            val nbrRepetitionOrHoldTime: String

            if (exerciseTypeEnum == ExerciseType.HOLD) {
                nbrRepetitionOrHoldTime = stats.holdtime.toString()
            } else if (exerciseTypeEnum == ExerciseType.AMPLITUDE) {
                nbrRepetitionOrHoldTime = stats.maxAngleAmplitude.toString()
            } else {
                nbrRepetitionOrHoldTime = stats.numberOfRepetition.last().toString()
            }

            val dates = SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
            val timeEnd: Date? = dates.parse(stats.exerciseEndTime!!)
            val timeStart: Date? = dates.parse(stats.exerciseStartTime!!)

            var timeDiff: Long = 0
            if (timeEnd != null && timeStart != null) {
                timeDiff = kotlin.math.abs(timeEnd.time - timeStart.time)
            }

            val histoEntry =
                    historique(
                            stats.exerciseName,
                            exerciseType,
                            stats.initStartTime,
                            formatTime(timeDiff),
                            nbrRepetitionOrHoldTime
                    )

            val task: FutureTask<String> =
                    FutureTask(
                            BackgroundInsertEntry(
                                    configUserId,
                                    histoEntry
                            ), "Succeeded History"
                    )
            val executorService: ExecutorService = Executors.newFixedThreadPool(1)
            executorService.execute(task)
        }

        fun insertStatistics(stats: ExerciseStatistics) {
            //TODO Pretreat the data, realm will have an error if the realmlist is too long
            val maxArray = 50

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
            val HIP: RealmList<pointPos> = RealmList<pointPos>()


            stats.bodyPartPos.HEAD.take(maxArray).forEach {
                HEAD.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.NECK.take(maxArray).forEach {
                NECK.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.L_SHOULDER.take(maxArray).forEach {
                L_SHOULDER.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.L_ELBOW.take(maxArray).forEach {
                L_ELBOW.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.L_WRIST.take(maxArray).forEach {
                L_WRIST.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.R_SHOULDER.take(maxArray).forEach {
                R_SHOULDER.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.R_ELBOW.take(maxArray).forEach {
                R_ELBOW.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.R_WRIST.take(maxArray).forEach {
                R_WRIST.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.L_HIP.take(maxArray).forEach {
                L_HIP.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.L_KNEE.take(maxArray).forEach {
                L_KNEE.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.L_ANKLE.take(maxArray).forEach {
                L_ANKLE.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.R_HIP.take(maxArray).forEach {
                R_HIP.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.R_KNEE.take(maxArray).forEach {
                R_KNEE.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.R_ANKLE.take(maxArray).forEach {
                R_ANKLE.add(pointPos(it.X, it.Y))
            }
            stats.bodyPartPos.HIP.take(maxArray).forEach {
                HIP.add(pointPos(it.X, it.Y))
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
                    R_ANKLE,
                    HIP
            )

            val timestampOfRepetition = RealmList<simpleString>()
            stats.timestampOfRepetition.take(maxArray).forEach {
                if (it != null) {
                    timestampOfRepetition.add(simpleString(it))
                }
            }

            val numberOfRepetition = RealmList<simpleInt>()
            stats.numberOfRepetition.take(maxArray).forEach {
                if (it != null) {
                    numberOfRepetition.add(simpleInt(it))
                }
            }

            val holdTimeStartTime = RealmList<simpleString>()
            stats.holdTimeStartTime.take(maxArray).forEach {
                if (it != null) {
                    holdTimeStartTime.add(simpleString(it))
                }
            }

            val holdTimeEndTime = RealmList<simpleString>()
            stats.holdTimeEndTime.take(maxArray).forEach {
                if (it != null) {
                    holdTimeEndTime.add(simpleString(it))
                }
            }

            val movementRealm = RealmList<movement>()
            stats.movements.take(maxArray).forEach {
                val timestampState = RealmList<simpleString>()
                val state = RealmList<simpleString>()
                it.timestampState.take(maxArray).forEach { timestamp ->
                    if (timestamp != null) {
                        timestampState.add(simpleString(timestamp))
                    }
                }
                it.state.take(maxArray).forEach { movement ->
                    state.add(simpleString(movement.toString()))
                }
                movementRealm.add(movement(timestampState, state))
            }


            val statistics = statistics(
                    stats.exerciseName,
                    stats.exerciseType,
                    stats.exerciseID,
                    timestampOfRepetition,
                    numberOfRepetition,
                    holdTimeStartTime,
                    holdTimeEndTime,
                    stats.maxAngleAmplitude,
                    stats.initStartTime,
                    stats.exerciseStartTime,
                    stats.exerciseEndTime,
                    movementRealm,
                    stats.avgFps,
                    bodypartObj
            )

            val task: FutureTask<String> =
                    FutureTask(
                            BackgroundInsertStatsEntry(
                                    configUserId,
                                    statistics
                            ), "Succeeded Statistics"
                    )
            val threads = Runtime.getRuntime().availableProcessors()
            val executorService: ExecutorService = Executors.newFixedThreadPool(threads)
            executorService.execute(task)
        }

        private fun addChangeListenerToRealm() {
            historyListener = uiThreadRealmUserId.where<historique>().findAllAsync()
            historyListener.addChangeListener { collection, _ ->

                val historySorted = mutableListOf<HistoryData>()
                collection.forEach {
                    val formatterFrom = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                    val dateTime = LocalDateTime.parse(it.date!!, formatterFrom)
                    historySorted.add(
                            HistoryData(
                                    it.exerciceName,
                                    it.exerciceType,
                                    dateTime,
                                    it.duree,
                                    it.nbrRepetitionOrHoldTime
                            )
                    )
                }

                //Sort list by date
                historySorted.sortByDescending { it.date }

                exerciseHistory = historySorted

                //Update exercise history as soon as there is a changer
                if (historyView != null) {
                    historyView!!.adapter =
                            HistoryActivity.SimpleItemRecyclerViewAdapter(exerciseHistory)
                }
            }

            programListener = uiThreadRealmUserId.where<programmes>().findAllAsync()
            programListener.addChangeListener { collection, _ ->
                programmesList = uiThreadRealmUserId.copyFromRealm(collection)
                exerciseList = exerciseList()
            }

            exercicesPhysiotecListener =
                    uiThreadRealmExercises.where<exercicesPhysiotec>().findAllAsync()
            exercicesPhysiotecListener.addChangeListener { collection, _ ->
                exercicesPhysiotecList = uiThreadRealmExercises.copyFromRealm(collection)
                exerciseList = exerciseList()
            }
        }

        private fun formatTime(time: Long): String {
            val cleanTime: String
            var seconds: Long = time / 1000

            if (seconds > 59) {
                val df = DecimalFormat("##")
                df.roundingMode = RoundingMode.FLOOR

                val minutes = df.format(seconds / 60)
                seconds %= 60

                cleanTime = minutes.toString() + "m " + seconds.toString() + "s"
            } else {
                cleanTime = seconds.toString() + "s"
            }

            return cleanTime
        }

        private fun exerciseList(): MutableList<ExerciseData> {
            val exerciseDataList: MutableList<ExerciseData> = mutableListOf()
            val currentProgrammesList = programmesList
            val currentExerciseList = exercicesPhysiotecList
            currentProgrammesList.forEach { programme ->
                programme.exercices?.forEach { exerciseProgram ->
                    currentExerciseList.forEach { exercise ->
                        val exerciseData = ExerciseData()
                        if (exerciseProgram.exerciceId == exercise._id.toString()) {
                            exerciseData.mondayAlarm = exerciseProgram.lundi ?: false
                            exerciseData.tuesdayAlarm = exerciseProgram.mardi ?: false
                            exerciseData.wednesdayAlarm = exerciseProgram.mercredi ?: false
                            exerciseData.thursdayAlarm = exerciseProgram.jeudi ?: false
                            exerciseData.fridayAlarm = exerciseProgram.vendredi ?: false
                            exerciseData.saturdayAlarm = exerciseProgram.samedi ?: false
                            exerciseData.sundayAlarm = exerciseProgram.dimanche ?: false
                            exerciseData.exercise.minExecutionTime =
                                    exerciseProgram.tempo?.min?.toFloat() ?: 0F
                            exerciseData.exercise.maxExecutionTime =
                                    exerciseProgram.tempo?.max?.toFloat() ?: 9999F
                            exerciseData.id = exerciseProgram.exerciceId
                            exerciseData.imagePath = exercise.imagePath
                            exerciseData.name = exerciseProgram.nom
                            exerciseData.description = exerciseProgram.description
                            exerciseData.exercise.exerciseType =
                                    ExerciseType.getEnumValue(exercise.type.toUpperCase(Locale.ROOT))
                                            ?: ExerciseType.REPETITION
                            exerciseData.exercise.targetHoldTime = exerciseProgram.tenir ?: 0
                            exerciseData.exercise.numberOfRepetitionToDo =
                                    exerciseProgram.repetition ?: 0
                            exerciseData.exercise.allowedTimeForExercise =
                                    exerciseProgram.duree ?: 0
                            exercise.movements?.forEachIndexed { index, movementValue ->
                                val movement = Movement(
                                        BodyPart.getEnumValue(movementValue.bodyPart0)?.ordinal!!,
                                        BodyPart.getEnumValue(movementValue.bodyPart1)?.ordinal!!,
                                        BodyPart.getEnumValue(movementValue.bodyPart2)?.ordinal!!
                                )
                                if (index == 0) {
                                    if (exerciseData.exercise.exerciseType == ExerciseType.HOLD) {
                                        movement.endingAngle = exerciseProgram.angle?.hold ?: 0
                                    } else {
                                        movement.endingAngle = exerciseProgram.angle?.fin ?: 0
                                    }
                                    movement.startingAngle = exerciseProgram.angle?.debut ?: 0
                                    movement.isAngleClockWise =
                                            exerciseProgram.angle?.isClockWise ?: false
                                } else {
                                    movement.startingAngle = exerciseProgram.angle2?.debut ?: 0
                                    movement.endingAngle = exerciseProgram.angle2?.fin ?: 0
                                    movement.isAngleClockWise =
                                            exerciseProgram.angle2?.isClockWise ?: false
                                }
                                exerciseData.exercise.movementList.add(movement)
                            }
                            exerciseDataList.add(exerciseData)
                        }
                    }
                }
            }
            return exerciseDataList
        }
    }

    class BackgroundInsertEntry(
            private val config: SyncConfiguration,
            private val histoEntry: historique
    ) :
            Runnable {
        override fun run() {
            val realmInstance = Realm.getInstance(config)

            realmInstance.executeTransaction { transactionRealm ->
                transactionRealm.insert(histoEntry)
            }

            realmInstance.close()
        }
    }

    class BackgroundInsertStatsEntry(
            private val config: SyncConfiguration,
            private val histoEntry: statistics
    ) :
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
        _exerciceID: String = "",
        _timestampOfRepetition: RealmList<simpleString> = RealmList<simpleString>(),
        _numberOfRepetition: RealmList<simpleInt> = RealmList<simpleInt>(),
        _holdTimeStartTime: RealmList<simpleString> = RealmList<simpleString>(),
        _holdTimeEndTime: RealmList<simpleString> = RealmList<simpleString>(),
        _maxAngleAmplitude: Int? = 0,
        _initStartTime: String? = null,
        _exerciceStartTime: String? = null,
        _exerciceEndTime: String? = null,
        _movement: RealmList<movement> = RealmList<movement>(),
        _avgFPS: Double = -1.0,
        _bodyPartPos: bodyPartPos? = bodyPartPos(),
) :
        RealmObject() {
    @PrimaryKey
    var _id: ObjectId = ObjectId()

    var exerciceName = _exerciceName
    var exerciceType = _exerciceType
    var exerciceID = _exerciceID

    var timestampOfRepetition = _timestampOfRepetition
    var numberOfRepetition = _numberOfRepetition

    var holdTimeStartTime = _holdTimeStartTime
    var holdTimeEndTime = _holdTimeEndTime

    var maxAngleAmplitude = _maxAngleAmplitude

    var initStartTime = _initStartTime
    var exerciceStartTime = _exerciceStartTime
    var exerciceEndTime = _exerciceEndTime

    var movement = _movement

    var avgFPS = _avgFPS
    var bodyPartPos = _bodyPartPos
}

@RealmClass(embedded = true)
open class movement(
        _timestampState: RealmList<simpleString> = RealmList<simpleString>(),
        _state: RealmList<simpleString> = RealmList<simpleString>(),
) :
        RealmObject() {
    var timestampState = _timestampState
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
        _R_ANKLE: RealmList<pointPos>? = null,
        _HIP: RealmList<pointPos>? = null
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
    var HIP = _HIP
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

open class programmes(
        _nom: String = "",
        _exercices: RealmList<exercices>? = null
) : RealmObject() {
    @PrimaryKey
    var _id: ObjectId = ObjectId()

    var nom = _nom
    var exercices = _exercices
}

@RealmClass(embedded = true)
open class exercices(
        _nom: String = "",
        _angle: angle? = null,
        _angle2: angle? = null,
        _description: String = "",
        _exerciceId: String = "",
        _repetition: Int? = null,
        _tenir: Int? = null,
        _tempo: tempo? = null,
        _duree: Int? = null,
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
    var duree = _duree
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
        _isClockWise: Boolean = false,
) : RealmObject() {
    var debut = _debut
    var fin = _fin
    var hold = _hold
    var isClockWise = _isClockWise
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

open class exercicesPhysiotec(
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




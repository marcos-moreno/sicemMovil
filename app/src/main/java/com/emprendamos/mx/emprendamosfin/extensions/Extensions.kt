package com.emprendamos.mx.emprendamosfin.extensions

import android.content.Context
import com.emprendamos.mx.emprendamosfin.data.database.repository.AppDatabase
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.PayControl
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControlClient
import com.emprendamos.mx.emprendamosfin.data.database.repository.mapper.MapperManager
import kotlinx.coroutines.experimental.launch
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import android.app.Activity
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.emprendamos.mx.emprendamosfin.data.database.repository.dao.PositionType
import com.emprendamos.mx.emprendamosfin.data.model.Asistances
import com.emprendamos.mx.emprendamosfin.data.model.Types
import java.net.HttpURLConnection
import java.net.URL


const val EMPTY_STRING = ""

enum class DateFormat(val format: String) {
    yyyy_MM_dd("yyyy-MM-dd"),
    dd_MM_yyyy("dd/MM/yyyy"),
    MM_dd_yyyy("MM/dd/yyyy"),
    yyyyMMdd("yyyy/MM/dd"),
    dd_MM_yyyy_hh_mm("dd/MM/yyyy hh:mm"),
    dd_MM_yyyy_hh_mm_ss("dd/MM/yyyy hh:mm:ss a"),
    dd_MM_yyyy_HH_mm("dd/MM/yyyy HH:mm")
}

fun <T, OT> Map<T, OT>.toOList(): List<OT> {
    var data = ArrayList<OT>()
    this.forEach { (t, ot) ->
        data.add(ot)
    }
    return data
}

fun String.toMoney(): String {
    return DecimalFormat("0.00").format(this.toDouble())
}

fun String.toDate(pattern: DateFormat): Date {
    return try {
        SimpleDateFormat(pattern.format).parse(this)
    } catch (ex: Exception) {
        Date(0)
    }
}

fun NodeList.findNodeByName(name: String): Node? {
    return MapperManager.findNodeByName(this, name)
}

fun binom2(n: Long): Long {
    return n * (n - 1) / 2
}

fun isTemplateRegister(paycontrol: PayControl): Boolean {
    return Date(paycontrol.date).time == 631173600000L
            || Date(paycontrol.date).year == 1990
            || Date(paycontrol.date).year == 90

}

fun tup(howMany: Int, arguments: Array<Long>): Long {
    return if (howMany == 1)
        arguments[0]
    else if (howMany == 2)
        binom2(arguments[0] + arguments[1] + 1) + arguments[1]
    else
        tup(2, arrayOf(tup(howMany - 1, arguments), arguments[howMany - 1]))
}

fun createNewPayControl(context: Context, payControl: PayControl, payControlSequences: List<SequencePayControlClient>, completitionListener: (Boolean) -> Unit) {
    launch {

        try {
            var total_real = 0.0
            var total_theorical = 0.0
            var total_aport = 0.0
            var total_refund = 0.0
            var total_saving = 0.0
            var total_fee = 0.0

            payControl.sequences.forEach {
                //it.paycontrol_id = payControl.id
                //it.id = null
                it.fecreg = System.currentTimeMillis()
                it.type = payControl.type
                //it.sequence = AppDatabase.getInstance(context).sequenceDao().getMax(payControl.id!!)
                //it.sequence++

                var idSequence = AppDatabase.getInstance(context).sequenceDao().insert(it)

                payControlSequences.forEach {
                    it.sequencepaycontrol_id = idSequence!!
                    it.local_modification = true
                    it.status = 1
                    it.getKey()
                    AppDatabase.getInstance(context).sequenceClientDao().insert(it)
                }
            }

            AppDatabase.getInstance(context).sequenceDao().getAllForPayControlModified(payControl.id!!).forEach { sequencePayControl ->

                total_real = 0.0
                total_theorical = 0.0
                total_aport = 0.0
                total_refund = 0.0
                total_saving = 0.0
                total_fee = 0.0
                
                AppDatabase.getInstance(context).sequenceClientDao().getAllForPayControlSequenceModified(sequencePayControl.id!!).forEach { sequencePayControlClient ->
                    total_real += sequencePayControlClient.real_payment!!
                    total_theorical += sequencePayControlClient.theorical_payment
                    total_aport += sequencePayControlClient.aport
                    total_refund += sequencePayControlClient.refund
                    total_saving += sequencePayControlClient.saving
                    total_fee += sequencePayControlClient.fee
                }

                sequencePayControl.real_payment = total_real
                sequencePayControl.theorical_payment = total_theorical
                sequencePayControl.aport = total_aport
                sequencePayControl.refund = total_refund
                sequencePayControl.saving = total_saving
                sequencePayControl.fee = total_fee

                AppDatabase.getInstance(context).sequenceDao().insert(sequencePayControl)
            }

            // payControl.real_payment = total_real
            payControl.theorical_payment = total_theorical
            payControl.aport = total_aport
            payControl.refund = total_refund
            payControl.saving = total_saving
            payControl.fee = total_fee

            AppDatabase.getInstance(context).payControlDao().insert(payControl)


            completitionListener(true)
        } catch (ex: Exception) {
            completitionListener(false)
        }

    }
}

fun editSequences(context: Context, payControlSequences: List<SequencePayControlClient>, payControl: PayControl, completitionListener: (Boolean, String) -> Unit) {
    launch {
        try {

            payControlSequences.forEach {
                it.sequencePayControl = payControl.sequences[0]
                it.getKey()
                it.local_modification = true
                it.status = 1
                AppDatabase.getInstance(context).sequenceClientDao().insert(it)
            }

            var total_real = 0.0
            var total_theorical = 0.0
            var total_aport = 0.0
            var total_refund = 0.0
            var total_saving = 0.0
            var total_fee = 0.0

            AppDatabase.getInstance(context).sequenceDao().getAllForPayControlModified(payControl.id!!).forEach { sequencePayControl ->
                total_real = 0.0
                total_theorical = 0.0
                total_aport = 0.0
                total_refund = 0.0
                total_saving = 0.0
                total_fee = 0.0

                AppDatabase.getInstance(context).sequenceClientDao().getAllForPayControlSequenceModified(sequencePayControl.id!!).forEach { sequencePayControlClient ->
                    total_real += sequencePayControlClient.real_payment!!
                    total_theorical += sequencePayControlClient.theorical_payment
                    total_aport += sequencePayControlClient.aport
                    total_refund += sequencePayControlClient.refund
                    total_saving += sequencePayControlClient.saving
                    total_fee += sequencePayControlClient.fee
                }

                sequencePayControl.real_payment = total_real
                sequencePayControl.theorical_payment = total_theorical
                sequencePayControl.aport = total_aport
                sequencePayControl.refund = total_refund
                sequencePayControl.saving = total_saving
                sequencePayControl.fee = total_fee

                AppDatabase.getInstance(context).sequenceDao().insert(sequencePayControl)
            }

            // payControl.real_payment = total_real
            payControl.theorical_payment = total_theorical
            payControl.aport = total_aport
            payControl.refund = total_refund
            payControl.saving = total_saving
            payControl.fee = total_fee

            //AppDatabase.getInstance(context).payControlDao().update(payControl)

            completitionListener(true, EMPTY_STRING)
        } catch (ex: Exception) {
            completitionListener(false, ex.message ?: "")
        }

    }
}

fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = activity.currentFocus
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Long.toFormattedCode(): String {
    return String.format("%06d", this)
}

fun hasInternetConnection(context: Context) : Boolean {
    if (isNetworkAvailable(context)) {
        try {
            val urlc = URL("http://clients3.google.com/generate_204")
                    .openConnection() as HttpURLConnection
            urlc.setRequestProperty("User-Agent", "Android")
            urlc.setRequestProperty("Connection", "close")
            urlc.connectTimeout = 1500
            urlc.connect()
            return urlc.responseCode == 204 && urlc.contentLength == 0
        } catch (ex: java.lang.Exception) {
            Log.e("Extensions", ex.message)
        }
        return false
    } else {
        Log.e("Extensions", "No network available")
        return false
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null
}

fun getPositionType(position_type: Short): String {
    val values = arrayOf(PositionType.NOT_SPECIFIED, PositionType.SERVICE_UNAVAILABLE, PositionType.LOG_IN, PositionType.GROUP_OPEN, PositionType.GROUP_CLOSE, PositionType.GROUP_SYNC, PositionType.SEQUENCE_EDIT, PositionType.SEQUENCE_NEW, PositionType.SEQUENCE_SAVE, PositionType.SEQUENCE_BEGIN)

    for (i in values.indices) {
        if (values[i].type == position_type)
            return values[i].description
    }

    return PositionType.NOT_SPECIFIED.description
}

fun getTypePosition(type: String): Int {
    val values = arrayOf(Types.NOT_ASIGNED, Types.S, Types.P)

    for (i in values.indices) {
        if (values[i].description.equals(type))
            return i
    }

    return 0
}

fun getAsistancePosition(asistance: String): Int {
    val values = arrayOf(Asistances.Asistencia, Asistances.Retardo, Asistances.Falta, Asistances.Mando_Pago, Asistances.Permiso, Asistances.Desconocido)

    if (asistance.equals(""))
        return values.lastIndex

    for (i in values.indices) {
        if (values[i].name.equals(asistance.replace("ó","o").replace(" ","_")))
            return i
    }

    return 0
}

fun editableZero(editable: Double): String {
    return if (editable <= 0)
        "0.0".toMoney()
    else
        editable.toString().toMoney()
}

fun getTypeKey(type: String): Char {
    val values = arrayOf(Types.NOT_ASIGNED, Types.UNKNOWN, Types.S, Types.P)

    for (i in values.indices) {
        if (values[i].description.equals(type))
            return values[i].identification
    }

    return Types.NOT_ASIGNED.identification
}
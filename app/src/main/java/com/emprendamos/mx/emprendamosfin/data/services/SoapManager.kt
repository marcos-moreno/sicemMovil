package com.emprendamos.mx.emprendamosfin.data.services

import android.content.Context
import android.util.Log
import com.emprendamos.mx.emprendamosfin.data.PreferenceHelper
import com.emprendamos.mx.emprendamosfin.data.database.repository.AppDatabase
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Advisor
import com.emprendamos.mx.emprendamosfin.data.database.repository.mapper.MapperManager
import com.emprendamos.mx.emprendamosfin.extensions.EMPTY_STRING
import com.emprendamos.mx.emprendamosfin.extensions.getPositionType
import kotlinx.coroutines.experimental.launch
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.MarshalDate
import org.ksoap2.serialization.MarshalFloat
import org.ksoap2.serialization.SoapPrimitive
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.net.Proxy
import java.util.*

class SoapManager(val context : Context) {
    companion object {
        val TAG = this::class.java.simpleName
        const val NS_EMPRENDAMOS_WS = "http://tempuri.org/"
        const val NS_EMPRENDAMOS_GPS = "http://www.emprendamosfin.com/microfinancieras/posicionador/"
        const val PROPERTY_USER = "usuario"
        const val URL_WS_ENDPOINT = "http://201.149.62.117/ws_servicios_marc/service.asmx"
        const val URL_LOGIN_ENDPOINT = "http://201.149.62.117/administracion/seguridad_marc/Process_Service/ES_Process_Service.asmx"
        const val URL_GPS_ENDPOINT = "http://201.149.62.118/Posicionador/Posicionador.asmx"
        const val TIMEOUT = 60000
    }

    val prefHelper by lazy {
        PreferenceHelper(context)
    }


    fun importGroupData(agentKey: String, serviceListener: (String) -> Unit) {
        val method = "getImportaControlPagos"
        var soapObject = SoapObject(NS_EMPRENDAMOS_WS, method)
        var result: String

        soapObject.addProperty(PROPERTY_USER, agentKey)

        try {
            result = executeWebService(prefHelper.getServicesUrl(), NS_EMPRENDAMOS_WS, method, soapObject)

            result.apply {
                replace("&lt;", "<")
                replace("&gt;", ">")
            }

            serviceListener(result)
        } catch (ex: Exception) {
            Log.e(TAG, ex.message)
        }

    }

    fun importGroupAndClientData(agentKey: String, serviceListener: (String) -> Unit) {
        val method = "getImportaControlPagosAcred"
        var soapObject = SoapObject(NS_EMPRENDAMOS_WS, method)
        var result: String

        soapObject.addProperty(PROPERTY_USER, agentKey)

        try {
            result = executeWebService(prefHelper.getServicesUrl(), NS_EMPRENDAMOS_WS, method, soapObject)

            result.apply {
                replace("&lt;", "<")
                replace("&gt;", ">")
            }

            serviceListener(result)
        } catch (ex: Exception) {
            Log.e(TAG, ex.message)
        }
    }

    fun executeWebService(endpoint: String, namespace: String, method: String, soapObject: SoapObject): String {
        var wrapper = SoapSerializationEnvelope(SoapEnvelope.VER11)

        MarshalDate().register(wrapper)
        MarshalFloat().register(wrapper)

        wrapper.dotNet = true
        wrapper.implicitTypes = true
        wrapper.isAddAdornments = false
        wrapper.setOutputSoapObject(soapObject)

        var httpTransport = HttpTransportSE(Proxy.NO_PROXY, endpoint, TIMEOUT)
        httpTransport.debug = true

        httpTransport.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->")

        try {
            httpTransport.call(namespace + method, wrapper)
            var response = wrapper.response

            return when (response) {
                is SoapPrimitive -> {
                    response.toString()
                }
                is SoapObject -> {
                    response.toString()
                }
                else -> EMPTY_STRING
            }
        } catch (ex: java.lang.Exception) {
            var dump = httpTransport.requestDump
            var responseDumb = httpTransport.responseDump
            return EMPTY_STRING
        }
    }

    fun doLogin(userName: String, userCode: String, serviceListener: (String) -> Unit) {
        var method = "ESLoginMethods"
        var soapParameters = SoapObject(NS_EMPRENDAMOS_WS, "Parameters")

        soapParameters.addProperty("anyType", userName)
        soapParameters.addProperty("anyType", userCode)

        var soapObject = SoapObject(NS_EMPRENDAMOS_WS, method)
        soapObject.addProperty("NumMetodo", 3)
        soapObject.addSoapObject(soapParameters)

        try {
            var result = executeWebService(prefHelper.getLoginUrl(), NS_EMPRENDAMOS_WS, method, soapObject)
            serviceListener(result)
        } catch (ex: Exception) {
            serviceListener("")
            Log.e(TAG, ex.message)
        }
    }

    fun syncService(context: Context, advisor : Advisor, callback : (Boolean, String) -> Unit = {_, _ -> }) {

        launch {
            var groups = AppDatabase.getInstance(context).groupDao().getAll()

            groups.forEach {
                val mainGroup = it
                it.payControls = ArrayList(AppDatabase.getInstance(context).payControlDao().getAllModified(it.code))

                it.payControls.forEach {
                    it.group = mainGroup
                    val payControl = it
                    it.sequences = ArrayList(AppDatabase.getInstance(context).sequenceDao().getAllForPayControlModified(it.id!!))

                    it.sequences.forEach {
                        val mainSequence = it
                        it.paycontrol = payControl
                        it.payControlSequencePayControlClient = ArrayList(AppDatabase.getInstance(context).sequenceClientDao().getAllForPayControlSequenceModified(it.id!!))
                        it.payControlSequencePayControlClient.forEach {
                            it.sequencePayControl = mainSequence
                            it.client = AppDatabase.getInstance(context).clientDao().getById(it.client_id)
                        }
                    }
                }
            }
            var list = MapperManager().data2xml(groups)

            val method = "setSincronizaControlSemanal"
            var sincronizedSequences = 0

            for(it in list)
            {
                try {
                    var sequenceId = it[2]
                    val soapObject = SoapObject(NS_EMPRENDAMOS_WS, method)
                    soapObject.addProperty("control", it[0])
                    soapObject.addProperty("controlDet", it[1])
                    soapObject.addProperty("usuario", advisor.key!!)

                    //Log.e("SoapManagerFile", soapObject.toString())

                    var result = executeWebService(prefHelper.getServicesUrl(), NS_EMPRENDAMOS_WS, method, soapObject)

                    if (result == "1") {
                        sincronizedSequences++
                        Log.e(TAG, "SYNCED: $sequenceId / $sincronizedSequences ${it[3]}")
                        AppDatabase.getInstance(context).sequenceDao().updateSincronized(sequenceId.toLong(), true)

                    } else {
                        Log.e(TAG, "SYNCERROR: $result")
                        AppDatabase.getInstance(context).sequenceDao().updateSincronized(sequenceId.toLong(), false)
                    }
                } catch (ex: Exception) {
                    Log.e("SoapManager", "${ex.message}")
                    callback(false, "${ex.message}")
                }
            }

            if (sincronizedSequences != list.size)
                callback(false, "No fue posible sincronizar completamente ($sincronizedSequences / ${list.size})")
            else
                callback(true, "$sincronizedSequences Sincronizadas!")

            Log.e("SoapManager", "${list.size}")
        }
    }

    fun syncPositions(companyCode: String, code: String, context : Context, completableListener : (Boolean) -> Unit = {}) {
        val method = "insertarPosicionesAsesor"

        val soapObject = SoapObject(NS_EMPRENDAMOS_GPS, method)
        val pack = SoapObject(NS_EMPRENDAMOS_GPS, "elpaquete")
        val soapPositions = SoapObject(NS_EMPRENDAMOS_GPS, "Lasposiciones")

        pack.addProperty("Codigoempresa", companyCode)
        pack.addProperty("Codigoasesor", code)

        launch {
            try {
                var positions = AppDatabase.getInstance(context).positionDao().getAll()

                positions.forEach {
                    val mPosition = it
                    val soapPosition = SoapObject(NS_EMPRENDAMOS_GPS, "Posicion")

                    soapPosition.addProperty("Fechagps", Date(it.gps_date ?: 0))
                    soapPosition.addProperty("Horagps", Date(it.gps_hour ?: 0))
                    soapPosition.addProperty("IDTipoposicion", it.position_type.toInt())
                    soapPosition.addProperty("Tipoposicion", getPositionType(it.position_type))
                    soapPosition.addProperty("Longitud", it.longitude)
                    soapPosition.addProperty("Latitud", it.latitude)
                    soapPosition.addProperty("Altitud", String.format("%.2f", it.altitude))
                    soapPosition.addProperty("Cadenaoriginal", it.original_string)
                    soapPosition.addProperty("Horaregistro", Date(it.register_hour ?: 0))

                    var groupCode: String? = null
                    var cicle: String? = null
                    var frealpago: Date? = Date()
                    var secuencia: String? = null
                    mPosition.group_id?.let {
                        if (it != 0L) {
                            val group = AppDatabase.getInstance(context).groupDao().getById(it)
                            groupCode = String.format("%06d", group.code)
                        }
                    }

                    soapPosition.addProperty("Codigogrupo", groupCode)

                    mPosition.secuence_paycontrol_id?.let {
                        if (it != 0L) {
                            val seq = AppDatabase.getInstance(context).sequenceDao().getById(it)

                            seq?.let {
                                seq.paycontrol = AppDatabase.getInstance(context).payControlDao().getForId(seq.paycontrol_id!!)
                                cicle = String.format("%02d", seq.paycontrol?.cycle ?: 0)
                                frealpago = Date(seq.paycontrol?.date ?: 0)
                                secuencia = String.format("%02d", seq.sequence)
                            }
                        }
                    }
                    soapPosition.addProperty("Ciclo",cicle)
                    soapPosition.addProperty("Frealpago", frealpago)
                    soapPosition.addProperty("Secuencia", secuencia)

                    soapPositions.addSoapObject(soapPosition)
                }

                pack.addSoapObject(soapPositions)
                soapObject.addSoapObject(pack)
                val response = executeWebService(prefHelper.getGpsUrl(), NS_EMPRENDAMOS_GPS, method, soapObject)
                AppDatabase.getInstance(context).positionDao().clearTable()
                Log.e(TAG, response)
            }
            catch (ex:Exception) {
                Log.e(TAG, ex.localizedMessage)
            } finally {
                completableListener(true)
            }
        }
    }

    fun executeWebServiceTest(endpoint: String, namespace: String, method: String, soapObject: SoapObject) : String {
        var wrapper = SoapSerializationEnvelope(SoapEnvelope.VER11)

        MarshalDate().register(wrapper)
        MarshalFloat().register(wrapper)

        wrapper.dotNet = true
        wrapper.implicitTypes = true
        wrapper.isAddAdornments = false
        wrapper.setOutputSoapObject(soapObject)

        var httpTransport = HttpTransportSE(Proxy.NO_PROXY, "", TIMEOUT)
        httpTransport.debug = true

        httpTransport.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->")

        return ""

    }


}
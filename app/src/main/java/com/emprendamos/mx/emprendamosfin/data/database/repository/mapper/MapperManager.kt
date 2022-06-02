package com.emprendamos.mx.emprendamosfin.data.database.repository.mapper

import android.util.Log
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.*
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import java.util.LinkedList
import javax.xml.parsers.DocumentBuilderFactory

class MapperManager {

    companion object {
        val TAG = this::class.java.simpleName

        fun findNodeByName(table: NodeList, nodeName: String): Node? {
            for (nodeIndex in 0 until table.length) {
                var childNode = table.item(nodeIndex)

                if (childNode.nodeName.equals(nodeName))
                    return childNode
            }

            return null
        }
    }

    @Throws(Exception::class)
    fun exportData(xmlData: String, exportListener: (PayInfo?) -> Unit = {}) {
        try {
            val groups = HashMap<Int, Group>()
            val payControls = HashMap<Long, PayControl>()
            val sequences = HashMap<Long, SequencePayControl>()
            var payInfo = PayInfo()

            var documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

            var inputSource = InputSource().apply {
                characterStream = StringReader(xmlData)
            }

            var doc = documentBuilder.parse(inputSource)
            var nodeList = doc.getElementsByTagName("Table")

            if (nodeList.length == 0) {
                exportListener(null)
            } else {
                for (nodeIndex in 0 until nodeList.length) {
                    var nodeChildList = nodeList.item(nodeIndex).childNodes
                    var group = Group()
                    var payControl = PayControl()
                    var payControlSequence = SequencePayControl()

                    group.xml2Group(nodeChildList)

                    if (groups.containsKey(group.code)) {
                        group = groups[group.code]!!
                    } else {
                        groups[group.code] = group
                    }

                    payControl.xml2Obj(nodeChildList)
                    payControl.group_id = group.code
                    payControl.getKey()

                    if (payControls.containsKey(payControl.id))
                        payControl = payControls.get(payControl.id)!!
                    else {

                        payControls.put(payControl.id!!, payControl)
                    }

                    payControlSequence.xml2Obj(nodeChildList)
                    payControlSequence.sincronized = false
                    payControlSequence.paycontrol_id = payControl.id!!
                    payControlSequence.getKey()

                    if (!sequences.containsKey(payControlSequence.id)) {
                        sequences.put(payControlSequence.id!!, payControlSequence)
                    } else
                        throw Error("Secuencia " + payControlSequence + "duplicada !!!! Deteniendo operaciones")

                }

                groups.forEach { (i, group) ->
                    payControls?.forEach { (y, payControl) ->
                        if (payControl.group_id == group.code) {
                            sequences?.forEach { (z, sequencePay) ->
                                if (sequencePay.paycontrol_id == payControl.id)
                                    payControl.sequences.add(sequencePay)
                            }
                            group.payControls.add(payControl)
                        }
                    }
                }

                payInfo.groups = groups
                payInfo.payControls = payControls
                payInfo.sequences = sequences

                exportListener(payInfo)
            }
        } catch (ex : Exception)
        {
            Log.e("SoapManager", ex.message)
        }
    }

    fun exportGroupAndClientData(xmlData: String, payInfo: PayInfo): Boolean {
        val losclientes = HashMap<Int, Client>()
        val lassecuenciasclientes = HashMap<Long, SequencePayControlClient>()

        try {

            var documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

            var inputSource = InputSource().apply {
                characterStream = StringReader(xmlData)
            }

            var doc = documentBuilder.parse(inputSource)
            var nodeList = doc.getElementsByTagName("Table")

            for (nodeIndex in 0 until nodeList.length) {
                var nodeChildList = nodeList.item(nodeIndex).childNodes
                var group = Group()
                var payControl = PayControl()
                var payControlSequence = SequencePayControl()
                var payControlSequenceClient = SequencePayControlClient()
                var client = Client()

                group.xml2Group(nodeChildList)

                if (!(payInfo.groups?.containsKey(group.code) ?: false))
                    throw Error("Grupo $group no se encuentra en los encabezados !!!")
                else
                    group = payInfo.groups?.get(group.code)!!

                payControl.xml2Obj(nodeChildList)
                payControl.group_id = group.code
                payControl.getKey()

                payControlSequence.xml2Obj(nodeChildList)
                payControlSequence.sincronized = false
                payControlSequence.paycontrol_id = payControl.id
                payControlSequence.getKey()

                client.xml2Client(nodeChildList)
                client.group_id = group.code.toLong()

                if (!losclientes.containsKey(client.code)) {
                    client.group_id = group.code.toLong()
                    losclientes[client.code] = client
                } else
                    client = losclientes[client.code]!!

                payControlSequenceClient.xml2Obj(nodeChildList)
                payControlSequenceClient.client_id = client.code.toLong()
                payControlSequenceClient.sequencepaycontrol_id = payControlSequence.id!!
                payControlSequenceClient.getKey()

                if (!lassecuenciasclientes.containsKey(payControlSequenceClient.id)) {
                    lassecuenciasclientes[payControlSequenceClient.id!!] = payControlSequenceClient
                } else
                    throw Error("Secuencia de cliente $payControlSequenceClient esta duplicada !!!")

            }

            Log.e(TAG, "Parse successful: exportGroupAndClientData")

            payInfo?.sequences?.forEach { (i, sequence) ->
                lassecuenciasclientes.forEach { (y, clientSequence) ->
                    if(clientSequence.sequencepaycontrol_id == sequence.id)
                        sequence.payControlSequencePayControlClient.add(clientSequence)
                }
            }

            payInfo?.groups?.forEach { (i, group) ->
                losclientes.forEach { (y, client) ->
                    if (client.group_id == group.code.toLong())
                        group.clients.add(client)
                }
            }
            payInfo.clients = losclientes
            payInfo.clientSequence = lassecuenciasclientes

            return true

        } catch (ex: Exception) {
            Log.e(TAG, "Error parsing: ${ex.message}")
            return false
        }
    }

    fun exportAdvisorName(xmlData: String): String {
        try {
            var documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

            var inputSource = InputSource().apply {
                characterStream = StringReader(xmlData)
            }

            var document = documentBuilder.parse(inputSource)
            var nodeList = document.getElementsByTagName("Table")

            return findNodeByName(nodeList.item(0).childNodes, "NOMBREC")?.textContent ?: ""

        } catch (ex: Exception) {
            Log.e(TAG, "exportAdvisorName: ${ex.message}")
            return ""
        }
    }


    fun data2xml( groups : List<Group>) : ArrayList<Array<String>> {

        val list = ArrayList<Array<String>>()

        for (group in groups) {
            for (paycontrol in group.payControls) {
                if (!paycontrol.local_modification)
                    continue

                if (paycontrol.week1 === PayControl.NEW_NO_SYNC)
                    paycontrol.week1 = PayControl.NEW_WEEK1

                for (secuence in paycontrol.sequences) {
                    if (secuence.sincronized ?: true )
                        continue

                    paycontrol.group = group

                    val header : String

                    header = String.format(
                            "<NewDataSet>\n<Table>\n%s\n</Table>\n</NewDataSet>\n",
                            secuence.obj2xml(true)
                    )


                    val detailClient = StringBuilder()

                    detailClient.append("<NewDataSet>\n")
                    secuence.payControlSequencePayControlClient.forEach {
                        detailClient.append(String.format(
                                "<Table>\n%s\n</Table>\n",
                                it.obj2xml(false)
                        ))
                    }

                    detailClient.append("</NewDataSet>\n")

                    list.add(arrayOf(header, detailClient.toString(), secuence.id.toString(), String.format(
                            "%s %s %02d %02d",
                            paycontrol.group!!.name,
                            paycontrol.date,
                            paycontrol.cycle,
                            secuence.sequence
                    )))
                }
            }
        }

        return list
    }
}
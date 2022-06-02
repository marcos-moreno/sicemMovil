package com.emprendamos.mx.emprendamosfin.ui.adapters

import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.emprendamos.mx.emprendamosfin.R
import android.view.LayoutInflater
import android.widget.TextView
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControlClient
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnSelectedItemInterface
import kotlinx.android.synthetic.main.list_item.view.*
import java.text.NumberFormat


class ClientsByGroupListAdapter(
        val sequences: ArrayList<SequencePayControlClient>, val clickListener: OnSelectedItemInterface) : RecyclerView.Adapter<ClientsByGroupListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_client_details, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sequence = sequences[position]

        holder.bind(sequence) {
            clickListener.onClientSelected(sequence.client!!, sequence)
        }
    }

    override fun getItemCount(): Int {
        return sequences.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(sequence: SequencePayControlClient, listener: (SequencePayControlClient) -> Unit) {
            with(itemView) {
                if (sequence.isEdited) {
                    txtEncabezado.background = ContextCompat.getDrawable(context, R.drawable.background_rounded)
                    txtEncabezado.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite,null))
                } else {
                    txtEncabezado.background = null
                    txtEncabezado.setTextColor(ResourcesCompat.getColor(resources, R.color.colorPrimaryDark,null))
                }

                txtEncabezado.text = context.getString(R.string.cFormatoCodigoGC,sequence.client?.code)
                txtEncabezado2.text = sequence.client?.name

                val txtDetails = String.format(context.getString(R.string.client_details_placeholder),
                        NumberFormat.getCurrencyInstance().format(sequence?.theorical_payment),
                        NumberFormat.getCurrencyInstance().format(sequence?.real_payment),
                        NumberFormat.getCurrencyInstance().format(sequence?.refund),
                        NumberFormat.getCurrencyInstance().format(sequence?.aport),
                        NumberFormat.getCurrencyInstance().format(sequence?.fee),
                        NumberFormat.getCurrencyInstance().format(sequence?.saving)
                )

                txtAdicional.text = txtDetails
                setOnClickListener {
                    listener(sequence)
                }

                setOnLongClickListener {
                    AlertDialog.Builder(context).apply {
                        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_client_details,null,false)
                        val textDetails = dialogView.findViewById<TextView>(R.id.text_client_details)
                        val textName = dialogView.findViewById<TextView>(R.id.text_client_name)
                        textName.text = sequence.client?.name
                        textDetails.text = txtDetails
                        setView(dialogView)
                        show()
                    }
                    true
                }
            }
        }
    }
}
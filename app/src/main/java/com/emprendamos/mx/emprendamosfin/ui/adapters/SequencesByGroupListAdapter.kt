package com.emprendamos.mx.emprendamosfin.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emprendamos.mx.emprendamosfin.R
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControl
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnSelectedItemInterface
import kotlinx.android.synthetic.main.item_sequence_details.view.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date

class SequencesByGroupListAdapter (
        val sequences : ArrayList<SequencePayControl>,
        val date: Long,
        val clickListener: OnSelectedItemInterface) : RecyclerView.Adapter<SequencesByGroupListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sequence_details, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var sequence = sequences[position]
        holder.bind(sequence, date) {
            clickListener.onSequenceSelected(sequence)
        }
    }

    override fun getItemCount(): Int {
        return sequences.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val txt_sequence_resume = itemView.txt_sequence_resume!!

        fun bind(sequence : SequencePayControl, date: Long, listener: (SequencePayControl) -> Unit) {
            with(itemView) {
                text_register.text = SimpleDateFormat("dd/MM/yyyy").format(Date(sequence.fecreg ?: 0))
                text_payment.text = SimpleDateFormat("dd/MM/yyyy").format(Date(date ?: 0))
                text_type.text = sequence?.type

                text_real_payment.text =   NumberFormat.getCurrencyInstance().format(sequence.payControlSequencePayControlClient.sumByDouble { ((it.real_payment) ?: 0.0) })

                text_pago_pactado.text = NumberFormat.getCurrencyInstance().format(sequence.payControlSequencePayControlClient.sumByDouble {
                    ((it.real_payment) ?: 0.0) + ((it.aport) ?: 0.0)
                })

                text_aport.text = NumberFormat.getCurrencyInstance().format(sequence.payControlSequencePayControlClient.sumByDouble { it.aport ?: 0.0 })
                text_refund.text = NumberFormat.getCurrencyInstance().format(sequence.payControlSequencePayControlClient.sumByDouble { it.refund ?: 0.0 })
                text_saving.text = NumberFormat.getCurrencyInstance().format(sequence.payControlSequencePayControlClient.sumByDouble { it.saving ?: 0.0 })
                text_fee.text = NumberFormat.getCurrencyInstance().format(sequence.payControlSequencePayControlClient.sumByDouble { it.fee ?: 0.0 })

                setOnClickListener {
                    listener(sequence)
                }
            }

        }
    }
}
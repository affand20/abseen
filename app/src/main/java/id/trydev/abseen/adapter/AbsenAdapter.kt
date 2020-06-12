package id.trydev.abseen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import id.trydev.abseen.R
import id.trydev.abseen.model.Absen
import java.text.SimpleDateFormat
import java.util.*

class AbsenAdapter(private val context: Context): RecyclerView.Adapter<AbsenAdapter.ViewHolder>() {

    private val listAbsen = mutableListOf<Absen>()
    fun setData(listAbsen: List<Absen>) {
        this.listAbsen.clear()
        this.listAbsen.addAll(listAbsen)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.absen_item, parent, false))
    }

    override fun getItemCount(): Int = listAbsen.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(listAbsen[position])
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val itemBody = view.findViewById<MaterialCardView>(R.id.item_body)
        private val employeeName = view.findViewById<TextView>(R.id.employee_name)
        private val officeLocation = view.findViewById<TextView>(R.id.office_location)
        private val absentDate = view.findViewById<TextView>(R.id.absent_date)
        private val absentTime = view.findViewById<TextView>(R.id.absent_time)

        fun bindItem(item: Absen) {

            val date = SimpleDateFormat("dd/MM/yyyy").parse(item.absentDate)
            val time = SimpleDateFormat("HH:mm").parse(item.absentTime)

            employeeName.text = item.employeeName
            officeLocation.text = item.officeName
            absentDate.text = "${SimpleDateFormat("EEE, dd MMM yyyy", Locale("ID", "IN")).format(date)}"
            absentTime.text = "${SimpleDateFormat("HH:mm", Locale("ID", "IN")).format(time)}"
        }
    }
}
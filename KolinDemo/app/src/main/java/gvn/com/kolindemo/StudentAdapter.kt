package gvn.com.kolindemo

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast


class StudentAdapter(var context : Context, var listStudent : List<Student>) : RecyclerView.Adapter<StudentAdapter.StudentHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentHolder {
        var view : View = LayoutInflater.from(context).inflate(R.layout.item_list_student,parent,false);

        return StudentHolder(view)
    }

    override fun getItemCount(): Int {
        Log.e("ThangPham",listStudent.size.toString());
        return listStudent.size;
    }

    override fun onBindViewHolder(holder: StudentHolder, position: Int) {
        holder.setData(listStudent.get(position),position,context);

    }


    class StudentHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)
    {

        var name : TextView = itemView!!.findViewById(R.id.name) as TextView;
        var age : TextView = itemView!!.findViewById(R.id.age);
        fun setData(data : Student, position: Int,context: Context)
        {
            name.text = data.name;
            age.text = data.age.toString();
            itemView.setOnClickListener(View.OnClickListener {
                Log.e("ThangPham",position.toString());

            })
        }
    }
}
package gvn.com.kolindemo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var listStudent : ArrayList<Student> = ArrayList();
        listStudent.add(Student("Thang",10));
        listStudent.add(Student("Thang1",10));
        listStudent.add(Student("Thang2",10));
        listStudent.add(Student("Thang3",10));
        listStudent.add(Student("Thang4",10));
        listStudent.add(Student("Thang5",10));
        listStudent.add(Student("Thang6",10));
        listStudent.add(Student("Thang7",10));
        var studentAdapter = StudentAdapter(this,listStudent);

        rvListStudent.layoutManager = LinearLayoutManager(this);
        rvListStudent.adapter = studentAdapter;
    }
}

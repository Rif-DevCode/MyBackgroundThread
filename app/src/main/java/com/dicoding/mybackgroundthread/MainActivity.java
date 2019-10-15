package com.dicoding.mybackgroundthread;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements MyAsyncCallback{

    private TextView tvStatus;
    private  TextView tvDesc;

    private final static String INPUT_STRING = "Halo ini Demo AsyncTask!!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tv_status);
        tvDesc = findViewById(R.id.tv_desc);

        //Baris di bawah akan membuat obyek DemoAsync dan menjanlakan AsyncTask dengan inputan berupa obyek string.
        DemoAsync demoAsync = new DemoAsync(this);
        demoAsync.execute(INPUT_STRING);
    }

    //Kode di bawah berfungsi untuk mempersiapkan asynctask
    @Override
    public void onPreExecute() {
        //
        tvStatus.setText(R.string.status_pre);
        tvDesc.setText(INPUT_STRING);

    }

    @Override
    public void onPostExecute(String result) {
        //Terakhir, metode onPostExecute() akan menampilkan hasil proses yang dilakukan di doInBackground().
        tvStatus.setText(R.string.status_post);
        if(result != null){
            tvDesc.setText(result);
        }

    }

    //WeakReference di dalam AsyncTask. WeakReference disarankan untuk menghindari memory leak yang bisa terjadi dalam AsyncTask.
    // Memory leak (kebocoran memori) ini bisa terjadi ketika aplikasi sudah ditutup, akan tetapi proses asynctask masih tetap berjalan.
    // Ketika hal ini terjadi maka seharusnya garbage collector bisa membersihkan variable yang ada di dalam asynctask.
    // Ketika kita tidak menggunakan weakreference maka garbage collector tidak bisa membersihkan variable di asynctask dan akan tercipta memory leak.

    private static class DemoAsync extends AsyncTask<String, Void, String> {
        static final String LOG_ASYNC = "DemoAsync";
        WeakReference<MyAsyncCallback> myListener;
//weakreference digunakan untuk mereferensikan obyek callback, perhatikan kode
        DemoAsync(MyAsyncCallback myListener){
            this.myListener = new WeakReference<>(myListener);
        }


        //onPreExecute
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(LOG_ASYNC, "status : onPreExecute");

            //Kelas DemoAsync menggunakan WeakReference untuk mengisi status yang ada pada bagian callback tersebut.
            //WeakReference dipanggil di bagian berikut:
            MyAsyncCallback myListener = this.myListener.get();
            if(myListener != null){
                myListener.onPreExecute();
            }
        }

        //Kemudian di sini kita melakukan proses sederhana yaitu menambahkan teks "Selamat Belajar!!" ke parameter input.
        // Proses ini memerlukan waktu selama 2 detik (2000 miliseconds). Hasil dari proses tersebut kita kembalikan dalam obyek output.
        // Sekarang seharusnya nilai dari obyek output adalah "Halo ini demo AsyncTask. Selamat Belajar!!."
        @Override
        protected String doInBackground(String... params) {
            Log.d(LOG_ASYNC, "status : doInBackground");
            String output = null;

            try {
                String input = params[0];
                output = input + "Selamat Belajar!!";
                Thread.sleep(2000);
            }catch (Exception e){
                Log.d(LOG_ASYNC, e.getMessage());
            }
            return output;
        }

        //onPostExecute
        //Kode di bawah, yakni onPostExecute berjalan ketika proses doInBackground telah selesai dan
        //akan dijalankan di main thread yang mana state/kondisi ini dapat mengakses view.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(LOG_ASYNC, "status : onPostExecute");

            //Kode di bawah akan mengirimkan informasi bahwa kelas DemoAsync sedang dalam proses onPostExecute atau proses sudah selesai.
            MyAsyncCallback myListener = this.myListener.get();
            if(myListener != null){
                myListener.onPostExecute(result);
            }
        }
    }
}

//membuat Interface
interface MyAsyncCallback{
    void onPreExecute();
    void onPostExecute(String text);
}


package net.kenevans.polar.polarecg;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.polar.polarecg.R;


public class MainActivity extends AppCompatActivity {

    private String TAG = "Polar_ECG";
    private String sharedPrefsKey = "polar_ecg_id";
    private String DEVICE_ID;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        checkBT();
    }

    public void onClickConnect(View view) {
        checkBT();
        DEVICE_ID = sharedPreferences.getString(sharedPrefsKey,"");
        Log.d(TAG,DEVICE_ID);
        if(DEVICE_ID.equals("")){
            showDialog(view);
        } else {
            Toast.makeText(this,getString(R.string.connecting) + " " + DEVICE_ID,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ECGActivity.class);
            intent.putExtra("id", DEVICE_ID);
            startActivity(intent);
        }
    }

    public void onClickChangeID(View view) {
        showDialog(view);
    }

    public void showDialog(View view){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.PolarTheme);
        dialog.setTitle(R.string.device_id_dialog_title);

        View viewInflated = LayoutInflater.from(getApplicationContext()).
                inflate(R.layout.device_id_dialog_layout,(ViewGroup) view.getRootView() , false);

        final EditText input = viewInflated.findViewById(R.id.input);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        DEVICE_ID = sharedPreferences.getString(sharedPrefsKey,"");
        input.setText(DEVICE_ID);
        dialog.setView(viewInflated);

        dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DEVICE_ID = input.getText().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(sharedPrefsKey, DEVICE_ID);
                editor.apply();
            }
        });
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void checkBT(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 2);
        }

        //requestPermissions() method needs to be called when the build SDK version is 23 or above
        if(Build.VERSION.SDK_INT >= 23){
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
    }
}

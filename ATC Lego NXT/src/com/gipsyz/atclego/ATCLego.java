package com.gipsyz.atclego;

import java.io.IOException;
import java.util.Set;

import lejos.nxt.Motor;
import lejos.nxt.Sound;
import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommAndroid;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ATCLego extends Activity implements OnClickListener  {
	
	private NXTConnector conn;
	private Control control;
	//Custom request codes
    private final static int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private final static int REQUEST_ENABLE_BT = 1;
    private final static int REQUEST_CONNECT_DEVICE  = 2;
    //MAC adress of NXT to connect with
    private String adressNXT;
    private BluetoothAdapter mBluetoothAdapter;
    
    private Animation buttonAnimation;

    private boolean sounde;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);     
        
        //Button Animations
        Animation animation = new TranslateAnimation(-500, 0,0, 0);
        animation.setDuration(500);
        ImageView atc=(ImageView)findViewById(R.id.imageView1);
        atc.startAnimation(animation);
       
        animation = new TranslateAnimation(500,0,0, 0);
        animation.setDuration(500);
        TextView tv=(TextView) findViewById(R.id.textView1);
        tv.startAnimation(animation); 
        
        buttonAnimation = new TranslateAnimation(-500, 0,0, 0);
        buttonAnimation.setDuration(500);
       
        //Animating buttongs
        new CountDownTimer(400,100){
        	int i=0;
        	int [] ids={R.id.bluetooth,R.id.sincronizar,R.id.conectar,R.id.info};
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				Button b=((Button) findViewById(ids[i]));
				b.setVisibility(View.VISIBLE);
				b.startAnimation(buttonAnimation);
				
			}

			@Override
			public void onTick(long arg0) {
				Button b=((Button) findViewById(ids[i]));
				b.setVisibility(View.VISIBLE);
				b.startAnimation(buttonAnimation);				
				buttonAnimation = new TranslateAnimation(-500, 0,0, 0);
			    buttonAnimation.setDuration(500);
				i++;
			}
        	
        }.start();

        sounde = true;
        //Connect button listeners
        Button conectar=(Button)findViewById(R.id.conectar);
        conectar.setOnClickListener(this);
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        //If none bluetooth device, the app finishes
        if (mBluetoothAdapter == null) {
        	Toast.makeText(this, "Tu SmartPhone no tiene Bluetooth", 1500).show();
        	finish();
        }
        
        if (mBluetoothAdapter.isEnabled()) {
        	((Button)findViewById(R.id.sincronizar)).setEnabled(true);
         	((Button)findViewById(R.id.bluetooth)).setEnabled(false);
        	((Button)findViewById(R.id.conectar)).setEnabled(false);
        	//Cheks for paired devices
        	checkPaired();
        }
        else{
        	((Button)findViewById(R.id.sincronizar)).setEnabled(false);
        	((Button)findViewById(R.id.conectar)).setEnabled(false);
        }
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(conn!=null){
			Motor.B.stop();
			Motor.C.stop();
			desconectarNXT();
		}
		if (mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.disable();
		}
		
	}
	
	@Override
	protected void onStop() {
		super.onDestroy();
		if(conn!=null){
			Motor.B.stop();
			Motor.C.stop();
			desconectarNXT();
		}
		
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		}

	private void checkPaired(){
		boolean emparejados = false;
		//Gets paired devices
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				//Only check LEGO devices
				if (device.getAddress().startsWith(NXTCommAndroid.OUI_LEGO)){
					emparejados = true;
				}
			}
		}
		if(emparejados)
	    	((Button)findViewById(R.id.conectar)).setEnabled(true);
		else{
	          Toast.makeText(this, "Tu SmartPhone no esta vinculado con ningun NXT, tendras que sincronizar con alguno", 1500).show();
		}
			
	
	}

	//Method invoked when activate Bluetooth pressed
	public void activarBluetooth(View v){
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        this.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
       
    

	}
	
	//Method invoked when syncronize NXT pressed
	public void sincronizarNXT(View v){
		 Intent serverIntent = new Intent(this, DeviceListActivity.class);
         startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}
	
	private void conectarNXT() {
		//Create new NXT Connector
		conn = new NXTConnector();
		conn.setDebug(true);
		//Set Debug listeners to show
		conn.addLogListener(new NXTCommLogListener() {

			public void logEvent(String arg0) {
				Log.e("Lego NXT", arg0);
			}

			public void logEvent(Throwable arg0) {
				Log.e("Lego NXT", arg0.getMessage(), arg0);
			}
		});

		boolean connected;

		//If no adress specified, connect to any NXT
		if (adressNXT == null)
			connected = conn.connectTo("btspp://NXT", NXTComm.LCP);
		else
			connected = conn.connectTo(null, adressNXT,
					NXTCommFactory.BLUETOOTH, NXTComm.LCP);
		//If the conection intent fails, the smartphone vibrates and finish 
		if (!connected) {
			Log.e("ERROR", "Failed to connect to any NXT");
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(500);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finish();

		} else {

			NXTCommand.getSingleton().setNXTComm(conn.getNXTComm());
			//Change from main view to custom control view
			verPantallaControl();
			//Shows a Toast with connected NXT information
			Toast toast = Toast.makeText(this, conn.getNXTInfo().name + " "
					+ conn.getNXTInfo().deviceAddress , 1000);
			toast.show();
			//Play tone in NXT
			Sound.playTone(1000, 100);
		}
	}
  
	private void verPantallaControl() {
		setContentView(R.layout.remote);
		control=new Control(sounde);
		SeekBar sk = (SeekBar) findViewById(R.id.nivel);
		sk.setOnSeekBarChangeListener(control);
		
	}
	
	//Close conection with NXT
	private void desconectarNXT(){
		try {
			if(conn!=null)
				conn.close();
		} catch (IOException e) {
			Log.e("Error", "Al intentar cerrar el socket");
		} 
    	
    }
	
	//Method invoked when voice action button pressed
	public void comandoVoz(View v){
		startVoiceRecognitionActivity();
	}
	
	//Starts a new voice recognition intent
	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "ATC Killer Command");
		
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	/**
	 * Handle the results from the recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Gets results from Voice recognition intent, only when the result is OK
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			//Invoke choice action method
			control.comandoVoz(data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));
		}
		//Result form bluetooth activate requies
		else if(requestCode == REQUEST_ENABLE_BT){
              if (resultCode == Activity.RESULT_OK) {
	            	((Button)findViewById(R.id.sincronizar)).setEnabled(true);
	              	((Button)findViewById(R.id.bluetooth)).setEnabled(false);
	             	checkPaired();

              } else {
            	  Toast.makeText(this, "Error al activar Bluetooth", 1500).show();
              }
          
		}
		//Request from synchronize NXT request
		else if(requestCode== REQUEST_CONNECT_DEVICE){
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                adressNXT = data.getExtras().getString(DeviceListActivity.EXTRA_NXT_ADDRESS);            
                ((Button)findViewById(R.id.conectar)).setEnabled(true);
            	Log.d("Dirrecion del NXT",adressNXT );
            }
        }
		super.onActivityResult(requestCode, resultCode, data);
	
	}
	

	//Method invoked from onClick listener of Connect Button
	public void onClick(View arg0) {
		conectarNXT();
	}
	
	public void adelante(View v){
		control.adelante();
	}
	public void atras(View v){
		control.atras();

	}
	public void izquierda(View v){
		control.izquierda();

	}
	public void derecha(View v){
		control.derecha();

	}
	public void izquierdaAdelante(View v){
		control.izquierdaAdelante();

	}
	public void izquierdaAtras(View v){
		control.izquierdaAtras();

	}
	public void derechaAdelante(View v){
		control.derechaAdelante();

	}
	public void derechaAtras(View v){
		control.derechaAtras();

	}
	
	public void stop(View v){
		control.stop();

	}
	public void juega(View v){
		control.juega();

	}
	public void saluda(View v){
		control.saluda();

	}
	public void ataca(View v){
		control.ataca();

	}
	
	public void sounde(View v){
		if(sounde)
			((Button)findViewById(R.id.sounde)).setBackgroundResource(R.drawable.soundep);
		else
			((Button)findViewById(R.id.sounde)).setBackgroundResource(R.drawable.sounde);
		sounde=!sounde;
	}
	
	public void info(View v){
		 new AlertDialog.Builder(this).setPositiveButton("OK", new android.content.DialogInterface.OnClickListener(){
			public void onClick(DialogInterface arg0, int arg1) {
			}
		 }).setMessage("App for controlling a Lego NXT MindStorm robot from a Android " +
		 		"device with touch and voice control. Developed as a project" +
		 		" for Mecatronica class of Escuela Universitaria de " +
		 		"Informatica, UPM, department of ATC. 2011-2012 course").show();
	}



}
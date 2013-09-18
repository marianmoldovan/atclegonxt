package com.gipsyz.atclego;

import java.util.ArrayList;
import java.util.Random;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Control implements OnSeekBarChangeListener {

	/**
	 * Motores accedidos de forma estatica, Motor.B, Motor.C
	 */

	private UltrasonicSensor sensorUltrasonico;
	private TouchSensor sensorDelantero;
	private TouchSensor sensorTrasero;

	private int potenciaMotor = 80;
	private Thread threadAtaca;

	private int accion = -1;

	private boolean sound;
	
	public Control(boolean sound) {
		this.sensorUltrasonico = new UltrasonicSensor(SensorPort.S2);
		this.sensorDelantero = new TouchSensor(SensorPort.S4);
		this.sensorTrasero = new TouchSensor(SensorPort.S1);

		this.sound=sound;
		
		Motor.B.setPower(this.potenciaMotor);
		Motor.C.setPower(this.potenciaMotor);
	}

	public synchronized boolean puedeAdelante() {
		if (sensorUltrasonico.getDistance() < 25)
			return false;
		else if (sensorDelantero.isPressed())
			return false;
		else
			return true;
	}

	public synchronized boolean puedeAtras() {
		if (sensorTrasero.isPressed())
			return false;
		else
			return true;

	}

	private boolean esGiro(int ultimaAccion) {
		if (ultimaAccion < 9 && ultimaAccion > 2 || ultimaAccion == 27)
			return true;
		else
			return false;
	}

	public void adelante() {
		if (this.accion == 1 || this.accion == 10)
			return;
		int ultimaAccion = this.accion;
		this.accion = 1;

		if (!puedeAdelante())
			return;

		if (esGiro(ultimaAccion)) {
			Motor.B.setPower(this.potenciaMotor);
			Motor.C.setPower(this.potenciaMotor);
		}

		Motor.B.forward();
		Motor.C.forward();

		new Thread() {
			@Override
			public void run() {
				while (accion == 1) {
					if (!puedeAdelante()) {
						parar();
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void atras() {
		if (this.accion == 2 || this.accion == 10)
			return;
		int ultimaAccion = this.accion;
		this.accion = 2;

		if (!puedeAtras())
			return;

		if (esGiro(ultimaAccion)) {
			Motor.B.setPower(this.potenciaMotor);
			Motor.C.setPower(this.potenciaMotor);
		}

		Motor.B.backward();
		Motor.C.backward();

		new Thread() {

			@Override
			public void run() {
				while (accion == 2) {
					if (!puedeAtras())
						parar();
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}.start();
	}

	public void izquierda() {
		if (this.accion == 3 || this.accion == 10)
			return;
		this.accion = 3;

		if (!puedeAdelante())
			return;

		Motor.C.flt();
		Motor.B.setPower(this.potenciaMotor);
		Motor.B.forward();

		new Thread() {

			@Override
			public void run() {
				while (accion == 3) {
					if (!puedeAdelante())
						parar();
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}.start();

		Log.d("Action", "Izquierda");
	}

	public void derecha() {
		if (this.accion == 4 || this.accion == 10)
			return;
		this.accion = 4;

		if (!puedeAdelante())
			return;

		Motor.B.flt();
		Motor.C.setPower(this.potenciaMotor);
		Motor.C.forward();

		new Thread() {

			@Override
			public void run() {
				while (accion == 4) {
					if (!puedeAdelante())
						parar();
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}.start();

		Log.d("Action", "Derecha");

	}

	public void izquierdaAdelante() {
		if (this.accion == 5 || this.accion == 10)
			return;
		this.accion = 5;

		if (!puedeAdelante())
			return;

		if (this.potenciaMotor > 90)
			this.potenciaMotor = 90;

		Motor.B.setPower(this.potenciaMotor + 10);
		Motor.C.setPower(this.potenciaMotor - 10);

		Motor.B.forward();
		Motor.C.forward();

		new Thread() {

			@Override
			public void run() {
				while (accion == 5) {
					if (!puedeAdelante())
						parar();
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}.start();

		Log.d("Action", "Izquierda");
	}

	public void derechaAdelante() {
		if (this.accion == 6 || this.accion == 10)
			return;
		this.accion = 6;

		if (!puedeAdelante())
			return;

		if (this.potenciaMotor > 90)
			this.potenciaMotor = 90;

		Motor.B.setPower(this.potenciaMotor - 10);
		Motor.C.setPower(this.potenciaMotor + 10);
		Motor.B.forward();
		Motor.C.forward();

		new Thread() {

			@Override
			public void run() {
				while (accion == 6) {
					if (!puedeAdelante())
						parar();

					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}.start();

	}

	public void izquierdaAtras() {
		if (this.accion == 7 || this.accion == 10)
			return;
		this.accion = 7;

		if (!puedeAtras())
			return;

		if (this.potenciaMotor > 90)
			this.potenciaMotor = 90;

		Motor.B.setPower(this.potenciaMotor + 10);
		Motor.C.setPower(this.potenciaMotor - 10);
		Motor.B.backward();
		Motor.C.backward();

		new Thread() {

			@Override
			public void run() {
				while (accion == 7) {
					if (!puedeAtras())
						parar();

					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}.start();

	}

	public void derechaAtras() {
		if (this.accion == 8 || this.accion == 10)
			return;
		this.accion = 8;

		if (!puedeAtras())
			return;

		if (this.potenciaMotor > 90)
			this.potenciaMotor = 90;

		Motor.B.setPower(this.potenciaMotor - 10);
		Motor.C.setPower(this.potenciaMotor + 10);
		Motor.B.backward();
		Motor.C.backward();

		new Thread() {

			@Override
			public void run() {
				while (accion == 8) {
					if (!puedeAtras())
						parar();

					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}.start();

	}

	public void stop() {

		this.accion = 27;
		if(isSound())
			Sound.playSoundFile("para.rso");
		Motor.B.stop();
		Motor.C.stop();
		Log.d("Action", "Stop");
	}

	public void parar() {
		this.accion = 27;
		if(isSound())
			Sound.playSoundFile("para.rso");
		Motor.B.stop();
		Motor.C.stop();
		Log.d("Action", "Stop");
	}

	public void juega() {
		if (this.accion == 10)
			return;
		this.accion = 10;
		Motor.B.resetTachoCount();
		Motor.C.resetTachoCount();
		Motor.B.setPower(80);
		Motor.C.setPower(80);

		new Thread() {

			@Override
			public void run() {
				Motor.B.forward();
				Motor.C.backward();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Motor.B.flt();
				Motor.C.flt();
				if (this.distanciaAceptable())
					irAdelante();
				else
					this.buscarDireccion();
			}

			private void irAdelante() {

				Motor.B.setPower(90);
				Motor.C.setPower(90);

				Motor.B.forward();
				Motor.C.forward();
				
				int distancia = sensorUltrasonico.getDistance();
				while (accion == 10 && distancia > 25
						&& !sensorDelantero.isPressed()) {
					Log.d("Debug","Distancia "+distancia);
					distancia = sensorUltrasonico.getDistance();
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				Motor.B.stop();
				Motor.C.stop();
				if (accion != 10)
					return;
				if (sensorDelantero.isPressed()
						|| sensorUltrasonico.getDistance() < 20) {
					Motor.C.backward();
					Motor.B.backward();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Motor.B.stop();
					Motor.C.stop();
				}
				if (accion != 10)
					return;
				this.buscarDireccion();

			}

			private void buscarDireccion() {
				if (accion != 10)
					return;
				int dDelante = sensorUltrasonico.getDistance();
				int dDerecha = -1;
				int dAtras = -1;
				int dIzquierda = -1;
				int antes = dDelante;
				girarNoventaDerecha();
				dDerecha = sensorUltrasonico.getDistance();
				int despues = dDerecha;
				if(antes!=255 && antes-despues<5 && antes-despues>-5){
					irAtras();
					return;
				}
				if (dDerecha > 60 && new Random().nextBoolean()) {
						irAdelante();
						return;
				} 
				
				antes = despues;
				girarNoventaDerecha();
				dAtras = sensorUltrasonico.getDistance();
				despues = dAtras;
				
				if(antes!=255 && antes-despues<5 && antes-despues>-5){
					irAtras();
					return;
				}
				if (dAtras > 100 && new Random().nextBoolean()) {
						irAdelante();
						return;
				} 
				
				antes = despues;
				girarNoventaDerecha();
				dIzquierda= sensorUltrasonico.getDistance();
				despues = dIzquierda;
				
				if(antes!=255 && antes-despues<5 && antes-despues>-5){
					irAtras();
					return;
				}
				if (dIzquierda > 60 && new Random().nextBoolean()) {
						irAdelante();
						return;
				} 
				
				if(dIzquierda < 30 && dAtras < 30 && dDerecha < 30)
					buscarDireccionMinima();
				else{
					if(dIzquierda > dAtras && dIzquierda > dDerecha){
						irAdelante();
					}
					else if (dAtras > dIzquierda && dAtras >dDerecha){
						girarCuarentaYCincoIzquierda();
						irAdelante();
					}
					else{
						girarNoventaIzquierda();
						irAdelante();
					}
				}
				

			}
			private void buscarDireccionMinima() {
				girarCuarentaYCincoIzquierda();
				if(distanciaAceptable()){
					irAdelante();
					return;
				}
				girarCuarentaYCincoIzquierda();
				if(distanciaAceptable()){
					irAdelante();
					return;
				}
				girarCuarentaYCincoIzquierda();
				if(distanciaAceptable()){
					irAdelante();
					return;
				}
				girarCuarentaYCincoIzquierda();
				if(distanciaAceptable()){
					irAdelante();
					return;
				}
				girarCuarentaYCincoIzquierda();
				if(distanciaAceptable()){
					irAdelante();
					return;
				}
				girarCuarentaYCincoIzquierda();
				if(distanciaAceptable()){
					irAdelante();
					return;
				}
				girarCuarentaYCincoIzquierda();
				if(distanciaAceptable()){
					irAdelante();
					return;
				}
				girarCuarentaYCincoIzquierda();
				if(distanciaAceptable()){
					irAdelante();
					return;
				}
				
			}

			private void irAtras() {
				Motor.C.backward();
				Motor.B.backward();
				try {
					Thread.sleep(1200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Motor.B.flt();
				Motor.C.flt();
				
				buscarDireccion();
			}

			/*private void buscarDireccion() {
				if (accion != 10)
					return;
				int dDelante = sensorUltrasonico.getDistance();
				int dDerecha = -1;
				int dAtras = -1;
				int dIzquierda = -1;
				girarNoventaDerecha();
				dDerecha = sensorUltrasonico.getDistance();
				if (dDerecha > 60) {
					if (new Random().nextBoolean())
						this.adelante();
					else {
						girarCientoOchentaDerecha();
						dIzquierda = sensorUltrasonico.getDistance();
						if (dDerecha < 50 && dIzquierda < 50) {
							if (new Random().nextBoolean()) {
								girarNoventaIzquierda();
								this.adelante();
							} else {
								Motor.C.backward();
								Motor.B.backward();
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if (accion != 10)
									return;
								this.buscarDireccion();
							}
						} else if (dDerecha > dIzquierda) {
							if (accion != 10)
								return;
							girarCientoOchentaDerecha();
							this.adelante();
						} else {
							this.adelante();
						}

					}
				} else {

					girarCientoOchentaDerecha();
					dIzquierda = sensorUltrasonico.getDistance();
					if (dDerecha < 50 && dIzquierda < 50) {
						if (new Random().nextBoolean()) {
							girarNoventaIzquierda();
							this.adelante();
						} else {
							Motor.C.backward();
							Motor.B.backward();
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (accion != 10)
								return;
							this.buscarDireccion();
						}
					} else if (dDerecha > dIzquierda) {
						if (accion != 10)
							return;
						girarCientoOchentaDerecha();
						this.adelante();
					} else {
						this.adelante();
					}

				}

			}*/

			private boolean distanciaAceptable() {
				if (sensorUltrasonico.getDistance() > 50)
					return true;
				else
					return false;
			}

		}.start();

		Log.d("Action", "Juega");

	}

	public void saluda() {
		if (this.accion == 11)
			return;
		
		if(isSound())
			Sound.playSoundFile("hola.rso");
		Log.d("Action", "Saluda");

	}

	public void ataca() {
		if (this.accion == 9 || this.accion == 10)
			return;
		this.accion = 9;

		this.threadAtaca = new Thread() {

			@Override
			public void run() {

				this.buscarVictima();

				if(isSound())
					Sound.playSoundFile("ataca.rso");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (accion != 9)
					return;
				potenciaMotor = 90;
				Motor.B.setPower(potenciaMotor);
				Motor.C.setPower(potenciaMotor);
				
				
				if (accion != 9)
					return;
				do {
					Motor.B.forward();
					Motor.C.forward();
				} while (!sensorDelantero.isPressed() && accion == 9);
				if (accion != 9)
					return;

				Motor.B.backward();
				Motor.C.backward();
				if (accion != 9)
					return;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Motor.B.stop();
				Motor.C.stop();
				if (accion != 9)
					return;
				if(isSound())
					Sound.playSoundFile("cobarde.rso");

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (accion != 9)
					return;
				do {

					Motor.B.forward();
					Motor.C.forward();
				} while (!sensorDelantero.isPressed() && accion == 9);
				Motor.B.stop();
				Motor.C.stop();
			}

			private void buscarVictima() {
				Log.d("Victima","Adelante");
				int distanciaAd = sensorUltrasonico.getDistance();
				if(distanciaAd > 50)
					return;
				Log.d("Victima","Izquierda");
				girarNoventaIzquierda();
				int distanciaIz = sensorUltrasonico.getDistance();
				if(distanciaIz > 50)
					return;
				Log.d("Victima","Atras");
				girarNoventaIzquierda();
				int distanciaAt = sensorUltrasonico.getDistance();
				if(distanciaAt > 50)
					return;
				Log.d("Victima","Derecha");
				girarNoventaIzquierda();
				int distanciaDe = sensorUltrasonico.getDistance();
				if(distanciaDe > 50)
					return;
				
				
			}
		};

		this.threadAtaca.start();

		Log.d("Action", "Ataca");
	}

	public void girarNoventaIzquierda() {
		Motor.B.resetTachoCount();
		Motor.B.rotate(360);
	}

	public void girarNoventaDerecha() {
		Motor.C.resetTachoCount();
		Motor.C.rotate(360);
	}

	public void girarCientoOchentaIzquierda() {
		Motor.B.resetTachoCount();
		Motor.B.rotate(720);

	}

	public void girarCientoOchentaDerecha() {
		Motor.C.resetTachoCount();
		Motor.C.rotate(720);

	}

	public void girarCuarentaYCincoDerecha() {
		Motor.C.resetTachoCount();
		Motor.C.rotate(180);

	}

	public void girarCuarentaYCincoIzquierda() {
		Motor.B.resetTachoCount();
		Motor.B.rotate(180);

	}

	/**
	 * @param progress
	 *            valor del seekBar, valores entre 0 y 100
	 * @param fromUser
	 *            indica si el valor proviene de un movimiento del usuario
	 */
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if(this.accion == 9 || this.accion == 10 || this.accion == 11)
			return;
		progress = 30 + (int) (progress * 0.7);
		Log.d("Nivel", "" + progress);
		
		this.potenciaMotor = progress;

		if (this.accion != 5 || this.accion != 6 || this.accion != 7
				|| this.accion != 8) {
			Motor.B.setPower(progress);
			Motor.C.setPower(progress);
		} else {
			Motor.B.flt();
			Motor.C.flt();
		}

		switch (this.accion) {
		case 1:
			Motor.B.forward();
			Motor.C.forward();
			break;
		case 2:
			Motor.B.backward();
			Motor.C.backward();
			break;
		case 3:
			Motor.B.forward();
			break;
		case 4:
			Motor.C.forward();
			break;
		case 5:
			Motor.B.setPower(progress + 10);
			Motor.C.setPower(progress - 10);
			Motor.B.forward();
			Motor.C.forward();
			break;
		case 6:
			Motor.B.setPower(progress - 10);
			Motor.C.setPower(progress + 10);
			Motor.B.forward();
			Motor.C.forward();
			break;
		case 7:
			Motor.B.setPower(progress + 10);
			Motor.C.setPower(progress - 10);
			Motor.B.backward();
			Motor.C.backward();
			break;
		case 8:
			Motor.B.setPower(progress - 10);
			Motor.C.setPower(progress + 10);
			Motor.B.backward();
			Motor.C.backward();
			break;
		}
	}

	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	public void comandoVoz(ArrayList<String> stringArrayListExtra) {
		int distancia = Integer.MAX_VALUE;
		Accion accion = null;
		for (String s : stringArrayListExtra) {
			for (Accion aux : Accion.values()) {
				int comparacion = Control.getLevenshteinDistance(s,
						aux.getLexema());
				if (comparacion < distancia) {
					accion = aux;
					distancia = comparacion;
				}
			}
		}
		
		this.seleccionarAccion(accion);

	}

	private void seleccionarAccion(Accion accion) {
		switch (accion.getId()) {
		case 1:
			this.adelante();
			break;
		case 2:
			this.atras();
			break;
		case 3:
			this.izquierda();
			break;
		case 4:
			this.derecha();
			break;
		case 5: // this.potenciaMotorB = this.potenciaMotorB+10;
			this.izquierdaAdelante();
			break;
		case 6: // this.potenciaMotorB = this.potenciaMotorB-40;
			this.izquierdaAtras();
			break;
		case 7: // this.potenciaMotorC = this.potenciaMotorC+10;
			this.derechaAdelante();
			break;
		case 8: // this.potenciaMotorC = this.potenciaMotorC-40;
			this.derechaAtras();
			break;
		case 9:
			this.ataca();
			break;
		case 10:
			this.juega();
			break;
		case 11:
			this.saluda();
			break;
		case 27:
			this.stop();
			break;
		}
	}

	public static int getLevenshteinDistance(String s, String t) {
		if (s == null || t == null) {
			throw new IllegalArgumentException("Cadena vacia");
		}

		int n = s.length();
		int m = t.length();

		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}

		int p[] = new int[n + 1];
		int d[] = new int[n + 1];
		int _d[];

		int i;
		int j;
		char t_j;
		int cost;

		for (i = 0; i <= n; i++) {
			p[i] = i;
		}

		for (j = 1; j <= m; j++) {
			t_j = t.charAt(j - 1);
			d[0] = j;

			for (i = 1; i <= n; i++) {
				cost = s.charAt(i - 1) == t_j ? 0 : 1;
				d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1]
						+ cost);
			}
			_d = p;
			p = d;
			d = _d;
		}
		return p[n];
	}

	

	private boolean isSound() {
		return sound;
	}
}

enum Accion {

	ADELANTE(1, "adelante"), ATRAS(2, "atras"), IZQUIERDA(3, "izquierda"), DERECHA(
			4, "derecha"), STOP(27, "stop"), PARA(27, "para"), JUEGA(10, "juega"), ATACA(9,
			"ataca"), SALUDA(11, "saluda");

	private int id;
	private String lexema;

	Accion(int id, String lexema) {
		this.id = id;
		this.lexema = lexema;
	}

	public int getId() {
		return this.id;
	}

	public String getLexema() {
		return this.lexema;
	}
}
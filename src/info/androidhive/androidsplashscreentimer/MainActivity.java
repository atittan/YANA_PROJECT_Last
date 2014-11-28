package info.androidhive.androidsplashscreentimer;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.speech.tts.TextToSpeech;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

public class MainActivity extends Activity implements
TextToSpeech.OnInitListener{
	private TextToSpeech tts = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tts = new TextToSpeech(this, this);
		Button btnDownload = (Button) findViewById(R.id.download);
		Button stopBtn = (Button) findViewById(R.id.stopButton);
		OnClickListener stopBtnListener = new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				second.cancel();
				inputURLflag = false;
			}
		};
		OnClickListener downloadListener = new OnClickListener() {	
		
			public void onClick(View v) {
				if(inputURLflag == false)
					testStart();
				inputURLflag = true;
					if (isNetworkAvailable()) {
						//EditText url = (EditText) findViewById(R.id.url);
						DownloadTask downloadTask = new DownloadTask();
						downloadTask.execute("http://192.168.43.12");
				} else {
					Toast.makeText(getBaseContext(), 
							"이용할 수 없는 네트워크입니다.", Toast.LENGTH_SHORT)
							.show();
				}
			}
		};
		btnDownload.setOnClickListener(downloadListener);
		stopBtn.setOnClickListener(stopBtnListener);
	}
	@Override
	public void onDestroy() {
		// Don't forget to shutdown!
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}
	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub

		if (status == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.KOREA);

			// tts.setPitch(5); // set pitch level

			// tts.setSpeechRate(2); // set speech speed rate

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "Language is not supported");
			} else {
				//btnSpeak.setEnabled(true);
				//speakOut();
			}
		} else {
			Log.e("TTS", "Initilization Failed");
		}
	}
	private void speakOut(String text) {

		tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}
	public void readURL() {
		
		//	urlCheckTimer();
			if (isNetworkAvailable()) {
				//EditText url = (EditText) findViewById(R.id.url);
				DownloadTask downloadTask = new DownloadTask();//(url.getText().toString()
				downloadTask.execute("http://192.168.43.12");
		} else {
			Toast.makeText(getBaseContext(),
					"이용할 수 없는 네트워크 입니다.", Toast.LENGTH_SHORT)
					.show();
		}
	}
	
	private TimerTask second;
	private TimerTask second2;
	private TimerTask second_exceptState;
	private final Handler handler = new Handler();
	private int timer_sec;
	private int count;
	private int timer_sec_second;
	private int count_second;
	private int timer_sec_exceptState;
	private int count_exceptState;
	public boolean inputURLflag = false;
	public void testStart() {

			timer_sec = 0;
			count = 0;

			second = new TimerTask() {
				@Override
				public void run() {
					Log.i("Test", "Timer start");
					timer_sec++;
					if(timer_sec % 2 == 0 && inputURLflag == true)
					{
						//Update();
						readURL();
					}
				}
			};

			Timer timer = new Timer();
			timer.schedule(second, 0, 1000);
	}
	public void exceptionMode() {

		String plusString = null;
		timer_sec_exceptState = 0;
		count_exceptState = 0;
		exception_flag = true;
		if(die_check_timer == true)
		{
			second2.cancel();
			tricktimer = false;
		}
		statusSbd = false;
		die_check_timer = false;
		second_exceptState = new TimerTask() {
			@Override
			public void run() {
				Log.i("Test_exception", "Timer exception state start");
				timer_sec_exceptState++;
				if(timer_sec_exceptState == 10)
				{
					//Toast.makeText(getBaseContext(), "10초 경과, 문제가 발생하였습니다.",
					//		Toast.LENGTH_SHORT).show();
					speakOut("10초 경과, 독거노인의 상태를 확인해주세요.");
					second_exceptState.cancel();
					exception_flag = false;
				}
			}
		};

		Timer timer = new Timer();
		timer.schedule(second_exceptState, 0, 1000);
}
	public void testStart_die() {

		timer_sec_second = 0;
		count_second = 0;

		second2 = new TimerTask() {
			@Override
			public void run() {
				Log.i("Test_diecheck", "Timer diecheck start");
				timer_sec_second++;

				if(die_check_timer == true){
					if(timer_sec_second % 5 == 0 && timer_sec_second < 10)
					{
						String remainTime = timer_sec_second + "초 경과하였습니다.";
						speakOut(remainTime);
					}
					else if(timer_sec_second == 10)
					{
						//Toast.makeText(getBaseContext(), "10초 경과, 문제가 발생하였습니다.",
						//		Toast.LENGTH_SHORT).show();
						speakOut("10초 경과, 문제가 발생하였습니다.");
					}
					else if(timer_sec_second == 15)
					{
						//Toast.makeText(getApplicationContext(), "시간 경과", Toast.LENGTH_SHORT).show();
						dieStatus = true;
						speakOut("문제가 발생하였습니다. 연락처 연결하겠습니다.");
						// second.cancel();
					}
					else if(timer_sec_second == 19)
					{
						Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:01047306411"));
						startActivity(intent);
						second2.cancel();
					}
				}
			}
		};
		Timer timer = new Timer();
		timer.schedule(second2, 0, 1000);
}
	public boolean die_check_timer = false;
	protected void Update() {

			Runnable updater = new Runnable() {
				public void run() {
					String sendSpeakdata = timer_sec + "초";
					speakOut(sendSpeakdata);
				}
			};
			handler.post(updater);
	}
	private boolean isNetworkAvailable() {
		boolean available = false;
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isAvailable())
			available = true;

		return available;
	}

	private String downloadUrl(String strUrl) throws IOException {
		String s = null;
		byte[] buffer= new byte[1000];
		InputStream iStream = null;
		try {
			URL url = new URL(strUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			urlConnection.connect();

			iStream = urlConnection.getInputStream();
			iStream.read(buffer);
			s = new String(buffer);
		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
		}
		return s;
	}
	int timeToGo = 40; // 60second
	Thread t2 = null;
	// 이게 잘못 구현한 타이머
	private void dieCheckTimer()
	{
		t2 = new Thread(new Runnable()
		{
			public void run()
			{
				while (true)
				{
					try
					{
						Thread.sleep(1000);
					} 
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					timeToGo--;
					showTimeGo();
					if(timeToGo == 10)
					{
						Toast.makeText(getBaseContext(), "30초 경과, 문제가 발생하였습니다.",
								Toast.LENGTH_SHORT).show();
						speakOut("30초 경과, 문제가 발생하였습니다.");
					}
					else if(timeToGo == 0 && dieStatus == false)
					{
						runOnUiThread(new Runnable()
						{
							public void run()
							{
								Toast.makeText(getApplicationContext(), "Timeout", Toast.LENGTH_SHORT).show();
								timeToGo = 40;
								showTimeGo();
								dieStatus = true;
								speakOut("문제가 발생하였습니다. 연락처 연결하겠습니다.");
								Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("tel:01047306411"));
							}
						});
						return;
					}
				}
			}
		}
		);
		t2.start();
	}

	private void showTimeGo()
	{
		runOnUiThread(new Runnable()
		{
			public void run()
			{
				//editTextTime.setText(min+":"+sec);
				if(timeToGo % 5 == 0 && timeToGo > 10)
				{
					String remainTime = timeToGo + "초 남았습니다.";
					speakOut(remainTime);
				}
			}
		});
	}
	boolean tricktimer = false;
	public void checkTimer(int statusData)
    {

    	if(statusData == 1 && statusSbd == true)
    	{
    		die_check_timer = true;
    		if(tricktimer == false)
    		{
    			testStart_die();
    			tricktimer = true;
    		}
    		
    	}
    	else if(statusData == 0)
    	{
    		if(die_check_timer == true)
    		{
    			speakOut("Timer 작동 중지");
    			second2.cancel();
    			tricktimer = false;
    		}
    		statusSbd = false;
    		die_check_timer = false;
    	}
    }

	public boolean dieStatus = false;
	public boolean statusSbd = false;
	public boolean exception_flag = false;
	private class DownloadTask extends AsyncTask<String, Integer, String> {
		String s = null;

		@Override
		protected String doInBackground(String... url) {

			try {
				s = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return s;
		}
		int statusData = 0;

		@Override
		protected void onPostExecute(String result) {
		    TextView statusDescription = (TextView)findViewById(R.id.statusDescription);
			TextView normalMode = (TextView)findViewById(R.id.normalMode);
			TextView sleepMode = (TextView)findViewById(R.id.sleepMode);
			TextView outMode = (TextView)findViewById(R.id.outMode);
			
			ImageView normalI = (ImageView) findViewById(R.id.normalI);
			
			ImageView sleepI = (ImageView) findViewById(R.id.sleepI);
			
			ImageView outsideI = (ImageView) findViewById(R.id.outsideI);
			
			
			
			
			String speakData = null;
			String enableColor = "#ff0000";
			String disableColor = "#000000";
			if(result.contains("*") == true)
			{
				String[] tempData = result.split("\\*");
				
				for(int i =0;i<tempData.length;i++)
				{
					tempData[i] = tempData[i].trim();
				}
				if(tempData[1].contains("Precent State")==true)
				{
					normalMode.setTextColor(Color.parseColor(enableColor));
					normalI.setImageResource(R.drawable.normal2);
					sleepMode.setTextColor(Color.parseColor(disableColor));
					outMode.setTextColor(Color.parseColor(disableColor));
					
					if(exception_flag == true)
					{
						second_exceptState.cancel();
					}
					if(tempData[2].contains("Somebody is in this area!")== true)
					{
						speakData = "동작감지 확인중입니다. 현재 정상적인 상태입니다.";
						statusDescription.setText(speakData);
						//speakOut(speakData);
						statusData = 0;
					}
					else if(tempData[2].contains("No one")==true)
					{
						speakData = "동작감지 확인중입니다. 현재 이상을 감지하였습니다. 타이머를 작동합니다.";
						statusDescription.setText(speakData);
						//speakOut(speakData);
						if(statusSbd == false)
						{
							statusSbd = true;
						}
						statusData = 1;
					}
					checkTimer(statusData);
				}
				if(tempData[1].contains("GoOutMode")==true)
				{
					normalMode.setTextColor(Color.parseColor(disableColor));
					sleepMode.setTextColor(Color.parseColor(disableColor));
					outMode.setTextColor(Color.parseColor(enableColor));
					outsideI.setImageResource(R.drawable.outside2);
					speakData = "외출상태입니다.";
					statusDescription.setText(speakData);
					//speakOut(speakData);
					
					if(exception_flag == false)
					{
						exceptionMode();
					}
				}
				if(tempData[1].contains("SleepMode")==true)
				{
					normalMode.setTextColor(Color.parseColor(disableColor));
					sleepMode.setTextColor(Color.parseColor(enableColor));
					sleepI.setImageResource(R.drawable.sleep2);
					outMode.setTextColor(Color.parseColor(disableColor));
					speakData = "취침상태입니다.";
					statusDescription.setText(speakData);
					//speakOut(speakData);
					if(exception_flag == false)
					{
						exceptionMode();
					}
				}
			}
			else
			{
				statusDescription.setText("카드로 상태를 확인해주세요");
			}
			//Toast.makeText(getBaseContext(), "현재 상태가 성공적으로 갱신되었습니다.",
			//		Toast.LENGTH_SHORT).show();
		}
	}
}
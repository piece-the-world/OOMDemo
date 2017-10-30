package com.netease.demo.oom;

import android.os.Bundle;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

	private static final String ERROR_HINT = "Error ! please input a number in upper EditText First";
	public static final float UNIT_M = 1024 * 1024;
	private TextView dashboard;
	private EditText etDigtal;
	private int digtal=-1;
	private List<byte[]> heap=new ArrayList<>();
	private Runnable increaseFDRunnable=new Runnable() {
		@Override
		public void run() {
			try {
				new BufferedReader(new FileReader("/proc/"+Process.myPid()+"/status"));
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	};
	private Runnable emptyRunnable=new Runnable() {
		@Override
		public void run() {
			try{
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dashboard = (TextView) findViewById(R.id.tv_dashboard);
		etDigtal=(EditText)findViewById(R.id.et_digtal);
		findViewById(R.id.bt1).setOnClickListener(this);
		findViewById(R.id.bt2).setOnClickListener(this);
		findViewById(R.id.bt3).setOnClickListener(this);
		findViewById(R.id.bt4).setOnClickListener(this);
		findViewById(R.id.bt5).setOnClickListener(this);
		findViewById(R.id.bt6).setOnClickListener(this);
		findViewById(R.id.bt7).setOnClickListener(this);
		findViewById(R.id.bt8).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		try{
			digtal=Integer.valueOf(etDigtal.getText().toString());
		}catch (Exception e){
			digtal=-1;
		}
		switch (view.getId()) {
			case R.id.bt1:
				showFileContent("/proc/"+ Process.myPid()+"/limits");
				break;
			case R.id.bt2:
				if (digtal<=0){
					dashboard.setText(ERROR_HINT);
				}else {
					for (int i=0;i<digtal;i++){
						new Thread(increaseFDRunnable).start();
					}
				}
				break;
			case R.id.bt3:
				File fdFile=new File("/proc/" + Process.myPid() + "/fd");
				File[] files = fdFile.listFiles();
				if (files!=null){
					dashboard.setText("current FD numbler is "+files.length);
				}else{
					dashboard.setText("/proc/pid/fd is empty ");
				}
				break;
			case R.id.bt4:
				showFileContent("/proc/"+ Process.myPid()+"/status");
				break;
			case R.id.bt5:
				if (digtal<=0){
					dashboard.setText(ERROR_HINT);
				}else {
					for (int i=0;i<digtal;i++){
						new Thread(emptyRunnable).start();
					}
				}
				break;
			case R.id.bt6:
				StringBuilder stringBuilder=new StringBuilder();
				stringBuilder.append("Java Heap Max : ").append(Runtime.getRuntime().maxMemory()/UNIT_M).append(" MB\r\n");
				stringBuilder.append("Current used  : ").append((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/UNIT_M).append(" MB\r\n");
				dashboard.setText(stringBuilder.toString());
				break;
			case R.id.bt7:
				if (digtal<=0){
					dashboard.setText(ERROR_HINT);
				}else {
					byte[] bytes = new byte[digtal];
					heap.add(bytes);
				}
				break;
			case R.id.bt8:
				heap=new ArrayList<>();
				System.gc();
				break;
		}
	}

	private void showFileContent(String path){
		if (TextUtils.isEmpty(path)){
			return;
		}
		try{
			RandomAccessFile randomAccessFile=new RandomAccessFile(path,"r");
			StringBuilder stringBuilder=new StringBuilder();
			String s;
			while ((s=randomAccessFile.readLine())!=null){
				stringBuilder.append(s).append("\r\n");
			}
			dashboard.setText(stringBuilder.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

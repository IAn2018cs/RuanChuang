package com.runachuang.massorganizationsignin.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtli {
	public static void show(Context context, String text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}

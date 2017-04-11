package com.fpballot;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences
{
	private static final String USERID = "user_id";

	private static SharedPreferences get() {
		return FPBallotApplication.getAppContext().getSharedPreferences("YuballApplication", Context.MODE_PRIVATE);
	}

	public static String getUserId() {
		return get().getString(USERID, null);
	}

	public static void setUserId(String user_id) {
		get().edit().putString(USERID, user_id).commit();
	}

}
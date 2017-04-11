package com.fpballot;

import android.app.Application;
import android.content.Context;

public class FPBallotApplication extends Application
{
	public static FPBallotApplication application;

	public FPBallotApplication() {
		application = this;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public static FPBallotApplication getApp() {
		if (application == null) {
			application = new FPBallotApplication();
		}
		return application;
	}

	public static Context getAppContext() {
		if (application == null) {
			application = new FPBallotApplication();
		}
		return application;
	}
}

package fr.spaz.widget.generic;

import fr.spaz.widget.word.WordWidget;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

public abstract class WidgetUpdateService extends Service
{
	@Override
	public void onStart(Intent intent, int startId)
	{
		// Build the widget update for today
		RemoteViews updateViews = buildUpdate(this);

		// Push update for this widget to the home screen
		ComponentName thisWidget = new ComponentName(this, WordWidget.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		manager.updateAppWidget(thisWidget, updateViews);
	}

	public abstract RemoteViews buildUpdate(Context context);

	@Override
	public IBinder onBind(Intent intent)
	{
		// We don't need to bind to this service
		return null;
	}
}

package fr.spaz.widget.generic;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;
import fr.spaz.widget.word.WordWidget;

public abstract class GenericAppWidgetUpdatePre3Service extends Service
{
	protected abstract RemoteViews buildUpdate(Context context, int position);
	
	public GenericAppWidgetUpdatePre3Service()
	{
		super();
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		// Build the widget update for today
		RemoteViews updateViews = buildUpdate(this, 0);

		// Push update for this widget to the home screen
		ComponentName thisWidget = new ComponentName(this, WordWidget.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		manager.updateAppWidget(thisWidget, updateViews);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// We don't need to bind to this service
		return null;
	}
}
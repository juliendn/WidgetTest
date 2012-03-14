package fr.spaz.widget.generic;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import fr.spaz.widget.R;
import fr.spaz.widget.stack.StackWidgetProvider;
import fr.spaz.widget.word.WordWidget;

public abstract class GenericAppWidgetProvider extends AppWidgetProvider
{

	protected abstract RemoteViews buildView(Context context, int position);

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1)
		{
			onUpdatePost3(context, appWidgetManager, appWidgetIds);
		}
		else
		{
			onUpdatePre3(context, appWidgetManager, appWidgetIds);
		}
	}

	private void onUpdatePre3(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		Log.d("GenericAppWidgetProvider", "onUpdatePre3");
		context.startService(new Intent(context, GenericAppWidgetUpdatePre3ServiceImpl.class));
	}

	private void onUpdatePost3(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		Log.d("GenericAppWidgetProvider", "onUpdatePost3");
		for (int i = 0; i < appWidgetIds.length; ++i)
		{

			// Here we setup the intent which points to the StackViewService which will
			// provide the views for this collection.
			Intent intent = new Intent(context, GenericAppWidgetUpdatePost3ServiceImpl.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			// When intents are compared, the extras are ignored, so we need to embed the extras
			// into the data so that the extras will not be ignored.
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_stack_layout);
			rv.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, intent);

			// The empty view is displayed when the collection has no items. It should be a sibling
			// of the collection view.
			rv.setEmptyView(R.id.stack_view, R.id.empty_view);

			// Here we setup the a pending intent template. Individuals items of a collection
			// cannot setup their own pending intents, instead, the collection as a whole can
			// setup a pending intent template, and the individual items can set a fillInIntent
			// to create unique before on an item to item basis.
			Intent toastIntent = new Intent(context, StackWidgetProvider.class);
			toastIntent.setAction(StackWidgetProvider.TOAST_ACTION);
			toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);

			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);

	}

	public class GenericAppWidgetUpdatePre3ServiceImpl extends GenericAppWidgetUpdatePre3Service
	{

		@Override
		protected RemoteViews buildUpdate(Context context, int position)
		{
			Log.d("GenericAppWidgetUpdatePre3ServiceImpl", "buildUpdate");
			return buildView(context, position);
		}
		
	}

	public class GenericAppWidgetUpdatePost3ServiceImpl extends GenericAppWidgetUpdatePost3Service
	{

		@Override
		public RemoteViews buildUpdate(Context context, int position)
		{
			Log.d("GenericAppWidgetUpdatePost3ServiceImpl", "buildUpdate");
			return buildUpdate(context, position);
		}
		
	}
}

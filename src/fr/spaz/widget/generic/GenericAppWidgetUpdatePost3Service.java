package fr.spaz.widget.generic;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public abstract class GenericAppWidgetUpdatePost3Service extends RemoteViewsService
{
	public abstract RemoteViews buildUpdate(Context context, int position);

	public GenericAppWidgetUpdatePost3Service()
	{
		super();
	}
	
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent)
	{
		return new GenericAppWidgetRemoteViewsFactory(this, intent);
	}

	private class GenericAppWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory
	{

		private Context mContext;

		public GenericAppWidgetRemoteViewsFactory(Context context, Intent intent)
		{
			super();
			mContext = context;
		}

		@Override
		public RemoteViews getViewAt(int position)
		{
			return buildUpdate(mContext, position);
		}

		@Override
		public int getCount()
		{
			return 0;
		}

		@Override
		public long getItemId(int position)
		{
			return 0;
		}

		@Override
		public RemoteViews getLoadingView()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getViewTypeCount()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean hasStableIds()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onCreate()
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onDataSetChanged()
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onDestroy()
		{
			// TODO Auto-generated method stub

		}
	}
}
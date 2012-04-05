package fr.spaz.widget.pedia;

/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.wikipedia.Wiki;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import fr.spaz.widget.R;

public class StackWikiRandomPageService extends RemoteViewsService
{
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent)
	{
		return new StackRemoteViewsFactory(this, intent);
	}
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory
{
	private static final String TAG = "StackRemoteViewsFactory";

	private static final int mCount = 10;
	private List<WikipediaItem> mWidgetItems = new ArrayList<WikipediaItem>();
	private Context mContext;
	private int mAppWidgetId;

	public StackRemoteViewsFactory(Context context, Intent intent)
	{
		mContext = context;
		mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	}

	@Override
	public void onCreate()
	{
		// In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
		// for example downloading or creating content etc, should be deferred to onDataSetChanged()
		// or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.

		
		SimpleWikipediaHelper.prepareUserAgent(mContext);
		
//		Wiki wiki = new Wiki("en.wikipedia.org");
//		
//		String title = null;
//		try
//		{
//			title = wiki.random();
//			final String images[] = wiki.getImagesOnPage(title);
//			final String content = wiki.getPageText(title);
//			
//			for(String image:images)
//			{
//				Log.d(TAG, "image: " + image);
//			}
//			Log.d(TAG, "content: " + content);
//		}
//		catch (IOException e2)
//		{
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
		
		
		List<String> pages = null;
		try
		{
			pages = SimpleWikipediaHelper.getRandomPages(mCount);
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}


		// We sleep for 3 seconds here to show how the empty view appears in the interim.
		// The empty view is set in the StackWidgetProvider and should be a sibling of the
		// collection view.

		String result = null;
		for (int i = 0; i < pages.size(); i++)
		{
			try
			{
				result = SimpleWikipediaHelper.getPageContent(pages.get(i), false);
				mWidgetItems.add(new WikipediaItem(pages.get(i), result));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy()
	{
		// In onDestroy() you should tear down anything that was setup for your data source,
		// eg. cursors, connections, etc.
		mWidgetItems.clear();
	}

	@Override
	public int getCount()
	{
		return mCount;
	}

	@Override
	public RemoteViews getViewAt(int position)
	{
		// position will always range from 0 to getCount() - 1.

		// We construct a remote views item based on our widget item xml file, and set the
		// text based on the position.
		RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_stack_wikipedia_item);
		rv.setTextViewText(R.id.widget_title, mWidgetItems.get(position).mTitle);
		rv.setTextViewText(R.id.widget_item, Html.fromHtml(mWidgetItems.get(position).mText));

		// Next, we set a fill-intent which will be used to fill-in the pending intent template
		// which is set on the collection view in StackWidgetProvider.
		Bundle extras = new Bundle();
		extras.putInt(StackWkiRandomPageProvider.EXTRA_ITEM, position);
		Intent fillInIntent = new Intent();
		fillInIntent.putExtras(extras);
		rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

		// You can do heaving lifting in here, synchronously. For example, if you need to
		// process an image, fetch something from the network, etc., it is ok to do it here,
		// synchronously. A loading view will show up in lieu of the actual contents in the
		// interim.
		try
		{
			System.out.println("Loading view " + position);
			Thread.sleep(500);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		// Return the remote views object.
		return rv;
	}

	@Override
	public RemoteViews getLoadingView()
	{
		// You can create a custom loading view (for instance when getViewAt() is slow.) If you
		// return null here, you will get the default loading view.
		return null;
	}

	@Override
	public int getViewTypeCount()
	{
		return 1;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public boolean hasStableIds()
	{
		return true;
	}

	@Override
	public void onDataSetChanged()
	{
		// This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
		// on the collection view corresponding to this factory. You can do heaving lifting in
		// here, synchronously. For example, if you need to process an image, fetch something
		// from the network, etc., it is ok to do it here, synchronously. The widget will remain
		// in its current state while work is being done here, so you don't need to worry about
		// locking up the widget.
	}
}
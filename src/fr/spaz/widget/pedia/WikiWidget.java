/*
 * Copyright (C) 2009 The Android Open Source Project
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

package fr.spaz.widget.pedia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;
import fr.spaz.widget.R;
import fr.spaz.widget.generic.WidgetUpdateService;
import fr.spaz.widget.pedia.SimpleWikipediaHelper.ApiException;
import fr.spaz.widget.pedia.SimpleWikipediaHelper.ParseException;

/**
 * Define a simple widget that shows the Wiktionary "Word of the day." To build
 * an update we spawn a background {@link Service} to perform the API queries.
 */
public class WikiWidget extends AppWidgetProvider
{
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		// To prevent any ANR timeouts, we perform the update in a service
		context.startService(new Intent(context, UpdateService.class));
	}

	public static class UpdateService extends WidgetUpdateService
	{

		/**
		 * Build a widget update to show the current Wikipedia
		 * "Featured article." Will block until the online API returns.
		 */
		@Override
		public RemoteViews buildUpdate(Context context)
		{
			// Pick out month names from resources
			Resources res = context.getResources();
			String[] monthNames = res.getStringArray(R.array.month_names);

			// Find current month and day
			Time today = new Time();
			today.setToNow();

			// "Wikipedia:Today\'s_featured_article/March 11, 2012"
			
			String pageName = res.getString(R.string.template_today_featured_article, monthNames[today.month], today.monthDay, today.year);
			RemoteViews updateViews = null;
			String pageContent = "";

			try
			{
				// Try querying the Wikipedia API for featured's article
				SimpleWikipediaHelper.prepareUserAgent(context);
				pageContent = SimpleWikipediaHelper.getPageContent(pageName, false);
			}
			catch (ApiException e)
			{
				Log.e("WordWidget", "Couldn't contact API", e);
			}
			catch (ParseException e)
			{
				Log.e("WordWidget", "Couldn't parse API response", e);
			}
			
			Log.d("Spaz", pageContent);
			// Build an update that holds the updated widget contents
			updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_wikipedia);

			updateViews.setTextViewText(R.id.wikipedia_text, pageContent);

			// When user clicks on widget, launch to Wiktionary definition
			// page
			String definePage = res.getString(R.string.template_wikipedia_define_url, Uri.encode(pageName));
			Intent defineIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(definePage));
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, defineIntent, 0);
			updateViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
			return updateViews;
		}
	}
}

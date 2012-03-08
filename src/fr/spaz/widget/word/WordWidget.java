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

package fr.spaz.widget.word;

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
import fr.spaz.widget.word.SimpleWikiHelper.ApiException;
import fr.spaz.widget.word.SimpleWikiHelper.ParseException;

/**
 * Define a simple widget that shows the Wiktionary "Word of the day." To build
 * an update we spawn a background {@link Service} to perform the API queries.
 */
public class WordWidget extends AppWidgetProvider
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
		 * Build a widget update to show the current Wiktionary
		 * "Word of the day." Will block until the online API returns.
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

			// Build today's page title, like
			// "Wiktionary:Word of the day/March 21"
			String pageName = res.getString(R.string.template_wotd_title, monthNames[today.month], today.monthDay);
			RemoteViews updateViews = null;
			String pageContent = "";

			try
			{
				// Try querying the Wiktionary API for today's word
				SimpleWikiHelper.prepareUserAgent(context);
				pageContent = SimpleWikiHelper.getPageContent(pageName, false);
			}
			catch (ApiException e)
			{
				Log.e("WordWidget", "Couldn't contact API", e);
			}
			catch (ParseException e)
			{
				Log.e("WordWidget", "Couldn't parse API response", e);
			}

			// Use a regular expression to parse out the word and its definition
			Pattern pattern = Pattern.compile(SimpleWikiHelper.WORD_OF_DAY_REGEX);
			Matcher matcher = pattern.matcher(pageContent);
			if (matcher.find())
			{
				// Build an update that holds the updated widget contents
				updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_word);

				String wordTitle = matcher.group(1);
				updateViews.setTextViewText(R.id.word_title, wordTitle);
				updateViews.setTextViewText(R.id.word_type, matcher.group(2));
				updateViews.setTextViewText(R.id.definition, matcher.group(3).trim());

				// When user clicks on widget, launch to Wiktionary definition
				// page
				String definePage = res.getString(R.string.template_define_url, Uri.encode(wordTitle));
				Intent defineIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(definePage));
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, defineIntent, 0);
				updateViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
			}
			else
			{
				// Didn't find word of day, so show error message
				updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_message);
				CharSequence errorMessage = context.getText(R.string.widget_error);
				updateViews.setTextViewText(R.id.message, errorMessage);
			}
			return updateViews;
		}
	}
}

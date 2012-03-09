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

package fr.spaz.widget.twitter;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import fr.spaz.widget.generic.WidgetUpdateService;

/**
 * Define a simple widget that shows the Wiktionary "Word of the day." To build an update we spawn a background {@link Service} to perform the API queries.
 */
public class TwitterWidget extends AppWidgetProvider
{
	private static final String TAG = "TwitterWidget";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		// To prevent any ANR timeouts, we perform the update in a service
		context.startService(new Intent(context, UpdateService.class));
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		super.onReceive(context, intent);
	}

	public static class UpdateService extends WidgetUpdateService
	{

		private static final String TWITTER_ACCOUNT_TYPE = "com.twitter.android.auth.login";
		private static final String TWITTER_ACCOUNT_TOKEN = "com.twitter.android.oauth.token";
		private static final String TWITTER_ACCOUNT_TOKEN_SECRET = "com.twitter.android.oauth.token.secret";

		private static final String CONSUMER_KEY = "jQwzPCLdvKETM17DBcZQ8g";
		private static final String CONSUMER_SECRET = "Ggo6qcpI26URilPqUVdbNhC8dquCxbBmXyqqazsUk";

		/**
		 * Build a widget update to show the current Wiktionary "Word of the day." Will block until the online API returns.
		 */
		@Override
		public RemoteViews buildUpdate(Context context)
		{
			AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
			final Account[] accounts = accountManager.getAccountsByType(TWITTER_ACCOUNT_TYPE);
			if (accounts.length > 0)
			{

				Log.d(TAG, "nb account: " + accounts.length);
				final Account account = accounts[0];

				String token = null;
				String secret = null;

				AccountManagerFuture<Bundle> bundle = null;
				
				bundle = accountManager.getAuthToken(account, TWITTER_ACCOUNT_TOKEN, true, null, null);
				try
				{
					Bundle b = bundle.getResult();
					token = b.getString(AccountManager.KEY_AUTHTOKEN);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				bundle = accountManager.getAuthToken(account, TWITTER_ACCOUNT_TOKEN_SECRET, true, null, null);
				try
				{
					Bundle b = bundle.getResult();
					secret = b.getString(AccountManager.KEY_AUTHTOKEN);

				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				try
				{
					Log.d(TAG, "token: " + token);
					Log.d(TAG, "secret: " + secret);

					Twitter twitter = new TwitterFactory().getInstance();
					AccessToken accesstoken = new AccessToken(token, secret);
					twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
					twitter.setOAuthAccessToken(accesstoken);
					ResponseList<Status> timeline = twitter.getHomeTimeline();
					Log.d(TAG, "size: " + timeline.size());
				}
				catch (TwitterException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				Log.d(TAG, "No account found");
			}
			return null;
		}
	}
}

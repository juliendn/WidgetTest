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

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
	private static final String TWITTER_ACCOUNT_TYPE = "com.twitter.android.auth.login";
	private static final String TWITTER_ACCOUNT_TOKEN = "com.twitter.android.oauth.token";
	private static final String TWITTER_ACCOUNT_TOKEN_SECRET = "com.twitter.android.oauth.token.secret";

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

				final Account account = accounts[0];
				Log.d(TAG, "name: " + account.name);
				Log.d(TAG, "type: " + account.type);

				accountManager.getAuthToken(account, "com.twitter.android.oauth.token", true, new AccountManagerCallback<Bundle>()
				{
					@Override
					public void run(AccountManagerFuture<Bundle> arg0)
					{
						try
						{
							Bundle b = arg0.getResult();
							String token = b.getString(AccountManager.KEY_AUTHTOKEN);
							String userName = b.getString(AccountManager.KEY_ACCOUNT_NAME);
							Log.d(TAG, "token: " + token);
							Log.d(TAG, "userName: " + userName);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}, null);

				accountManager.getAuthToken(account, "com.twitter.android.oauth.token.secret", true, new AccountManagerCallback<Bundle>()
				{
					@Override
					public void run(AccountManagerFuture<Bundle> arg0)
					{
						try
						{
							Bundle b = arg0.getResult();
							String secret = b.getString(AccountManager.KEY_AUTHTOKEN);
							Log.d(TAG, "secret: " + secret);

						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}, null);
			}
			else
			{
				Log.d(TAG, "No account found");
			}
			return null;
		}
	}
}

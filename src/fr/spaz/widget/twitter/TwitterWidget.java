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

import fr.spaz.widget.generic.WidgetUpdateService;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

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
		// AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		// if (intent.getAction().equals(TOAST_ACTION))
		// {
		// int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		// int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
		// Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();
		// }
		super.onReceive(context, intent);
	}

	public static class UpdateService extends WidgetUpdateService
	{

		private static final String TWITTER_ACCOUNT_TYPE = "com.twitter.android.auth.login";
		private static final String TWITTER_ACCOUNT_TOKEN = "com.twitter.android.oauth.token";

		/**
		 * Build a widget update to show the current Wiktionary "Word of the day." Will block until the online API returns.
		 */
		@Override
		public RemoteViews buildUpdate(Context context)
		{
			AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
			final Account[] accounts = accountManager.getAccountsByType(TWITTER_ACCOUNT_TYPE);
			if (accounts == null)
			{
				Log.d(TAG, "No account found");
				return null;
			}

			final Account account = accounts[0];

			try
			{
				String token = accountManager.blockingGetAuthToken(account, TWITTER_ACCOUNT_TOKEN, false);
				Log.d(TAG, "token: " + token);

			}
			catch (OperationCanceledException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (AuthenticatorException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_stack_layout);
			// rv.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, intent);

			return null;
		}
	}
}

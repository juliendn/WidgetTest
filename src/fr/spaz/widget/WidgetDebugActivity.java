package fr.spaz.widget;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class WidgetDebugActivity extends Activity
{

	private static final String TAG = "WidgetDebugActivity";
	private static final String TWITTER_ACCOUNT_TYPE = "com.twitter.android.auth.login";
	private static final String TWITTER_ACCOUNT_TOKEN = "com.twitter.android.oauth.token";
	private static final String TWITTER_ACCOUNT_TOKEN_SECRET = "com.twitter.android.oauth.token";

	// private static final String TWITTER_ACCOUNT_TYPE = "com.google";
	// private static final String TWITTER_ACCOUNT_TOKEN = "android";

	@Override
	protected void onCreate(Bundle _savedInstanceState)
	{
		super.onCreate(_savedInstanceState);

		AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
		final Account[] accounts = accountManager.getAccountsByType(TWITTER_ACCOUNT_TYPE);
		if (accounts.length > 0)
		{
			final Account account = accounts[0];
			Log.d(TAG, "name: " + account.name);
			Log.d(TAG, "type: " + account.type);

			// ConnectionTask task = new ConnectionTask(accountManager);
			// task.execute(account);

			String token = null;
			String secret = null;
			String userName = null;
			AccountManagerFuture<Bundle> response = accountManager.getAuthToken(account, TWITTER_ACCOUNT_TOKEN, null, this, null, null);
			try
			{
				Bundle b = response.getResult();
				token = b.getString(AccountManager.KEY_AUTHTOKEN);
				userName = b.getString(AccountManager.KEY_ACCOUNT_NAME);
				Log.d(TAG, "token: " + token);
				Log.d(TAG, "userName: " + userName);
			}
			catch (OperationCanceledException e)
			{
				e.printStackTrace();
			}
			catch (AuthenticatorException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			response = accountManager.getAuthToken(account, TWITTER_ACCOUNT_TOKEN_SECRET, null, this, null, null);
			try
			{
				Bundle b = response.getResult();
				secret = b.getString(AccountManager.KEY_AUTHTOKEN);
				Log.d(TAG, "secret: " + secret);
			}
			catch (OperationCanceledException e)
			{
				e.printStackTrace();
			}
			catch (AuthenticatorException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}
		else
		{
			Log.d(TAG, "No account");
		}
	}

	private class ConnectionTask extends AsyncTask<Account, Void, String>
	{

		private AccountManager mAccountManager;

		public ConnectionTask(AccountManager accountManager)
		{
			mAccountManager = accountManager;
		}

		@Override
		protected String doInBackground(Account... params)
		{
			String token = null;
			String secret = null;
			try
			{
				token = mAccountManager.blockingGetAuthToken(params[0], TWITTER_ACCOUNT_TOKEN, true);
				secret = mAccountManager.blockingGetAuthToken(params[0], TWITTER_ACCOUNT_TOKEN_SECRET, true);
			}
			catch (OperationCanceledException e)
			{
				e.printStackTrace();
			}
			catch (AuthenticatorException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return token;
		}
	}

}

package fr.spaz.widget;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class WidgetDebugActivity extends Activity
{

	private static final String TAG = "WidgetDebugActivity";
	private static final String TWITTER_ACCOUNT_TYPE = "com.twitter.android.auth.login";
	private static final String TWITTER_ACCOUNT_TOKEN = "com.twitter.android.oauth.token";
	private static final String TWITTER_ACCOUNT_TOKEN_SECRET = "com.twitter.android.oauth.token.secret";

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

			accountManager.getAuthToken(account, "com.twitter.android.oauth.token", null, this, new AccountManagerCallback<Bundle>()
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

			accountManager.getAuthToken(account, "com.twitter.android.oauth.token.secret", null, this, new AccountManagerCallback<Bundle>()
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

			// try
			// {
			// String token = accountManager.blockingGetAuthToken(account,
			// TWITTER_ACCOUNT_TOKEN, false);
			// Log.d(TAG, token);
			// }
			// catch (OperationCanceledException e)
			// {
			// e.printStackTrace();
			// }
			// catch (AuthenticatorException e)
			// {
			// e.printStackTrace();
			// }
			// catch (IOException e)
			// {
			// e.printStackTrace();
			// }
		}
		else
		{
			Log.d(TAG, "No account");
		}
	}
}

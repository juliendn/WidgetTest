package fr.spaz.widget.twitter;

import java.io.IOException;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthSupport;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.util.Log;

public class WidgetTwitterDebugActivity extends Activity
{

	private static final String TAG = "WidgetDebugActivity";
	private static final String TWITTER_ACCOUNT_TYPE = "com.twitter.android.auth.login";
	private static final String TWITTER_ACCOUNT_TOKEN = "com.twitter.android.oauth.token";
	private static final String TWITTER_ACCOUNT_SECRET = "com.twitter.android.oauth.token.secret";

	private static final String CONSUMER_KEY = "jQwzPCLdvKETM17DBcZQ8g";
	private static final String CONSUMER_SECRET = "Ggo6qcpI26URilPqUVdbNhC8dquCxbBmXyqqazsUk";
	private OAuthSupport mTwitter;

	@Override
	protected void onCreate(Bundle _savedInstanceState)
	{
		super.onCreate(_savedInstanceState);

		AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
		final Account[] accounts = accountManager.getAccountsByType(TWITTER_ACCOUNT_TYPE);
		if (accounts.length > 0)
		{
			Log.d(TAG, "nb account: " + accounts.length);
			final Account account = accounts[0];

			String token = null;
			String secret = null;

			AccountManagerFuture<Bundle> response = null;
			response = accountManager.getAuthToken(account, TWITTER_ACCOUNT_TOKEN, null, this, null, null);
			try
			{
				Bundle b = response.getResult();
				token = b.getString(AccountManager.KEY_AUTHTOKEN);
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

			response = accountManager.getAuthToken(account, TWITTER_ACCOUNT_SECRET, null, this, null, null);
			try
			{
				Bundle b = response.getResult();
				secret = b.getString(AccountManager.KEY_AUTHTOKEN);
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

			Log.d(TAG, "token: " + token);
			Log.d(TAG, "secret: " + secret);

			try
			{
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
			Log.d(TAG, "No account");
		}
	}
}

package fr.spaz.widget.testimpl;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import fr.spaz.widget.R;
import fr.spaz.widget.generic.GenericAppWidgetProvider;

public class TestImpl extends GenericAppWidgetProvider
{

	@Override
	protected RemoteViews buildView(Context context, int position)
	{
		final RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_word);

		updateViews.setTextViewText(R.id.word_title, "title");
		updateViews.setTextViewText(R.id.word_type, "type");
		updateViews.setTextViewText(R.id.definition, "def");

		// When user clicks on widget, launch to Wiktionary definition
		// page
		String definePage = context.getString(R.string.template_wiktionary_define_url, "www.google.com");
		Intent defineIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(definePage));
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, defineIntent, 0);
		updateViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

		return updateViews;
	}

}

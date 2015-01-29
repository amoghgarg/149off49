package productions.mousedroid;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by 0000101795 on 2015/01/19.
 */
public class widget_class extends AppWidgetProvider{


    public void onReceive(Context ctx, Intent intent) {

        super.onReceive(ctx, intent);
        final String action = intent.getAction();
        Log.d("Received",action);
        if (action.equals("pause")) {
            Intent tempIntent = new Intent(ctx, SensorService.class);
            tempIntent.setAction("pause");
            ctx.startService(tempIntent);
        }
        else if (action.equals("left")) {
            Intent tempIntent = new Intent(ctx, SensorService.class);
            tempIntent.setAction("left");
            Log.d("widget","left");
            ctx.startService(tempIntent);
        }
        else if (action.equals("right")) {
            Intent tempIntent = new Intent(ctx, SensorService.class);
            tempIntent.setAction("right");
            Log.d("widget","right");
            ctx.startService(tempIntent);
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i=0; i<appWidgetIds.length; i++) {
            Log.d("Update","Assigning Buttons");
            Intent pauseIntent = new Intent(context, widget_class.class);
            pauseIntent.setAction("pause");
            PendingIntent pPauseIntent = PendingIntent.getBroadcast(context, 10, pauseIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            Intent leftIntent = new Intent(context, widget_class.class);
            leftIntent.setAction("left");
            PendingIntent pLeftIntent = PendingIntent.getBroadcast(context, 10, leftIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            Intent rightIntent = new Intent(context, widget_class.class);
            rightIntent.setAction("right");
            PendingIntent pRightIntent = PendingIntent.getBroadcast(context, 10, rightIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.widgetLeft, pLeftIntent);
            views.setOnClickPendingIntent(R.id.widgetPause, pPauseIntent);
            views.setOnClickPendingIntent(R.id.widgetRight, pRightIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }


    }
}

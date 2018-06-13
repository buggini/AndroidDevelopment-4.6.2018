package rs.aleph.android.example1.async;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import rs.aleph.android.example1.R;
import rs.aleph.android.example1.tools.ReviewerTools;


/**
 * Created by milossimic on 10/23/16.
 *
 * BroadcastReceiver je komonenta koja moze da reaguje na poruke drugih delova
 * samog sistema kao i korisnicki definisanih. Cesto se koristi u sprezi sa
 * servisima i asinhronim zadacima.
 *
 * Pored toga on moze da reaguje i na neke sistemske dogadjaje prispece sms poruke
 * paljenje uredjaja, novi poziv isl.
 */
public class SimpleReceiver extends BroadcastReceiver{

    private static int notificationID = 1;

    @Override
    /**
     * Intent je bitan parametar za BroadcastReceiver. Kada posaljemo neku poruku,
     * ovaj Intent cuva akciju i podatke koje smo mu poslali.
     * */
    public void onReceive(Context context, Intent intent) {
        Log.i("MY_ANDROID_APP", "Receive");
        /**
         * Posto nas BroadcastReceiver reaguje samo na jednu akciju koju smo definisali
         * dobro je da proverimo da li som dobili bas tu akciju. Ako jesmo onda mozemo
         * preuzeti i sadrzaj ako ga ima.
         *
         * Voditi racuna o tome da se naziv akcije kada korisnik salje Intent mora poklapati sa
         * nazivom akcije kada akciju proveravamo unutar BroadcastReceiver-a. Isto vazi i za podatke.
         * Dobra praksa je da se ovi nazivi izdvoje unutar neke staticke promenljive.
         * */
        if(intent.getAction().equals("SYNC_DATA")){
            int resultCode = intent.getExtras().getInt("RESULT_CODE");

            prepareNotification(resultCode, context);
        }
    }

    private void prepareNotification(int resultCode, Context context){
        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_network);

        Log.e("MY_ANDROID_APP", "preperNotification");

        Toast.makeText(context, "Reciever: " + resultCode, Toast.LENGTH_LONG).show();

        if(resultCode == ReviewerTools.TYPE_NOT_CONNECTED){
            mBuilder.setSmallIcon(R.drawable.ic_action_settings);
            mBuilder.setContentTitle(context.getString(R.string.autosync_problem));
            mBuilder.setContentText(context.getString(R.string.no_internet));

        }else if(resultCode == ReviewerTools.TYPE_MOBILE){
            mBuilder.setSmallIcon(R.drawable.ic_stat_buy);
            mBuilder.setContentTitle(context.getString(R.string.autosync_warning));
            mBuilder.setContentText(context.getString(R.string.connect_to_wifi));
        }else{
            mBuilder.setSmallIcon(R.drawable.ic_action_refresh);
            mBuilder.setContentTitle(context.getString(R.string.autosync));
            mBuilder.setContentText(context.getString(R.string.good_news_sync));
        }


        mBuilder.setLargeIcon(bm);
        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(notificationID, mBuilder.build());
    }
}

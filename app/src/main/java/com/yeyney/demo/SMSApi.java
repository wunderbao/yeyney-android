package com.yeyney.demo;

import android.os.AsyncTask;
import android.util.Log;

import com.messagebird.MessageBirdClient;
import com.messagebird.MessageBirdService;
import com.messagebird.MessageBirdServiceImpl;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.UnauthorizedException;
import com.messagebird.objects.Message;
import com.messagebird.objects.MessageResponse;

public class SMSApi {

    // Create a MessageBirdService
    private static final MessageBirdService messageBirdService = new MessageBirdServiceImpl("live_8h3ZRQVilRwTfBhyV65xEFSEv");
    // Add the service to the client
    private static final MessageBirdClient messageBirdClient = new MessageBirdClient(messageBirdService);

    private static final String TAG = "SMSApi";

    public static void sendSMS(String recipients, String messageBody) throws UnauthorizedException, GeneralException {
        String originator = "Yey Ney Demo";
        Message message = new Message(originator, messageBody, recipients);
        new SendSMSTask().execute(message);
    }

    static class SendSMSTask extends AsyncTask<Message, Void, MessageResponse> {

        @Override
        protected MessageResponse doInBackground(Message... messages) {
            for (Message message : messages) {
                try {
                    return messageBirdClient.sendMessage(message);
                } catch (UnauthorizedException e) {
                    e.printStackTrace();
                } catch (GeneralException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(MessageResponse messageResponse) {
            Log.d(TAG, messageResponse.getDirection());
            Log.d(TAG, messageResponse.getHref());
            Log.d(TAG, messageResponse.getId());
        }
    }
}

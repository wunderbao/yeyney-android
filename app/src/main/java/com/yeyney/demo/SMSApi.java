package com.yeyney.demo;

import android.content.Context;
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

    private static final MessageBirdService messageBirdService = new MessageBirdServiceImpl("test_fTdqjSGd2gWjhN5VzDqfAt1fl");
    private static final MessageBirdClient messageBirdClient = new MessageBirdClient(messageBirdService);

    private static final String TAG = "SMSApi";
    private static final String originator = "Yey Ney";

    public static void sendSMS(String recipients, String messageBody) throws UnauthorizedException, GeneralException {
        Message message = new Message(originator, messageBody, "+4747859817");
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
            Log.d(TAG, "Sent " + messageResponse.getRecipients().getTotalSentCount() + " messages.");
        }
    }
}

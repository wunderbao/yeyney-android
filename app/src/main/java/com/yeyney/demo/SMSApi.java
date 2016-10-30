package com.yeyney.demo;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.messagebird.MessageBirdClient;
import com.messagebird.MessageBirdService;
import com.messagebird.MessageBirdServiceImpl;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.UnauthorizedException;
import com.messagebird.objects.Message;
import com.messagebird.objects.MessageResponse;
import com.yeyney.demo.model.Contact;

import java.util.ArrayList;
import java.util.Random;

public class SMSApi {

    private static final MessageBirdService messageBirdService = new MessageBirdServiceImpl("test_fTdqjSGd2gWjhN5VzDqfAt1fl");
    private static final MessageBirdClient messageBirdClient = new MessageBirdClient(messageBirdService);

    private static final String TAG = "SMSApi";
    private static final String originator = "Yey Ney";

    public static void sendSMS(String recipients, String messageBody) throws UnauthorizedException, GeneralException {
        Message message = new Message(originator, messageBody, "+4747859817");
        new SendSMSTask().execute(message);
    }

    public static void generateAndSendSMS(String uid, ArrayList<Contact> recipients, StorageReference imagesRef, String comment, String value) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String postToken = getRandomHexString(8);
        addPhotoToUser(database.getReference("shared"), uid, postToken, imagesRef, comment, value);
        DatabaseReference users = database.getReference("users");
        for (Contact recipient : recipients) {
            addMissingUser(users, recipient);
            StringBuilder builder = new StringBuilder(comment);
            builder.append("\nVisit https://yeyney-demo.firebaseapp.com/?");
            builder.append("id=").append(uid);
            builder.append("&post=").append(postToken);
            builder.append("&token=").append(recipient.getNumber());
            Log.d(TAG, builder.toString());
            try {
                sendSMS(recipient.getNumber(), builder.toString());
            } catch (UnauthorizedException | GeneralException e) {
                e.printStackTrace();
            }
        }
    }

    private static void addMissingUser(DatabaseReference users, Contact recipient) {
        DatabaseReference recipientChild = users.child(recipient.getNumber());
        recipientChild.child("name").setValue(recipient.getDisplayName());
        recipientChild.child("number").setValue(recipient.getNumber());
    }

    private static void addPhotoToUser(DatabaseReference shared, String uid, String postToken, StorageReference imagesRef, String comment, String value) {
        DatabaseReference userChild = shared.child(uid);
        if (userChild == null) {
            shared.setValue(uid);
            userChild = shared.child(uid);
        }
        DatabaseReference postChild = userChild.child(postToken);
        postChild.child("image").setValue(imagesRef.getPath());
        postChild.child("message").setValue(comment);
        postChild.child("price").setValue(value);
    }

    private static String getRandomHexString(int numchars) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        while (builder.length() < numchars) {
            builder.append(Integer.toHexString(random.nextInt()));
        }

        return builder.toString().substring(0, numchars);
    }


    private static class SendSMSTask extends AsyncTask<Message, Void, MessageResponse> {

        @Override
        protected MessageResponse doInBackground(Message... messages) {
            for (Message message : messages) {
                try {
                    return messageBirdClient.sendMessage(message);
                } catch (UnauthorizedException | GeneralException e) {
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

package com.yeyney.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VotesActivity extends AuthActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "VotesActivity";

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference votesReference, sharedReference;

    private List<Shared> sharedList = new ArrayList<>();
    private VotesArrayAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votes);

        String uid = auth.getCurrentUser().getUid();
        votesReference = firebaseDatabase.getReference("votes").child(uid);
        sharedReference = firebaseDatabase.getReference("shared").child(uid);

        ListView votesList = (ListView) findViewById(R.id.listview_votes);
        listAdapter = new VotesArrayAdapter(this, R.layout.shared_list_item, sharedList);
        votesList.setAdapter(listAdapter);
        votesList.setOnItemClickListener(this);
        getShared();
    }

    private void getShared() {
        sharedReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listAdapter.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Shared shared = new Shared(postSnapshot.getKey());
                    shared.image = postSnapshot.child("image").getValue(String.class);
                    shared.message = postSnapshot.child("message").getValue(String.class);
                    sharedList.add(shared);
                    listAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "The read failed: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Shared shared = sharedList.get(position);

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Votes");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // TODO: Should at least add this to a list, no need to make new objects
        new Vote(shared.key, alertDialog);
    }

    protected class Shared {

        private String key;
        public String message;
        public String image;

        public Shared(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return message;
        }
    }

    protected class Vote {
        int yey, ney;
        String key;

        Vote(String key, final AlertDialog alertDialog) {
            this.key = key;

            // TODO: Can it be more dirty?
            votesReference.child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    yey = ney = 0;
                    for (DataSnapshot votes : dataSnapshot.getChildren()) {
                        String voteValue = votes.getValue(String.class);
                        if (voteValue.equals("yey")) {
                            yey++;
                        } else {
                            ney++;
                        }
                    }
                    if (!alertDialog.isShowing()) {
                        alertDialog.setMessage(Vote.this.printVotes());
                        alertDialog.show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Votes for " + Vote.this.key + ", can't be read: " + databaseError.getMessage());
                }
            });
        }

        public String printVotes() {
            return yey + " people voted yey!\n" + ney + " people voted ney!";
        }

        @Override
        public String toString() {
            return key + ": yey(" + yey + "), ney(" + ney + ")";
        }
    }
}

package com.yeyney.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VotesActivity extends AuthActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "VotesActivity";

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference votesReference;
    private DatabaseReference usersReference;
    private Query sharedReference;

    private List<Shared> sharedList;
    private Map<String, String> usersMap;
    private VotesArrayAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votes);

        showProgressIndicator();
        String uid = auth.getCurrentUser().getUid();
        usersReference = firebaseDatabase.getReference("users");
        votesReference = firebaseDatabase.getReference("votes").child(uid);
        sharedReference = firebaseDatabase.getReference("shared").child(uid).orderByChild("price");

        sharedList = new ArrayList<>();
        usersMap = new HashMap<>();

        genereateVotersName();

        ListView votesList = (ListView) findViewById(R.id.listview_votes);
        listAdapter = new VotesArrayAdapter(this, R.layout.shared_list_item, sharedList);
        votesList.setAdapter(listAdapter);
        votesList.setOnItemClickListener(this);
        getShared();
    }

    private void genereateVotersName() {
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    usersMap.put(child.getKey(), child.child("name").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "The read failed: " + databaseError.getMessage());
            }
        });
    }

    private void getShared() {
        sharedReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Shared shared = new Shared(dataSnapshot.getKey());
                shared.price = dataSnapshot.child("price").getValue(Integer.class);
                shared.image = dataSnapshot.child("image").getValue(String.class);
                shared.message = dataSnapshot.child("message").getValue(String.class);
                sharedList.add(shared);
                listAdapter.notifyDataSetChanged();
                dismissProgressIndicator();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int index = -1;
                for (int i = 0; i < sharedList.size(); i++) {
                    if (sharedList.get(i).key.equals(dataSnapshot.getKey())) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    sharedList.remove(index);
                    listAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                for (Shared shared : sharedList) {
                    if (shared.key == dataSnapshot.getKey()) {
                        shared.price = dataSnapshot.child("price").getValue(Integer.class);
                        shared.image = dataSnapshot.child("image").getValue(String.class);
                        shared.message = dataSnapshot.child("message").getValue(String.class);
                        listAdapter.notifyDataSetChanged();
                    }
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Votes");
        builder.setNeutralButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // TODO: Should at least add this to a list, no need to make new objects
        new Vote(shared.key, builder);
    }

    protected class Shared {

        private String key;
        public String message;
        public String image;
        public int price;

        public Shared(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return message;
        }
    }

    protected class Vote {
        List<String> answers;
        AlertDialog alertDialog;
        String key;

        Vote(String key, final AlertDialog.Builder builder) {
            answers = new ArrayList<>();
            this.key = key;

            // TODO: Can it be more dirty?
            votesReference.child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (alertDialog != null) {
                        answers.clear();
                        alertDialog.cancel();
                        alertDialog = null;
                    }
                    for (DataSnapshot votes : dataSnapshot.getChildren()) {
                        String name = usersMap.get(votes.getKey());
                        if (name == null) {
                            name = votes.getKey();
                        }
                        String voteValue = votes.getValue(String.class);
                        if (voteValue.equals("yey") || voteValue.equals("ney")) {
                            answers.add(name + " voted " + voteValue);
                        }
                    }

                    if (alertDialog == null) {
                        if (answers.isEmpty()) {
                            answers.add("No votes reported yet");
                        }
                        builder.setItems(answers.toArray(new String[answers.size()]), null);
                        alertDialog = builder.create();
                    }

                    if (!alertDialog.isShowing()) {
                        alertDialog.show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Votes for " + Vote.this.key + ", can't be read: " + databaseError.getMessage());
                }
            });
        }
    }
}

package com.alexdeo.flashchatnewfirebase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by alex on 2/28/2018.
 */

public class ChatListAdapter extends BaseAdapter {

    //private variables
    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private String mDisplayName;
    private ArrayList<DataSnapshot> mDataSnapshots;

    private ChildEventListener mChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            mDataSnapshots.add(dataSnapshot);
            //have to tell the listview to refresh coz we have new data avilable
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public ChatListAdapter(Activity activity, DatabaseReference ref, String displayName){
        mActivity = activity;
        mDatabaseReference = ref.child("message");
        //attaching the DB childLister to the DatabaseReference..normal setup for an addapter so use contructor
        mDatabaseReference.addChildEventListener(mChildEventListener);
        mDisplayName = displayName;
        mDataSnapshots = new ArrayList<>();
    }
    //Inner Classs ..acts as a package to hold all our views in chat mesg row layout xml
    static class ViewHolder{
        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params;
    }

    @Override
    public int getCount() {
        return mDataSnapshots.size();
    }

    @Override
    public InstantMessage getItem(int position) {
        DataSnapshot snapshot = mDataSnapshots.get(position);
        //getValue converts the json from the snapshot to an instant message object
        //now get item is returning an instant message object everytime its get called inside get view
        return snapshot.getValue(InstantMessage.class);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //check if there is an existing view that can be reused.
        if(convertView == null){
            //create new row from scratch, meaning their is no reusable row.
            //we need a LayoutInflater object called inflater
            LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //give the getView method the view to give to the listView.
            //Inflate is same  as saying "PASS THE XML "
            convertView = inflater.inflate(R.layout.chat_msg_row,parent,false);

            final ViewHolder holder = new ViewHolder();
            //link all the fields of the viewHolder to the views of the chat_mesg_row which we inflated
            holder.authorName = (TextView) convertView.findViewById(R.id.author);
            holder.body = (TextView)convertView.findViewById(R.id.message);
            holder.params = (LinearLayout.LayoutParams)holder.authorName.getLayoutParams();
             //need to temp store our view holder so that we can reuse it later..avoiding using findViewbyID repeatedly
            convertView.setTag(holder);


        }
        final InstantMessage message = getItem(position);
        //retrieve the viewHolder that was temp saved with setTage()
        final ViewHolder holder = (ViewHolder)convertView.getTag();

        //check if the author is me
        Boolean isMe = message.getAuthor().equals(mDisplayName);
        setChatRowAppearence(isMe,holder);
        //setting the ViewHolder fields holder with new data from the Instantmessage
        String author = message.getAuthor();
        holder.authorName.setText(author);

        String body = message.getMessage();
        holder.body.setText(body);
        return convertView;
    }
    //method to setChatRowAppearence
    private void setChatRowAppearence(boolean IsItMe,ViewHolder holder){
        //configuring the layout parameters
        if(IsItMe){
            holder.params.gravity = Gravity.END;
            holder.authorName.setTextColor(Color.GREEN);
            holder.body.setBackgroundResource(R.drawable.bubble1);
        }else{
            holder.params.gravity = Gravity.START;
            holder.authorName.setTextColor(Color.BLUE);
            holder.body.setBackgroundResource(R.drawable.bubble2);
        }
        //setting the layout to with the new layout parameters
        holder.authorName.setLayoutParams(holder.params);
        holder.body.setLayoutParams(holder.params);

    }

    public void cleanup(){
        mDatabaseReference.removeEventListener(mChildEventListener);
    }
}

package com.example.shubhangi.travelq;

import android.content.pm.PackageManager;
import android.Manifest;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.ResponseMessage;
import ai.api.model.Result;

public class MainActivity extends AppCompatActivity implements AIListener{

    private AIService aiService;
    DatabaseReference ref;

    RecyclerView rv;
    EditText edt;
    ImageView addBtn;
    FirebaseRecyclerAdapter<Message,chat_rec> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1);

        }
        rv= (RecyclerView)findViewById(R.id.recyclerView);
        edt = (EditText)findViewById(R.id.ques);
        addBtn = (ImageView)findViewById(R.id.fab_btn);
        rv.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rv.setLayoutManager(linearLayoutManager);
        final AIConfiguration config = new AIConfiguration("57fc54a95a5d4182b0790d00d69f92bb",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        ref = FirebaseDatabase.getInstance().getReference();
        ref.keepSynced(true);
        final AIDataService aiDataService = new AIDataService(config);

        final AIRequest aiRequest = new AIRequest();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=edt.getText().toString();
                if (!message.equals("")){
                    Message msg=new Message(message,"user");
                    ref.child("Convo").push().setValue(msg);
                    aiRequest.setQuery(message);
                    new AsyncTask<AIRequest,Void,AIResponse>(){

                        @Override
                        protected void onPostExecute(AIResponse aiResponse) {
                            if (aiResponse != null) {

                                Result result = aiResponse.getResult();
                                String reply = result.getFulfillment().getSpeech();
                                Message msgr=new Message(reply,"bot");
                                ref.child("Convo").push().setValue(msgr);
                            }
                        }

                        @Override
                        protected AIResponse doInBackground(AIRequest... aiRequests) {
                            final AIRequest request=aiRequests[0];
                            try {
                                final AIResponse response = aiDataService.request(aiRequest);
                                return response;
                            } catch (AIServiceException e) {
                            }

                            return null;
                        }
                    }.execute(aiRequest);
                }
                else {
                    aiService.startListening();
                }
            }
        });
        adapter = new FirebaseRecyclerAdapter<Message, chat_rec>(Message.class,R.layout.converse,chat_rec.class,ref.child("Convo")){

            @Override
            public int getItemViewType(int position) {
                Message m=getItem(position);
                if(m.getUsr_type().equals("user")){
                    return R.layout.query_message;
                }
                else
                    return R.layout.response_messag;
            }

            @Override
            protected void populateViewHolder(chat_rec viewHolder, Message model, int position) {
                /*if(model.getUsr_type()=="user"){
                    viewHolder.query.setText(model.getMsg());
                    viewHolder.query.setVisibility(View.VISIBLE);
                }
                else {
                    viewHolder.response.setText(model.getMsg());
                    viewHolder.response.setVisibility(View.GONE);
                    viewHolder.response.setVisibility(View.VISIBLE);
                }
                */
                if(getItemViewType(position)==R.layout.query_message){
                    viewHolder.q1.setText(model.getMsg());
                }
                else if(getItemViewType(position)==R.layout.response_messag){
                    viewHolder.r1.setText(model.getMsg());
                }
            }
        };
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int msgCount = adapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (msgCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    rv.scrollToPosition(positionStart);

                }

            }
        });
        rv.setAdapter(adapter);
    }

    @Override
    public void onResult(AIResponse result) {

        Result res=result.getResult();
        String q=res.getResolvedQuery();
        Message m1=new Message(q,"user");
        ref.child("Convo").push().setValue(m1);

        List<ResponseMessage> messageList=res.getFulfillment().getMessages();
        ResponseMessage resp=messageList.get(0);
        String x=resp.toString();
        String r=res.getFulfillment().getSpeech();
        Message m2=new Message(r,"bot");
        ref.child("Convo").push().setValue(m2);
    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }
}

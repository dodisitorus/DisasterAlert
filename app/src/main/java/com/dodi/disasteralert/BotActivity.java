package com.dodi.disasteralert;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dodi.disasteralert.adapters.ChatAdapter;
import com.dodi.disasteralert.models.Message;
import com.dodi.disasteralert.utils.BlurImage;
import com.dodi.disasteralert.utils.StreamPlayer2;
import com.dodi.disasteralert.listeners.StreamPlayerListener;
import com.dodi.disasteralert.utils.Utils;
import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;
import com.ibm.cloud.sdk.core.http.HttpMediaType;
import com.ibm.cloud.sdk.core.http.Response;
import com.ibm.cloud.sdk.core.http.ServiceCall;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.assistant.v2.model.DialogNodeOutputOptionsElement;
import com.ibm.watson.assistant.v2.model.MessageInput;
import com.ibm.watson.assistant.v2.model.MessageOptions;
import com.ibm.watson.assistant.v2.model.MessageResponse;
import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;
import com.ibm.watson.assistant.v2.model.SessionResponse;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionAlternative;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResult;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.ibm.watson.speech_to_text.v1.websocket.RecognizeCallback;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;
import com.skyfishjy.library.RippleBackground;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class BotActivity extends AppCompatActivity {

    private ConstraintLayout container_call;
    private ConstraintLayout container_chat;
    private ImageView image_wait;
    private TextView guide_wait_text;
    private TextView text_connection;

    private EditText inputMessage;
    private RecyclerView recyclerView;

    private TextToSpeech textToSpeechService;
    private SpeechToText speechToTextService;
    private Assistant assistantService;
    private Response<SessionResponse> watsonAssistantSession;

    private MicrophoneInputStream capture;
    private MicrophoneHelper microphoneHelper;

    private StreamPlayer2 streamPlayer = new StreamPlayer2();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private AudioManager audioManager;

    private String textRecord = "";
    private boolean initialRequest;
    private boolean isMessageVoice = true;
    private boolean isStartConversation = false;
    private boolean isInterrupt = false;
    private boolean isStartRecord = false;
    private boolean isBotTalking = false;

    private ChatAdapter mAdapter;
    private ArrayList<Message> messageArrayList;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        ConstraintLayout constraintLayout = findViewById(R.id.parent);
        container_call = findViewById(R.id.container_call);
        container_chat = findViewById(R.id.container_chat);
        RippleBackground rippleBackground = findViewById(R.id.start_btn);
        SwipeButton swipeButton = findViewById(R.id.swipe_btn);
        CardView card_chat = findViewById(R.id.card_chat);
        CardView card_wait = findViewById(R.id.card_wait);
        image_wait = findViewById(R.id.image_wait);
        guide_wait_text = findViewById(R.id.guide_wait);
        text_connection = findViewById(R.id.text_connection);
        // chat
        Toolbar toolbar = findViewById(R.id.toolbar);
        inputMessage = findViewById(R.id.messageInput);
        Button btnSend = findViewById(R.id.btn_send);
        ImageView btnFile = findViewById(R.id.btn_file);
        recyclerView = findViewById(R.id.recycler_view);

        // set toolbar
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToCallBot();
            }
        });

        // start ripple animation
        rippleBackground.startRippleAnimation();

        // create blur background image
        Bitmap bg1 = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.bg1);
        Bitmap bitmap = BlurImage.fastblur(bg1, 1f, 50);
        BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
        constraintLayout.setBackground(ob);

        // start service IBM
        textToSpeechService = initTextToSpeechService();
        speechToTextService = initSpeechToTextService();
        assistantService = initAssistantService();

        // helper
        microphoneHelper = new MicrophoneHelper(this);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        text_connection.setText("");

        swipeButton.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                isStartConversation = active;
                if (active) {
                    // start focus record audio
                    audioManager.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                    // start bot with voice
                    text_connection.setText(getString(R.string.connecting));
                    // get first bot welcome
                    isBotTalking = true;
                    getRespondMessageAssistant();
                } else {
                    // interrupt assistant
                    streamPlayer.interrupt();
                    // stop record
                    microphoneHelper.closeInputStream();
                    // reset message
                    messageArrayList.clear();
                    finish();
                    startActivity(getIntent());
                }
            }
        });

        card_wait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStartConversation) {
                    if (!isInterrupt) {
                        if (!isStartRecord) {
                            image_wait.setImageDrawable(getDrawable(R.drawable.ic_play));
                            guide_wait_text.setText(R.string.ok_i_listen_to_you);
                            // interrupt assistant
                            streamPlayer.interrupt();
                            isInterrupt = true;
                            // start record when start talking
                            startRecord();
                        }
                    }
                } else {
                    //Toast.makeText(mContext, getString(R.string.swipe_to_start), Toast.LENGTH_SHORT).show();
                    text_connection.setText(getString(R.string.swipe_to_start));
                }
            }
        });

        card_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChatBot();
            }
        });

        // Create Chat View
        messageArrayList = new ArrayList<>();
        mAdapter = new ChatAdapter(messageArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        inputMessage.setText("");
        initialRequest = true;

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection()) {
                    isMessageVoice = false;
                    getRespondMessageAssistant();
                }
            }
        });

        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                recordMessage();
            }
        });
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (container_call.getVisibility() == View.GONE) {
            backToCallBot();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    private void goToChatBot() {
        if (isStartConversation) {
            container_call.setVisibility(View.GONE);
            container_chat.setVisibility(View.VISIBLE);
            isMessageVoice = false;
        } else {
            //Toast.makeText(mContext, getText(R.string.swipe_to_start), Toast.LENGTH_SHORT).show();
            text_connection.setText(getText(R.string.swipe_to_start));
        }
    }

    private void backToCallBot() {
        container_chat.setVisibility(View.GONE);
        container_call.setVisibility(View.VISIBLE);
        isMessageVoice = true;
        // hide keyboard soft
        Utils.hideSoftKey(inputMessage);
        if (!isStartRecord) {
            if (!isBotTalking) {
                startRecord();
            }
        }
    }

    private AudioManager.OnAudioFocusChangeListener focusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    Log.d("AudioFocusChange", "" + focusChange);
                    switch (focusChange) {
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) :
                            // Lower the volume while ducking.
                            mediaPlayer.setVolume(0.2f, 0.2f);
                            break;
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) :
                            mediaPlayer.pause();
                            break;
                        case (AudioManager.AUDIOFOCUS_LOSS) :
                            mediaPlayer.stop();
                            break;
                        case (AudioManager.AUDIOFOCUS_GAIN_TRANSIENT):
                            break;
                        case (AudioManager.STREAM_MUSIC):
                            break;
                        case (AudioManager.AUDIOFOCUS_GAIN) :
                            // Return the volume to normal and resume if paused.
                            mediaPlayer.setVolume(1f, 1f);
                            mediaPlayer.start();
                            streamPlayer.interrupt();
                            break;
                        default: break;
                    }
                }
            };

    private void showError(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BotActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    // TEXT TO SPEECH SERVICE
    private TextToSpeech initTextToSpeechService() {
        Authenticator authenticator = new IamAuthenticator(getString(R.string.text_speech_iam_apikey));
        TextToSpeech service = new TextToSpeech(authenticator);
        service.setServiceUrl(getString(R.string.text_speech_url));
        return service;
    }

    private SpeechToText initSpeechToTextService() {
        Authenticator authenticator = new IamAuthenticator(getString(R.string.speech_text_iam_apikey));
        SpeechToText service = new SpeechToText(authenticator);
        service.setServiceUrl(getString(R.string.speech_text_url));
        return service;
    }

    private Assistant initAssistantService() {
        Authenticator authenticator = new IamAuthenticator(getString(R.string.watson_assistant_iam_apikey));
        Assistant service = new Assistant("2019-02-28", authenticator);
        service.setServiceUrl(getString(R.string.watson_assistant_url));
        return service;
    }

    public void chooseFile(View view) {

    }

    // SPEECH TO TEXT SERVICE
    private void startRecord() {
        isStartRecord = true;
        //Toast.makeText(BotActivity.this, R.string.your_turn_i_listen, Toast.LENGTH_SHORT).show();
        text_connection.setText(getString(R.string.your_turn_i_listen));
        capture = microphoneHelper.getInputStream(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    speechToTextService.recognizeUsingWebSocket(getRecognizeOptions(capture), new MicrophoneRecognizeDelegate());
                } catch (Exception e) {
                    showError(e);
                }
            }
        }).start();
    }

    private RecognizeOptions getRecognizeOptions(InputStream captureStream) {
        return new RecognizeOptions.Builder()
                .audio(captureStream)
                .contentType(ContentType.OPUS.toString())
                .model("en-US_BroadbandModel")
                .interimResults(true)
                .speakerLabels(true)
                .inactivityTimeout(2000)
                .smartFormatting(true)
                .wordConfidence(true)
                .wordAlternativesThreshold((float) 0.01)
                .build();
    }

    private class MicrophoneRecognizeDelegate extends BaseRecognizeCallback implements RecognizeCallback {
        @Override
        public void onTranscription(SpeechRecognitionResults speechResults) {
            if (speechResults.getResults() != null) {
                for (int i = 0; i < speechResults.getResults().size(); i++) {
                    SpeechRecognitionResult transcript = speechResults.getResults().get(i);
                    if (transcript.isXFinal()) {
                        // stop record
                        isStartRecord = false;
                        microphoneHelper.closeInputStream();
                        // set result record into text record
                        SpeechRecognitionAlternative speechAlternative = transcript.getAlternatives().get(0);
                        textRecord = speechAlternative.getTranscript();
                        // finish interrupt
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finishInterrupt();
                            }
                        });
                        // when record finish, automatically request respond assistant
                        getRespondMessageAssistant();
                    }
                }
            }
        }

        private void finishInterrupt() {
            image_wait.setImageDrawable(getDrawable(R.drawable.icon_pause));
            guide_wait_text.setText(R.string.click_to_interrupt);
            isInterrupt = false;
        }

        @Override
        public void onError(Exception e) {
            try {
                capture.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void onDisconnected() {

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class SynthesisTask extends AsyncTask<String, Void, String> implements StreamPlayerListener {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected String doInBackground(String... params) {
            SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                    .text(params[0])
                    .voice(SynthesizeOptions.Voice.EN_US_LISAVOICE)
                    .accept(HttpMediaType.AUDIO_WAV)
                    .build();
            isBotTalking = true;
            streamPlayer.playStream(textToSpeechService.synthesize(synthesizeOptions).execute().getResult(), BotActivity.this, this);
            return "Did synthesize";
        }

        // when assistant finish give respond, record start active to listen
        @Override
        public void onMarkerReached(AudioTrack audioTrack) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isMessageVoice) {
                        if (!isInterrupt) {
                            startRecord();
                        }
                    }
                    isBotTalking = false;
                }
            });
        }
    }

    public static final String DATE_FORMAT_1 = "hh:mm a";
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_1);
        Date today = new Date();
        return dateFormat.format(today);
    }

        // ASSISTANT SERVICE
    // Sending a message to Watson Assistant Service
    private void getRespondMessageAssistant() {
        // start process respond assistant
        final String messageUser;
        if (isMessageVoice) {
            messageUser = textRecord.trim();
            textRecord = "";
        } else {
            messageUser = inputMessage.getText().toString().trim();
            inputMessage.setText("");
        }

        if (!initialRequest) {
            Message inputMessage = new Message();
            inputMessage.setMessage(messageUser);
            inputMessage.setTime(getCurrentTime());
            inputMessage.setId("1");
            messageArrayList.add(inputMessage);
        } else {
            Message inputMessage = new Message();
            inputMessage.setMessage(messageUser);
            inputMessage.setTime(getCurrentTime());
            inputMessage.setId("100");
            initialRequest = false;
        }

        mAdapter.notifyDataSetChanged();

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    if (watsonAssistantSession == null) {
                        ServiceCall<SessionResponse> call = assistantService.createSession(new CreateSessionOptions.Builder().assistantId(getString(R.string.watson_assistant_id)).build());
                        watsonAssistantSession = call.execute();
                    }

                    MessageInput input = new MessageInput.Builder()
                            .text(messageUser)
                            .build();

                    MessageOptions options = new MessageOptions.Builder()
                            .assistantId(getString(R.string.watson_assistant_id))
                            .input(input)
                            .sessionId(watsonAssistantSession.getResult().getSessionId())
                            .build();

                    Response<MessageResponse> response = assistantService.message(options).execute();

                    if (response.getResult().getOutput() != null && !response.getResult().getOutput().getGeneric().isEmpty()) {
                        List<RuntimeResponseGeneric> responses = response.getResult().getOutput().getGeneric();
                        for (RuntimeResponseGeneric r : responses) {
                            Message outMessage;
                            text_connection.setText("");
                            switch (r.responseType()) {
                                case "text":
                                    outMessage = new Message();
                                    outMessage.setMessage(r.text());
                                    outMessage.setTime(getCurrentTime());
                                    outMessage.setId("2");

                                    messageArrayList.add(outMessage);

                                    // speak the message
                                    if (isMessageVoice && isStartConversation) {
                                        new SynthesisTask().execute(outMessage.getMessage());
                                    }
                                    break;

                                case "option":
                                    outMessage = new Message();
                                    String title = r.title();
                                    StringBuilder OptionsOutput = new StringBuilder();
                                    for (int i = 0; i < r.options().size(); i++) {
                                        DialogNodeOutputOptionsElement option = r.options().get(i);
                                        OptionsOutput.append(option.getLabel()).append("\n");
                                    }
                                    outMessage.setMessage(title + "\n" + OptionsOutput);
                                    outMessage.setId("2");
                                    outMessage.setTime(getCurrentTime());

                                    messageArrayList.add(outMessage);

                                    // speak the message
                                    if (isMessageVoice && isStartConversation) {
                                        new SynthesisTask().execute(outMessage.getMessage());
                                    }
                                    break;

                                case "image":
                                    outMessage = new Message(r);
                                    outMessage.setTime(getCurrentTime());
                                    messageArrayList.add(outMessage);

                                    // speak the description
                                    if (isMessageVoice && isStartConversation) {
                                        new SynthesisTask().execute("You received an image: " + outMessage.getTitle() + outMessage.getDescription());
                                    }
                                    break;
                                default:
                                    Log.e("Error", "Unhandled message type");
                            }
                        }

                        runOnUiThread(new Runnable() {
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                if (mAdapter.getItemCount() > 1) {
                                    Objects.requireNonNull(recyclerView.getLayoutManager()).smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }


    /**
     * On request permissions result.
     *
     * @param requestCode the request code
     * @param permissions the permissions
     * @param grantResults the grant results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MicrophoneHelper.REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission to record audio denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean checkInternetConnection() {
        // get Connectivity Manager object to check connection
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Check for network connections
        if (isConnected) {
            return true;
        } else {
            Toast.makeText(this, " No Internet Connection available ", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
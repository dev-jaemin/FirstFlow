package com.example.firstflow.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.SoundPool;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.firstflow.AudioCaptureService;
import com.example.firstflow.R;

public class XylophoneFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    SoundPool soundPool = new SoundPool.Builder().setMaxStreams(8).build();
    boolean soundPoolLoaded = false;


    public XylophoneFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static XylophoneFragment newInstance(String param1, String param2) {
        XylophoneFragment fragment = new XylophoneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_xylophone, container, false);
        mToast = new Toast(getContext());
        ToggleButton recordBtn = v.findViewById(R.id.xylophone_toggleButton);

        Button[] keyboards = {
                (Button) v.findViewById(R.id.xylophone_do),
                (Button) v.findViewById(R.id.xylophone_re),
                (Button) v.findViewById(R.id.xylophone_mi),
                (Button) v.findViewById(R.id.xylophone_fa),
                (Button) v.findViewById(R.id.xylophone_so),
                (Button) v.findViewById(R.id.xylophone_la),
                (Button) v.findViewById(R.id.xylophone_ti),
                (Button) v.findViewById(R.id.xylophone_doHigh),
        };

        int[] files = {
                R.raw.do_,
                R.raw.re,
                R.raw.mi,
                R.raw.fa,
                R.raw.so,
                R.raw.la,
                R.raw.ti,
                R.raw.dohigh
        };

        int[] colors = {
                Color.parseColor("#ff7f7f"),
                Color.parseColor("#f3cdad"),
                Color.parseColor("#fff77f"),
                Color.parseColor("#bae7af"),
                Color.parseColor("#beebfd"),
                Color.parseColor("#a9a0fc"),
                Color.parseColor("#cb9ffd"),
                Color.parseColor("#ff7f7f"),
        };


        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPoolLoaded = true;
            }
        });

        for (int i = 0; i < keyboards.length; i++) {
            int soundId = soundPool.load(v.getContext(), files[i], 1);
            Button keyboard = keyboards[i];
            int buttonColor = colors[i];

            keyboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (soundPoolLoaded) {
                        Handler handler = new Handler();
                        soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1);
                        keyboard.setBackgroundColor(buttonColor);

                        // ????????? ????????? ????????? 0.2??? ?????? ???????????? ?????????.
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                keyboard.setBackgroundResource(R.drawable.silver2);
                            }
                        }, 200);
                    }
                }
            });
        }

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recordBtn.isChecked()) {
                    startCapturing();
                } else {
                    stopCapturing();

                }
            }
        });

        return v;
    }

    private final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 42;
    private final int MEDIA_PROJECTION_REQUEST_CODE = 13;
    private Toast mToast;

    private void startCapturing() {
        if (!isRecordAudioPermissionGranted()) {
            requestRecordAudioPermission();
        } else {
            startMediaProjectionRequest();
        }
    }

    private void stopCapturing() {
        changeText(false);

        Intent intent = new Intent(getContext(), AudioCaptureService.class);
        intent.setAction(AudioCaptureService.ACTION_STOP);

        ContextCompat.startForegroundService(getContext(), intent);

        if (mToast != null) {
            mToast.cancel();
        }
        mToast.makeText(getContext(), "?????? ??????", Toast.LENGTH_SHORT).show();
    }

    private void changeText(boolean isChecked) {
        if (isChecked) {
            TextView t = getView().findViewById(R.id.xylophone_explanation);
            t.setText("???????????? ????????? ?????? ???...");
        } else {
            TextView t = getView().findViewById(R.id.xylophone_explanation);
            t.setText("?????? ????????? ??????????????????.");
        }
    }

    private boolean isRecordAudioPermissionGranted() {
        return ContextCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestRecordAudioPermission() {
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.RECORD_AUDIO},
                RECORD_AUDIO_PERMISSION_REQUEST_CODE
        );
    }

    private void startMediaProjectionRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MediaProjectionManager mediaProjectionManager =
                    (MediaProjectionManager) getContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);

            startActivityForResult(
                    mediaProjectionManager.createScreenCaptureIntent(),
                    MEDIA_PROJECTION_REQUEST_CODE
            );
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MEDIA_PROJECTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mToast.makeText(
                        getContext(),
                        "????????? ???????????????.",
                        Toast.LENGTH_SHORT
                ).show();

                changeText(true);

                Intent intent = new Intent(getContext(), AudioCaptureService.class);
                intent.setAction(AudioCaptureService.ACTION_START);
                intent.putExtra(AudioCaptureService.EXTRA_RESULT_DATA, data);


                ContextCompat.startForegroundService(getContext(), intent);
            } else {
                mToast.makeText(
                        getContext(), "???????????? ????????? ???????????? ?????? ???????????????.",
                        Toast.LENGTH_SHORT
                ).show();

                // Toggle ????????? ?????? ?????? ???????????? ???????????? ?????? ???????????? ????????? ???.
                ToggleButton t = getView().findViewById(R.id.xylophone_toggleButton);
                t.setChecked(false);
            }
        }
    }

}
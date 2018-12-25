package ln_zap.zap.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ln_zap.zap.QRCodeScanner.QRCodeScannerActivity;
import ln_zap.zap.R;
import ln_zap.zap.ReceiveActivity;
import ln_zap.zap.util.BtcDisplayUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class Wallet extends Fragment {


    public Wallet() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);


        // Placeholder Balance
        TextView tvBalance = view.findViewById(R.id.Balance);
        TextView tvBalanceUnit = view.findViewById(R.id.BalanceUnit);

        long myBalance = 120871010L;

        tvBalance.setText(BtcDisplayUtil.getDisplayAmount(myBalance, getActivity()));
        tvBalanceUnit.setText(BtcDisplayUtil.getDisplayUnit(getActivity()));


        // Action when clicked on "send"
        Button btnSend = view.findViewById(R.id.sendButton);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), QRCodeScannerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
            }
        });


        // Action when clicked on "receive"
        Button btnReceive = view.findViewById(R.id.receiveButton);
        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReceiveActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }



}

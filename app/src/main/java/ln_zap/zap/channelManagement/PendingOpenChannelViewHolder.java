package ln_zap.zap.channelManagement;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lightningnetwork.lnd.lnrpc.NodeInfo;

import ln_zap.zap.R;
import ln_zap.zap.util.MonetaryUtil;
import ln_zap.zap.util.OnSingleClickListener;
import ln_zap.zap.util.Wallet;


public class PendingOpenChannelViewHolder extends RecyclerView.ViewHolder {

    private TextView mRemoteName;
    private TextView mStatus;
    private TextView mLocalBalance;
    private TextView mRemoteBalance;
    private TextView mCapacity;
    private ImageView mStatusDot;
    private ProgressBar mLocalBar;
    private ProgressBar mRemoteBar;
    private View mRootView;
    private Context mContext;

    public PendingOpenChannelViewHolder(View v) {
        super(v);
        mRemoteName = v.findViewById(R.id.remoteName);
        mStatus = v.findViewById(R.id.state);
        mStatusDot = v.findViewById(R.id.statusDot);
        mLocalBalance = v.findViewById(R.id.localBalance);
        mRemoteBalance = v.findViewById(R.id.remoteBalance);
        mCapacity = v.findViewById(R.id.capacity);
        mLocalBar = v.findViewById(R.id.localBar);
        mRemoteBar = v.findViewById(R.id.remoteBar);
        mRootView = v.findViewById(R.id.transactionRootView);
        mContext = v.getContext();
    }

    public void bindPendingOpenChannelItem(final PendingOpenChannelItem pendingOpenChannelItem) {


        // Set state
        mStatus.setText(R.string.channel_state_pending_open);
        mStatusDot.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.lightningOrange)));
        mRootView.setAlpha(0.65f);


        // Set capacities
        long availableCapacity = pendingOpenChannelItem.getChannel().getChannel().getCapacity() - pendingOpenChannelItem.getChannel().getCommitFee();
        float localBarValue = (float) ((double) pendingOpenChannelItem.getChannel().getChannel().getLocalBalance() / (double) availableCapacity);
        float remoteBarValue = (float) ((double) pendingOpenChannelItem.getChannel().getChannel().getRemoteBalance() / (double) availableCapacity);

        mLocalBar.setProgress((int) (localBarValue * 100f));
        mRemoteBar.setProgress((int) (remoteBarValue * 100f));

        mCapacity.setText(MonetaryUtil.getInstance().getPrimaryDisplayAmountAndUnit(availableCapacity));
        mLocalBalance.setText(MonetaryUtil.getInstance().getPrimaryDisplayAmountAndUnit(pendingOpenChannelItem.getChannel().getChannel().getLocalBalance()));
        mRemoteBalance.setText(MonetaryUtil.getInstance().getPrimaryDisplayAmountAndUnit(pendingOpenChannelItem.getChannel().getChannel().getRemoteBalance()));


        // Set name

        for (NodeInfo i : Wallet.getInstance().mNodeInfos) {
            if (i.getNode().getPubKey().equals(pendingOpenChannelItem.getChannel().getChannel().getRemoteNodePub())) {
                if (i.getNode().getAlias().startsWith(i.getNode().getPubKey().substring(0, 8))) {
                    String unnamed = mContext.getResources().getString(R.string.channel_no_alias);
                    mRemoteName.setText(unnamed + " (" + i.getNode().getPubKey().substring(0, 5) + "...)");
                } else {
                    mRemoteName.setText(i.getNode().getAlias());
                }
                break;
            } else {
                mRemoteName.setText(pendingOpenChannelItem.getChannel().getChannel().getRemoteNodePub());
            }
        }


        // Set on click listener
        mRootView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(mContext, OpenChannelDetailsActivity.class);
                mContext.startActivity(intent);
            }
        });

    }
}
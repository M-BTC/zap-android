package zapsolutions.zap.channelManagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lightningnetwork.lnd.lnrpc.Channel;
import com.github.lightningnetwork.lnd.lnrpc.PendingChannelsResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.List;

import zapsolutions.zap.R;
import zapsolutions.zap.baseClasses.BaseAppCompatActivity;
import zapsolutions.zap.fragments.OpenChannelBSDFragment;
import zapsolutions.zap.lightning.LightningNodeUri;
import zapsolutions.zap.util.Wallet;

public class ManageChannelsActivity extends BaseAppCompatActivity implements ChannelSelectListener, Wallet.ChannelsUpdatedSubscriptionListener {

    private static int REQUEST_CODE_OPEN_CHANNEL = 100;
    private RecyclerView mRecyclerView;
    private ChannelItemAdapter mAdapter;
    private TextView mEmptyListText;
    private List<ChannelListItem> mChannelItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_channels);

        Wallet.getInstance().registerChannelsUpdatedSubscriptionListener(this);

        mRecyclerView = findViewById(R.id.channelsList);
        mEmptyListText = findViewById(R.id.listEmpty);

        mChannelItems = new ArrayList<>();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ManageChannelsActivity.this, ScanNodePubKeyActivity.class);
            startActivityForResult(intent, REQUEST_CODE_OPEN_CHANNEL);
        });
        mAdapter = new ChannelItemAdapter(mChannelItems, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateChannelsDisplayList();
    }

    private void updateChannelsDisplayList() {
        mChannelItems.clear();

        List<ChannelListItem> offlineChannels = new ArrayList<>();

        // Add all open channel items

        if (Wallet.getInstance().mOpenChannelsList != null) {
            for (Channel c : Wallet.getInstance().mOpenChannelsList) {
                OpenChannelItem openChannelItem = new OpenChannelItem(c);
                if (c.getActive()) {
                    mChannelItems.add(openChannelItem);
                } else {
                    offlineChannels.add(openChannelItem);
                }
            }
        }

        // Add all pending channel items

        // Add open pending
        if (Wallet.getInstance().mPendingOpenChannelsList != null) {
            for (PendingChannelsResponse.PendingOpenChannel c : Wallet.getInstance().mPendingOpenChannelsList) {
                PendingOpenChannelItem pendingOpenChannelItem = new PendingOpenChannelItem(c);
                mChannelItems.add(pendingOpenChannelItem);
            }
        }

        // Add closing pending
        if (Wallet.getInstance().mPendingClosedChannelsList != null) {
            for (PendingChannelsResponse.ClosedChannel c : Wallet.getInstance().mPendingClosedChannelsList) {
                PendingClosingChannelItem pendingClosingChannelItem = new PendingClosingChannelItem(c);
                mChannelItems.add(pendingClosingChannelItem);
            }
        }

        // Add force closing pending
        if (Wallet.getInstance().mPendingForceClosedChannelsList != null) {
            for (PendingChannelsResponse.ForceClosedChannel c : Wallet.getInstance().mPendingForceClosedChannelsList) {
                PendingForceClosingChannelItem pendingForceClosingChannelItem = new PendingForceClosingChannelItem(c);
                mChannelItems.add(pendingForceClosingChannelItem);
            }
        }

        // Add waiting for close
        if (Wallet.getInstance().mPendingWaitingCloseChannelsList != null) {
            for (PendingChannelsResponse.WaitingCloseChannel c : Wallet.getInstance().mPendingWaitingCloseChannelsList) {
                WaitingCloseChannelItem waitingCloseChannelItem = new WaitingCloseChannelItem(c);
                mChannelItems.add(waitingCloseChannelItem);
            }
        }

        // Show offline channels at the bottom
        mChannelItems.addAll(offlineChannels);

        // Show "No channels" if the list is empty
        if (mChannelItems.size() == 0) {
            mEmptyListText.setVisibility(View.VISIBLE);
        } else {
            mEmptyListText.setVisibility(View.GONE);
        }

        // Update the view
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChannelSelect(ByteString channel, int type) {
        if (channel != null) {
            ChannelDetailBSDFragment channelDetailBSDFragment = new ChannelDetailBSDFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(ChannelDetailBSDFragment.ARGS_CHANNEL, channel);
            bundle.putInt(ChannelDetailBSDFragment.ARGS_TYPE, type);
            channelDetailBSDFragment.setArguments(bundle);
            channelDetailBSDFragment.show(getSupportFragmentManager(), ChannelDetailBSDFragment.TAG);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_OPEN_CHANNEL && resultCode == RESULT_OK) {
            if (data != null) {
                LightningNodeUri nodeUri = (LightningNodeUri) data.getSerializableExtra(ScanNodePubKeyActivity.EXTRA_NODE_URI);

                OpenChannelBSDFragment openChannelBSDFragment = new OpenChannelBSDFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(OpenChannelBSDFragment.ARGS_NODE_URI, nodeUri);
                openChannelBSDFragment.setArguments(bundle);
                openChannelBSDFragment.show(getSupportFragmentManager(), OpenChannelBSDFragment.TAG);
            }
        }
    }

    @Override
    protected void onDestroy() {
        Wallet.getInstance().unregisterChannelsUpdatedSubscriptionListener(this);

        super.onDestroy();
    }

    @Override
    public void onChannelsUpdated() {
        runOnUiThread(this::updateChannelsDisplayList);
    }
}

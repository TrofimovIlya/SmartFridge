package ru.hse.smartrefrigerator.views.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;
import ru.hse.smartrefrigerator.R;
import ru.hse.smartrefrigerator.controllers.AbstractDataProvider;

import java.text.SimpleDateFormat;

/**
 * @author Ilya Trofimov
 */

public class SwipeableProductItemAdapter
        extends RecyclerView.Adapter<SwipeableProductItemAdapter.MyViewHolder>
        implements SwipeableItemAdapter<SwipeableProductItemAdapter.MyViewHolder> {

    private AbstractDataProvider mProvider;
    private EventListener mEventListener;
    private View.OnClickListener mItemViewOnClickListener;
    private View.OnClickListener mSwipeableViewContainerOnClickListener;

    public SwipeableProductItemAdapter(AbstractDataProvider dataProvider) {
        mProvider = dataProvider;
        mItemViewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemViewClick(v);
            }
        };
        mSwipeableViewContainerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwipeableViewContainerClick(v);
            }
        };

        // SwipeableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    private void onItemViewClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(v);
        }
    }

    private void onSwipeableViewContainerClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }
    }

    @Override
    public long getItemId(int position) {
        return mProvider.getItem(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return mProvider.getItem(position).getViewType();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_item_product, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final AbstractDataProvider.Data item = mProvider.getItem(position);

        // set listeners
        holder.itemView.setOnClickListener(mItemViewOnClickListener);
        holder.mContainer.setOnClickListener(mSwipeableViewContainerOnClickListener);

        String name = item.getProduct().getName();

        SimpleDateFormat ddMMMMyyFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        String expDate = ddMMMMyyFormat.format(item.getProduct().getExpirationDate());

        // set text


        String symbol = (name == null || name.length() < 1) ? "#" : name.substring(0, 1).toUpperCase();
        holder.mImageViewIcon.setImageDrawable(TextDrawable.builder().buildRound(symbol, ColorGenerator.MATERIAL.getColor(name + expDate)));
        holder.mTextViewName.setText(name);
        holder.mTextViewDate.setText("Употребить до: " + expDate);

        // set background resource (target view ID: container)
        final int swipeState = holder.getSwipeStateFlags();

        if ((swipeState & RecyclerViewSwipeManager.STATE_FLAG_IS_UPDATED) != 0) {
            int bgResId;

            if ((swipeState & RecyclerViewSwipeManager.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_swiping_active_state;
            } else if ((swipeState & RecyclerViewSwipeManager.STATE_FLAG_SWIPING) != 0) {
                bgResId = R.drawable.bg_item_swiping_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }

        // set swiping properties
        holder.setSwipeItemSlideAmount(0);
    }

    @Override
    public int getItemCount() {
        return mProvider.getCount();
    }

    @Override
    public int onGetSwipeReactionType(MyViewHolder holder, int position, int x, int y) {
        return mProvider.getItem(position).getSwipeReactionType();
    }

    @Override
    public void onSetSwipeBackground(MyViewHolder holder, int position, int type) {
        int bgRes = 0;
        switch (type) {
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_neutral;
                break;
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_left;
                break;
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_right;
                break;
        }

        holder.itemView.setBackgroundResource(bgRes);
    }

    @Override
    public int onSwipeItem(MyViewHolder holder, int position, int result) {
        switch (result) {
            // swipe right or left
            case RecyclerViewSwipeManager.RESULT_SWIPED_RIGHT:
            case RecyclerViewSwipeManager.RESULT_SWIPED_LEFT:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM;

            // other do nothing
            case RecyclerViewSwipeManager.RESULT_CANCELED:
            default:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
        }
    }

    @Override
    public void onPerformAfterSwipeReaction(MyViewHolder holder, int position, int result, int reaction) {
        if (reaction == RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM) {
            mProvider.removeItem(position);
            notifyItemRemoved(position);

            if (mEventListener != null) {
                mEventListener.onItemRemoved(position);
            }
        }
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    public interface EventListener {
        void onItemRemoved(int position);

        void onItemViewClicked(View v);
    }

    public static class MyViewHolder extends AbstractSwipeableItemViewHolder {
        public ViewGroup mContainer;
        public View mDragHandle;
        public TextView mTextViewName;
        public ImageView mImageViewIcon;
        public TextView mTextViewDate;

        public MyViewHolder(View v) {
            super(v);
            mContainer = (ViewGroup) v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.drag_handle);
            mTextViewName = (TextView) v.findViewById(R.id.tv_product_name);
            mTextViewDate = (TextView) v.findViewById(R.id.tv_date);
            mImageViewIcon = (ImageView) v.findViewById(R.id.iv_roundedIcon);

            mDragHandle.setVisibility(View.GONE);  // hide the drag handle
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }
    }
}

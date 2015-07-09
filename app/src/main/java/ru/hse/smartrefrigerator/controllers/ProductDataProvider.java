package ru.hse.smartrefrigerator.controllers;

/**
 * @author Ilya Trofimov
 */

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import ru.hse.smartrefrigerator.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductDataProvider extends AbstractDataProvider {
    private List<ConcreteData> mData;
    private ConcreteData mLastRemovedData;
    private int mLastRemovedPosition = -1;

    public ProductDataProvider() {
        initData();
    }

    public ProductDataProvider(ArrayList<Product> addThis) {
        addProducts(addThis);
    }

    public ProductDataProvider(Product addThis) {
        addProduct(addThis);
    }

    public void addProduct(Product addThis) {
        initData();

        long id = mData.size();
        mData.add(new ConcreteData(id, addThis));
    }

    public void addProducts(ArrayList<Product> addThis) {
        initData();

        for (Product product : addThis) {
            final long id = mData.size();
            mData.add(new ConcreteData(id, product));
        }
    }

    public void initData() {
        if (mData == null) {
            mData = new ArrayList<ConcreteData>();
        }
    }

    public ArrayList<Product> getProducts() {
        ArrayList<Product> products = new ArrayList<Product>();
        for (ConcreteData dataItem : mData) {
            products.add(dataItem.getProduct());
        }

        return products;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Data getItem(int index) {
        if (index < 0 || index >= getCount()) {
            throw new IndexOutOfBoundsException("index = " + index);
        }

        return mData.get(index);
    }

    @Override
    public int undoLastRemoval() {
        if (mLastRemovedData != null) {
            int insertedPosition;
            if (mLastRemovedPosition >= 0 && mLastRemovedPosition < mData.size()) {
                insertedPosition = mLastRemovedPosition;
            } else {
                insertedPosition = mData.size();
            }

            mData.add(insertedPosition, mLastRemovedData);

            mLastRemovedData = null;
            mLastRemovedPosition = -1;

            return insertedPosition;
        } else {
            return -1;
        }
    }

    @Override
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final ConcreteData item = mData.remove(fromPosition);

        mData.add(toPosition, item);
        mLastRemovedPosition = -1;
    }

    @Override
    public void removeItem(int position) {
        //noinspection UnnecessaryLocalVariable
        final ConcreteData removedItem = mData.remove(position);

        mLastRemovedData = removedItem;
        mLastRemovedPosition = position;
    }

    public static final class ConcreteData extends Data {
        private final long mId;
        private final Product mProduct;
        private boolean mPinnedToSwipeLeft;

        ConcreteData(long id, Product product) {
            this.mId = id;
            this.mProduct = product;
        }


        public Product getProduct() {
            return this.mProduct;
        }

        @Override
        public boolean isSectionHeader() {
            return false;
        }

        @Override
        public int getViewType() {
            return 0;
        }

        @Override
        public long getId() {
            return mId;
        }

        @Override
        public int getSwipeReactionType() {
            return RecyclerViewSwipeManager.REACTION_CAN_SWIPE_LEFT | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_RIGHT;
        }
    }
}
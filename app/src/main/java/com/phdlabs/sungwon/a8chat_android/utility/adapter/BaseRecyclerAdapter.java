package com.phdlabs.sungwon.a8chat_android.utility.adapter;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.Filter;
import android.widget.Filterable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by SungWon on 10/9/2017.
 * Updated by JPAM on 2/25/2018
 */

public abstract class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>
        implements Filterable {

    //TAG
    private static final String TAG = "BaseRecyclerAdapter";

    public static final int CHOICE_MODE_NONE = 0;
    public static final int CHOICE_MODE_SINGLE = 1;
    public static final int CHOICE_MODE_MULTIPLE = 2;

    @IntDef({CHOICE_MODE_NONE, CHOICE_MODE_SINGLE, CHOICE_MODE_MULTIPLE})
    @Retention(RetentionPolicy.SOURCE)
    @interface ChoiceMode {

    }

    private List<T> mOriginalValues, mFilteredValues;

    private Comparator<? super T> mSortComparator;

    private Function<T, Boolean> mFilter;
    @ChoiceMode
    private int choiceMode = CHOICE_MODE_NONE;

    private SparseBooleanArray selectedItems;
    private OnItemSelectionChangeListener<T> itemSelectedListener;
    GestureDetectorCompat gestureDetector;

    public interface OnItemSelectionChangeListener<T> {

        void onSelect(boolean isSelected, T data, int position);
    }

    /**
     * Lock used to modify the content of . Any write operation
     * performed on the array should be synchronized on this lock. This lock is also
     * used by the filter (see {@link #getFilter()} to make a synchronized copy of
     * the original array of data.
     */
    private final Object mLock = new Object();

    public BaseRecyclerAdapter() {
        mOriginalValues = new ArrayList<>();
        mFilteredValues = new ArrayList<>();
        selectedItems = new SparseBooleanArray();
    }

    public BaseRecyclerAdapter(@NonNull List<T> values) {
        this();
        addAll(values);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewHolder(LayoutInflater.from(parent.getContext()), parent, viewType);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        boolean selected = selectedItems.get(position, false);
        if (holder.itemView instanceof Checkable) {
            ((Checkable) holder.itemView).setChecked(selected);
        } else {
            holder.itemView.setActivated(selected);
        }
        onBindItemViewHolder(holder, getItem(position), position, getItemViewType(position));
    }

    public T getItem(int position) {
        if (mFilter != null) {
            return mFilteredValues.get(position);
        } else {
            return mOriginalValues.get(position);
        }
    }

    @Override
    public int getItemCount() {
        if (mFilter != null) {
            return mFilteredValues.size();
        } else {
            return mOriginalValues.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItemType(getItem(position));
    }

    public int getItemType(T t) {
        return 0;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        //Tap Gesture
        gestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new GestureListener(recyclerView));
        recyclerView.setOnTouchListener(new ItemTouchListener());

        //Delete Gesture
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    /**
     * Delete Callback
     * Left swipe action
     */
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            if(direction == ItemTouchHelper.LEFT){
                Log.i(TAG, "onSwiped: " + viewHolder.getAdapterPosition());
                //Broadcast the swiped position
                //notifyItemChanged(viewHolder.getAdapterPosition());
                notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        }
    };

    private class GestureListener implements GestureDetector.OnGestureListener {

        private GestureListener(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        RecyclerView recyclerView;

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            if (choiceMode == CHOICE_MODE_NONE) {
                return false;
            }
            final int action = MotionEventCompat.getActionMasked(event);
            if (action == MotionEvent.ACTION_UP) {
                View view = recyclerView.findChildViewUnder(event.getX(), event.getY());
                if (view == null) {
                    return false;
                }

                int position = recyclerView.getChildAdapterPosition(view);
                if (choiceMode == CHOICE_MODE_SINGLE) {
                    toggleSelection(position);
                    unselectExcluding(position);

                } else if (choiceMode == CHOICE_MODE_MULTIPLE) {
                    toggleSelection(position);
                }
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

    }

    private void unselectExcluding(int position) {
        for (int i = 0; i < selectedItems.size(); i++) {
            int posToUnselect = selectedItems.keyAt(i);
            if (posToUnselect != position) {
                if (selectedItems.get(posToUnselect)) {
                    selectedItems.put(posToUnselect, false);
                    notifyItemChanged(posToUnselect);
                }
            }
        }
    }

    public class ItemTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            return gestureDetector.onTouchEvent(event);
        }

    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            if (itemSelectedListener != null) {
                itemSelectedListener.onSelect(false, getItem(pos), pos);
            }
        } else {
            selectedItems.put(pos, true);
            if (itemSelectedListener != null) {
                itemSelectedListener.onSelect(true, getItem(pos), pos);
            }
        }
        notifyItemChanged(pos);
    }

    private void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<T> getSelectedItems() {
        List<T> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(mOriginalValues.get(selectedItems.keyAt(i)));
        }
        return items;
    }

    public void setItemSelectedListener(OnItemSelectionChangeListener<T> itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    public void setSortComparator(Comparator<? super T> mSortComparator) {
        this.mSortComparator = mSortComparator;
        if (mFilter == null) {
            Collections.sort(mOriginalValues, mSortComparator);
        } else {
            Collections.sort(mFilteredValues, mSortComparator);
        }
        notifyDataSetChanged();
    }

    public void add(int position, T item) {
        mOriginalValues.add(position, item);
        notifyItemInserted(position);
        int itemCount = mOriginalValues.size() - position;
        notifyItemRangeChanged(position, itemCount);
    }

    public void add(T item) {
        mOriginalValues.add(item);
        notifyItemInserted(mOriginalValues.size() - 1);
    }

    public void addAll(List<? extends T> items) {
        final int size = this.mOriginalValues.size();
        this.mOriginalValues.addAll(items);
        notifyItemRangeInserted(size, items.size());
    }

    public void setItems(List<? extends T> items) {
        clear();

        if (items != null) {
            addAll(items);
        }

        if (mFilter == null) {
            notifyDataSetChanged();
        } else {
            getFilter().filter("");
        }

    }

    public void set(int position, T item) {
        mOriginalValues.set(position, item);
        int itemCount = mOriginalValues.size() - position;
        notifyItemRangeChanged(position, itemCount);
    }

    public void removeChild(int position) {
        mOriginalValues.remove(position);
        notifyItemRemoved(position);
        int itemCount = mOriginalValues.size() - position;
        notifyItemRangeChanged(position, itemCount);
    }

    public void clear() {
        final int size = mOriginalValues.size();
        mOriginalValues.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void moveChildTo(int fromPosition, int toPosition) {
        if (toPosition != -1 && toPosition < mOriginalValues.size()) {
            final T item = mOriginalValues.remove(fromPosition);
            mOriginalValues.add(toPosition, item);
            notifyItemMoved(fromPosition, toPosition);
            int positionStart = fromPosition < toPosition ? fromPosition : toPosition;
            int itemCount = Math.abs(fromPosition - toPosition) + 1;
            notifyItemRangeChanged(positionStart, itemCount);
        }
    }

    public void sort(Comparator<? super T> comparator) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                Collections.sort(mOriginalValues, comparator);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence prefix) {
                FilterResults results = new FilterResults();
                ArrayList<T> newValues;
                if (mFilter == null) {

                    synchronized (mLock) {
                        newValues = new ArrayList<T>(mOriginalValues);
                    }

                } else {

                    ArrayList<T> values;
                    synchronized (mLock) {
                        values = new ArrayList<T>(mOriginalValues);
                    }

                    final int count = values.size();
                    newValues = new ArrayList<T>();

                    for (int i = 0; i < count; i++) {
                        final T value = values.get(i);
                        if (mFilter.apply(value)) {
                            newValues.add(value);
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                mFilteredValues.clear();
                mFilteredValues.addAll((Collection<? extends T>) results.values);
                notifyDataSetChanged();

            }
        };
    }

    public void setFilter(Function<T, Boolean> filter) {
        mFilter = filter;

        if (mFilter == null) {
            setItems(mOriginalValues);
        } else {
            getFilter().filter("");
        }
    }

    public int getChoiceMode() {
        return choiceMode;
    }

    public void setChoiceMode(@ChoiceMode int choiceMode) {
        this.choiceMode = choiceMode;
        if (choiceMode == CHOICE_MODE_NONE) {
            clearSelections();
        }
    }

    protected abstract void onBindItemViewHolder(VH viewHolder, T data, int position, int type);

    protected abstract VH viewHolder(LayoutInflater inflater, ViewGroup parent, int type);


}

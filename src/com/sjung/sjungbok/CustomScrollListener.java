//package com.sjung.sjungbok;
//
//import android.widget.AbsListView;
//
//import java.lang.reflect.Field;
//
//public class CustomScrollListener implements AbsListView.OnScrollListener {
//
//    private int mState = -1;
//    private Field stateField = null;
//    private Object mFastScroller;
//    private int STATE_DRAGGING;
//
//    public CustomScrollListener() {
//        super();
//
//        try {
//            Field fastScrollerField = AbsListView.class.getDeclaredField("mFastScroller");
//            fastScrollerField.setAccessible(true);
//            mFastScroller = fastScrollerField.get(grid);
//            Field stateDraggingField = mFastScroller.getClass().getDeclaredField("STATE_DRAGGING");
//            stateDraggingField.setAccessible(true);
//            STATE_DRAGGING = stateDraggingField.getInt(mFastScroller);
//
//            stateField = mFastScroller.getClass().getDeclaredField("mState");
//            stateField.setAccessible(true);
//            mState = stateField.getInt(mFastScroller);
//
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//        // update fast scroll state
//        try {
//            if (stateField != null) {
//                mState = stateField.getInt(mFastScroller);
//            }
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//
//        if (mState == STATE_DRAGGING) {
//            // the user is fast scrolling through the list
//        }
//    }
//
//    @Override
//    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//        // update fast scroll state
//        try {
//            if (stateField != null) {
//                mState = stateField.getInt(mFastScroller);
//            }
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//    }
//}